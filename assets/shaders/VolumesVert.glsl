#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tex_coord;
layout (location = 2) in vec3 vertNormal;

out vec4 varyingOffsetDir;
out vec2 varyingTexCoord;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform vec4 cam_position;
uniform vec4 light_pos;

void main(void){	
	vec4 worldPos = m_matrix * vec4(position,1.0);
	varyingOffsetDir = vec4(normalize(worldPos.xyz-cam_position.xyz),0);
	varyingTexCoord = tex_coord;
	if (position.x < 1e36){
		gl_Position = worldPos;
	}else{
		gl_Position = vec4(position,1.0);
	}
}