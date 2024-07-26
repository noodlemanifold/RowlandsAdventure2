#version 430

in vec2 tc;
in vec3 varyingNormal;
in vec3 varyingNormalFlat;
in vec3 varyingTangent;
in vec3 vVertPos;
in vec3 varyingVertPos;

out vec4 fragColor;

struct Light
{	vec4 ambient;  
	vec4 diffuse;  
	vec4 specular;  
	vec3 position;
	float constantAttenuation;
	float linearAttenuation;
	float quadraticAttenuation;
	float range;
	vec3 direction;
	float cutoffAngle;
	float offAxisExponent;
	float type;
};

struct Material
{	vec4 ambient;  
	vec4 diffuse;  
	vec4 specular;  
	float shininess;
};

Light light;

uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform int has_texture;
uniform int tileCount;
uniform int heightMapped;
uniform float heightScale;
uniform int hasLighting;
uniform int solidColor;
uniform vec3 color;
uniform int num_lights;
uniform int fields_per_light;
uniform vec3 surf_color_1;
uniform vec3 surf_color_2;
uniform vec3 surf_color_3;
uniform vec3 surf_color_4;
uniform float shadow;

layout (std430, binding=0) buffer lightBuffer { float lightArray[]; };
layout (binding = 0) uniform sampler2D surface_map;
layout (binding = 1) uniform sampler2D tex_2;
layout (binding = 2) uniform sampler2D height;
layout (binding = 3) uniform sampler2D surface1;
layout (binding = 4) uniform sampler2D surface2;
layout (binding = 5) uniform sampler2D surface3;
layout (binding = 6) uniform sampler2D surface4;
layout (binding = 7) uniform sampler2D ramp;

vec3 lightDir, L, N, N2, V, R;
float cosTheta, cosPhi, intensity, attenuationFactor, dist, rampFac, brightness;
int i,f;
vec4 tcolor;

vec3 calcTriplanarColor(sampler2D samp){
	vec3 test_normal = abs(normalize(N2));
	float xw = dot(vec3(1,0,0),test_normal);
	xw = 1-clamp((acos(xw) - 0.6)*2.5,0,1);
	float yw = dot(vec3(0,1,0),test_normal);
	yw = 1-clamp((acos(yw) - 0.6)*2.5,0,1);
	float zw = dot(vec3(0,0,1),test_normal);
	zw = 1-clamp((acos(zw) - 0.6)*2.5,0,1);

	vec3 xcol = vec3(1,1,1);
	if (xw > 0) xcol = texture(samp,vec2(varyingVertPos.y,varyingVertPos.z)/4).rgb;
	vec3 ycol = vec3(1,1,1);
	if (yw > 0) ycol = texture(samp,vec2(varyingVertPos.z,varyingVertPos.x)/4).rgb;
	vec3 zcol = vec3(1,1,1);
	if (zw > 0) zcol = texture(samp,vec2(varyingVertPos.x,varyingVertPos.y)/4).rgb;

	float sum = xw+yw+zw;
	xw/=sum;
	yw/=sum;
	zw/=sum;

	// vec3 xcol = texture(samp,vec2(varyingVertPos.z,varyingVertPos.y)).rgb;
	// vec3 ycol = texture(samp,vec2(varyingVertPos.x,varyingVertPos.z)).rgb;
	// vec3 zcol = texture(samp,vec2(varyingVertPos.x,varyingVertPos.y)).rgb;

	return (xcol * xw)  + (ycol * yw) + (zcol * zw);
}

vec3 calcSurfaceColor(){
	vec4 mapVal = texture(surface_map, tc);
	mapVal.rgb *= mapVal.a;//blend alpha biome properly
	mapVal.a = 1-mapVal.a;

	//return calcTriplanarColor(surface1,norm);

	//i really hope branching is less slow than texture lookups
	vec3 surfa = vec3(0,0,0);
	if (mapVal.r > 0) surfa = calcTriplanarColor(surface1) * surf_color_1;
	vec3 surfb = vec3(0,0,0);
	if (mapVal.g > 0) surfb = calcTriplanarColor(surface2) * surf_color_2;
	vec3 surfc = vec3(0,0,0);
	if (mapVal.b > 0) surfc = calcTriplanarColor(surface3) * surf_color_3;
	vec3 surfd = vec3(0,0,0);
	if (mapVal.a > 0) surfd = calcTriplanarColor(surface4) * surf_color_4;

	float sum = mapVal.r + mapVal.g + mapVal.b + mapVal.a;
	mapVal *= sum;
	//naive color interpolation will do
	vec3 surf = (surfa * mapVal.r) + (surfb * mapVal.g) + (surfc * mapVal.b) + (surfd * mapVal.a);
	return surf;
}

