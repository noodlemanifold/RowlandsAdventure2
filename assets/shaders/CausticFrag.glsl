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

uniform vec4 globalAmbient;
uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform int envMapped;
uniform int has_texture;
uniform int tileCount;
uniform int heightMapped;
uniform int hasLighting;
uniform int solidColor;
uniform vec3 color;
uniform int num_lights;
uniform int fields_per_light;
uniform float time;

layout (std430, binding=0) buffer lightBuffer { float lightArray[]; };
layout (binding = 0) uniform sampler2D samp;
layout (binding = 1) uniform samplerCube t;
layout (binding = 2) uniform sampler2D height;
layout (binding = 3) uniform sampler2D samp2;

vec3 lightDir, L, N, V, R, ambient, diffuse, specular, thisAmbient, thisDiffuse, thisSpecular;
float cosTheta, cosPhi, intensity, attenuationFactor, dist;
int i,f;
vec4 tcolor;


vec3 estimateNormal(float offset, float heightScale)
{	// this algorithm is from Program 15.4
	float h1 = heightScale * texture(height, vec2(tc.s, tc.t+offset)).r;
	float h2 = heightScale * texture(height, vec2(tc.s-offset, tc.t-offset)).r;
	float h3 = heightScale * texture(height, vec2(tc.s+offset, tc.t-offset)).r;
	vec3 v1 = vec3(0, h1, -1);
	vec3 v2 = vec3(-1, h2, 1);
	vec3 v3 = vec3(1, h3, 1);
	vec3 v4 = v2-v1;
	vec3 v5 = v3-v1;
	vec3 normEst = normalize(cross(v4,v5));
	return normEst;
}

void calcPositionalLight()
{	thisDiffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta,0.0);
	thisSpecular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi,0.0), material.shininess);
}

void calcSpotLight()
{	// compute the angle between the spotlight direction and the direction to this pixel
	float cosAngle = abs(dot(-L, normalize((light.direction).xyz)));
	float angleD = degrees(acos(cosAngle));
	
	// compute the intensity factor of the light based on the angle
	if (angleD > light.cutoffAngle)
		intensity = 0.0;
	else
		intensity = pow(cosAngle, light.offAxisExponent);

	thisDiffuse = intensity * light.diffuse.xyz * material.diffuse.xyz * max(cosTheta,0.0);
	thisSpecular = intensity * light.specular.xyz * material.specular.xyz * pow(max(cosPhi,0.0), material.shininess);
}

vec4 calcCaustics(){
	float brightness = 1.3;
	float scale1 = 5;
	float scale2 = 3.5;
	vec2 offset1 = vec2(0.1+(time*0.1),0.7+(time*0.12));
	vec2 offset2 = vec2(0.4+(time*-0.06),0.8+(time*-0.1));
	vec4 ccolor1 = texture(samp2, vec2(mod(varyingVertPos.x+offset1.x,scale1)/scale1,mod(varyingVertPos.z+offset1.y,scale1)/scale1));
	vec4 ccolor2 = texture(samp2, vec2(mod(varyingVertPos.x+offset2.x,scale2)/scale2,mod(varyingVertPos.z+offset2.y,scale2)/scale2));
	return min(ccolor1,ccolor2) *clamp(dot(vec3(0,1,0),normalize(varyingNormal)),0.0,1.0)*brightness;
}

void main(void)
{	f = fields_per_light;
	for (i=0; i<num_lights; i++)
	{	light.position = vec3(lightArray[i*f+0], lightArray[i*f+1], lightArray[i*f+2]);
		lightDir = light.position - varyingVertPos;

		// normalize the light, normal, and view vectors:
		L = normalize(lightDir);
		V = normalize(-v_matrix[3].xyz - varyingVertPos);

		if (heightMapped == 1)
			N = estimateNormal(.005, 5.0);
		else
			N = normalize(varyingNormal);
	
		// compute light reflection vector, with respect N:
		R = normalize(reflect(-L, N));
	
		// get the angle between the light and surface normal:
		cosTheta = dot(L,N);
	
		// angle between the view vector and reflected light:
		cosPhi = dot(V,R);

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

		// compute ADS contributions (per pixel):
		thisAmbient = (globalAmbient + (light.ambient * material.ambient)).xyz;
		ambient = max(ambient, thisAmbient);

		if (light.type == 0.0)
			calcPositionalLight();
		else
			calcSpotLight();

		dist = distance(varyingVertPos, light.position);
		attenuationFactor = 1.0 / (light.constantAttenuation + light.linearAttenuation*dist + light.quadraticAttenuation*dist*dist);

		diffuse = min(vec3(1,1,1), diffuse + attenuationFactor * thisDiffuse);
		specular = min(vec3(1,1,1), specular + attenuationFactor * thisSpecular);
	}

	if (hasLighting == 0)
	{	if (solidColor == 1)
		{	fragColor = vec4(color, 1.0);
		}
		else if (envMapped == 1)
		{	vec3 r = -reflect(normalize(-vVertPos), normalize(varyingNormal));
			fragColor = texture(t,r);
		}
		else
		{	fragColor = texture(samp,tc);
		}
	}
	else // has lighting
	{	
		vec4 colorO = vec4(1.0,1.0,1.0,1.0);
		if (solidColor == 1)
		{	tcolor = vec4(color, 1.0);
			colorO = min((tcolor * vec4((ambient + diffuse),1.0) + vec4(specular,0.0)) + calcCaustics(), vec4(1,1,1,1));
		}
		else if (envMapped == 1)
		{	vec3 r = -reflect(normalize(-vVertPos), normalize(varyingNormal));
			tcolor = texture(t,r);
			colorO = min((tcolor * vec4((ambient + diffuse),1.0) + vec4(specular,0.0)) + calcCaustics(), vec4(1,1,1,1));
		}
		else if (has_texture == 0)
		{	colorO = min(0.5 * vec4((ambient + diffuse + specular), 1.0) + calcCaustics(), vec4(1,1,1,1));
		}
		else
		{	
			tcolor = texture(samp, tc);
			colorO = min((tcolor * vec4((ambient + diffuse),1.0) + vec4(specular,0.0)), vec4(1,1,1,1));
		}
		float dist = -vVertPos.z;
		float t = clamp(dist * 0.02,0.0,0.7);
		colorO = mix(colorO,vec4(1.0,1.0,1.0,1.0),calcCaustics().r);
		fragColor = mix(colorO,vec4(0.0,0.25,0.5,1.0),t);
		//fragColor = texture(samp2, vec2(fract(varyingVertPos.x),fract(varyingVertPos.z)));
	}
}
