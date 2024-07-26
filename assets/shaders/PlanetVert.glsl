#version 430

layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertNormal;
layout (location = 3) in vec3 vertTangent;

out vec2 tcG;
out vec3 varyingNormalG;
out vec3 varyingTangentG;
out vec3 varyingVertPosG;
out vec3 vVertPosG;

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
uniform float heightScale;
uniform int hasLighting;
uniform int solidColor;
uniform vec3 color;
uniform int num_lights;
uniform int fields_per_light;

layout (std430, binding=0) buffer lightBuffer { float lightArray[]; };
layout (binding = 0) uniform sampler2D samp;
layout (binding = 1) uniform samplerCube t;
layout (binding = 2) uniform sampler2D height;

void main(void){

	vec3 normal = normalize((norm_matrix * vec4(vertNormal,1.0)).xyz);
	varyingNormalG = normal;
	//https://stackoverflow.com/questions/35092885/transform-normal-and-tangent-from-object-space-to-world-space
	//This guy said use model matrix!!
	varyingTangentG = normalize((m_matrix * vec4(vertTangent,1.0)).xyz);
	//varyingTangent = vertTangent;
	

	vec3 worldPos = (m_matrix * vec4(vertPos,1.0)).xyz;
	vec4 p = vec4(worldPos + normal * (texture(height,texCoord)).r * heightScale,1.0);

	// Compute the texture coordinates depending on the specified tileFactor
	tcG = texCoord;
	tcG = tcG * tileCount;
	
	gl_Position = p_matrix * v_matrix * p;
	vVertPosG = (v_matrix * p).xyz;
	varyingVertPosG = (p).xyz;
}