vec2 calcPositionalLight()
{	
	float spec = material.specular.r * pow(max(cosPhi,0.0), /*material.shininess*/45);
	float angle = acos(cosTheta);
	float diff = 1-(angle/3.14);
	float angleFactor = clamp(dot(L,N2),0,1);
	angleFactor = 1-angleFactor;
	angleFactor *= 0.25;
	return vec2(clamp(diff-angleFactor,0,1),spec);
}

vec2 calcDirectional(){
	vec2 lighting = calcPositionalLight();//math actually comes out the same!
	lighting.x = clamp(lighting.x,0.1,1.0);//lil ambient light so shadows pop
	lighting *= shadow;
	return lighting;
}

vec2 calcSpotLight()
{	// compute the angle between the spotlight direction and the direction to this pixel
	float cosAngle = -(dot(-L, normalize((light.direction).xyz)));
	float angleD = degrees(acos(cosAngle));
	
	// compute the intensity factor of the light based on the angle
	if (angleD > light.cutoffAngle)
		intensity = 0.0;
	else
		intensity = pow(cosAngle, light.offAxisExponent);

	float angle = acos(cosTheta);
	float diff = intensity*(1-(angle/3.14))*light.diffuse.r;
	float spec = intensity*material.specular.r * pow(max(cosPhi,0.0), /*material.shininess*/45);
	return vec2(diff,spec);
}

void main(void)
{	f = fields_per_light;
	brightness = 0;
	for (i=0; i<num_lights; i++)
	{	light.position = vec3(lightArray[i*f+0], lightArray[i*f+1], lightArray[i*f+2]);
		lightDir = light.position - varyingVertPos;

		light.ambient = vec4(lightArray[i*f+3], lightArray[i*f+4], lightArray[i*f+5], 1.0);
		light.diffuse = vec4(lightArray[i*f+6], lightArray[i*f+7], lightArray[i*f+8], 1.0);
		light.specular = vec4(lightArray[i*f+9], lightArray[i*f+10], lightArray[i*f+11], 1.0);
		light.constantAttenuation = lightArray[i*f+12];
		light.linearAttenuation = lightArray[i*f+13];
		light.quadraticAttenuation = lightArray[i*f+14];
		light.range = lightArray[i*f+15];
		light.direction = vec3(lightArray[i*f+16], lightArray[i*f+17], lightArray[i*f+18]);
		light.cutoffAngle = lightArray[i*f+19];
		light.offAxisExponent = lightArray[i*f+20];
		light.type = lightArray[i*f+21];

		// normalize the light, normal, and view vectors:
		if (light.type == 2.0)
			L = normalize(light.direction);
		else
			L = normalize(lightDir);

		V = normalize(-v_matrix[3].xyz - varyingVertPos);

		N = normalize(varyingNormalFlat);
		N2 = normalize(varyingNormal);
		// compute light reflection vector, with respect N:
		R = normalize(reflect(-L, N));
	
		// get the angle between the light and surface normal:
		cosTheta = dot(L,N);
	
		// angle between the view vector and reflected light:
		cosPhi = dot(V,R);

		vec2 lighting = vec2(0,0);

		if (light.type == 0.0)
			lighting = calcPositionalLight();
		else if(light.type == 1.0)
			lighting = calcSpotLight();
		else 
			lighting = calcDirectional();

		dist = distance(varyingVertPos, light.position);
		attenuationFactor = 1.0 / (light.constantAttenuation + light.linearAttenuation*dist + light.quadraticAttenuation*dist*dist);
		attenuationFactor = clamp(attenuationFactor,0,1);
		lighting *= attenuationFactor;

		//disable planet specular
		lighting.y = 0;

		brightness = max(brightness, lighting.x+lighting.y);
		brightness = clamp(brightness,0,1);
	}

	rampFac = brightness;

	if (hasLighting == 0)
	{	if (solidColor == 1)
		{	fragColor = vec4(color, 1.0);
		}
		else
		{	fragColor = vec4(calcSurfaceColor(),1.0);
		}
	}
	else {	
		tcolor = vec4(1,1,1,1);
		if (solidColor == 1)
		{	tcolor = vec4(color, 1.0);
		}
		else if (has_texture == 1)
		{	tcolor = vec4(calcSurfaceColor(),1.0);
		}

		fragColor = texture(ramp,vec2(brightness,0.5)) * tcolor;
	}
}
