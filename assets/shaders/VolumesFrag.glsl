#version 430

//in vec3 varyingNormalG;

out vec4 color;

layout (binding=0) uniform sampler2D tex;

void main(void){
	//color = vec4(varyingNormalG,1f);
	color = vec4(1f,1f,1f,1f);
}
