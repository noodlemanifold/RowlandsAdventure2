#version 430

in vec2 tc;
in vec3 varyingNormal;
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
uniform int envMapped;
uniform int has_texture;
uniform int has_texture2;
uniform int tileCount;
uniform int heightMapped;
uniform float heightScale;
uniform int hasLighting;
uniform int solidColor;
uniform vec3 color;
uniform int num_lights;
uniform int fields_per_light;
uniform float shadow;
uniform float planet_light;

layout (std430, binding=0) buffer lightBuffer { float lightArray[]; };
layout (binding = 0) uniform sampler2D samp;
layout (binding = 1) uniform samplerCube t;
layout (binding = 3) uniform sampler2D samp2;
layout (binding = 4) uniform sampler2D ramp;
layout (binding = 5) uniform sampler2D planetRamp;

vec3 lightDir, L, N, V, R;
float cosTheta, cosPhi, intensity, attenuationFactor, dist, rampFac, planetRampFac, brightness;
int i,f;
vec4 tcolor;
vec4 t2color;

vec2 calcPositionalLight()
{	
	float spec = material.specular.r * pow(max(cosPhi,0.0), /*material.shininess*/45);
	float angle = acos(cosTheta);
	float diff = 1-(angle/3.14);
	return vec2(diff,spec);
}

vec2 calcDirectional(){
	vec2 lighting = calcPositionalLight();//math actually comes out the same!
	lighting *= shadow;
	lighting.y = 0.0;//directional specular just didnt look good
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

		N = normalize(varyingNormal);
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

		//lighting = calcDirectional();

		dist = distance(varyingVertPos, light.position);
		attenuationFactor = 1.0 / (light.constantAttenuation + light.linearAttenuation*dist + light.quadraticAttenuation*dist*dist);
		attenuationFactor = clamp(attenuationFactor,0,1);
		lighting *= attenuationFactor;

		//make specular highlight sharper
		lighting.y = pow(lighting.y,2);

		brightness = max(brightness, lighting.x+lighting.y);
		brightness = clamp(brightness,0,1);
	}

	rampFac = brightness;

	if (hasLighting == 0)
	{	if (solidColor == 1)
		{	fragColor = vec4(color, 1.0);
		}
		else
		{	fragColor = texture(samp,tc);
		}
	}
	else {	
		tcolor = vec4(1,1,1,1);
		t2color = vec4(0,0,0,0);
		if (solidColor == 1)
		{	tcolor = vec4(color, 1.0);
		}
		else if (has_texture == 1)
		{	tcolor = texture(samp, tc);
			if (has_texture2 == 1){
				t2color = texture(samp2,tc);
				tcolor = (tcolor * (1-t2color.a)) + (t2color * t2color.a);
				tcolor.a = 1;
			}
		}

		planetRampFac = clamp(planet_light,0.1,1.0);
		vec4 litColor = texture(ramp,vec2(rampFac,0.5)) * texture(planetRamp,vec2(planetRampFac,0.5)) * tcolor;
		fragColor = mix(litColor, tcolor, t2color.a);
		//fragColor = vec4(shadow.rrr,1);
	}
}
