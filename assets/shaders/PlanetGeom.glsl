#version 430

layout (triangles_adjacency) in;

in vec2 tcG[];
in vec3 varyingNormalG[];
in vec3 varyingTangentG[];
in vec3 varyingVertPosG[];
in vec3 vVertPosG[];

out vec2 tc;
out vec3 varyingNormal;
out vec3 varyingNormalFlat;
out vec3 varyingTangent;
out vec3 varyingVertPos;
out vec3 vVertPos;

layout (triangle_strip, max_vertices=3) out;

//calculate heightmapped terrain normals
//I couldnt get an approximation function to look good :/

void main(void) {
    vec3 a1 = varyingVertPosG[1] - varyingVertPosG[0];
    vec3 b1 = varyingVertPosG[2] - varyingVertPosG[0];
    vec3 n1 = cross(a1,b1);
    vec3 a2 = varyingVertPosG[3] - varyingVertPosG[2];
    vec3 b2 = varyingVertPosG[4] - varyingVertPosG[2];
    vec3 n2 = cross(a2,b2);
    vec3 a3 = varyingVertPosG[5] - varyingVertPosG[4];
    vec3 b3 = varyingVertPosG[0] - varyingVertPosG[4];
    vec3 n3 = cross(a3,b3);
    vec3 a4 = varyingVertPosG[2] - varyingVertPosG[0];
    vec3 b4 = varyingVertPosG[4] - varyingVertPosG[0];
    vec3 n4 = cross(a4,b4);
    vec3 n = (n1+n2+n3+n4)/4;
	for (int i = 0; i < 3; i++){
		gl_Position = gl_in[i*2].gl_Position;
        tc = tcG[i*2];
		varyingNormalFlat = varyingNormalG[i*2];
        varyingNormal = normalize(n);
        varyingTangent = varyingTangentG[i*2];
        varyingVertPos = varyingVertPosG[i*2];
        vVertPos = vVertPosG[i*2];
		EmitVertex();
	}
	EndPrimitive();
}