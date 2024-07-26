#version 430

struct InData
{
    vec4 pos;
    vec4 norm;
    vec4 uv;
};

struct OutData
{
    vec4 pos;
};

layout (local_size_x=1) in;
layout(binding=0) buffer inputBuffer { 
    InData inData[]; 
};
layout(binding=1) buffer outputBuffer { 
    OutData outData[]; 
};

uniform float height;
uniform mat4 s_matrix;
layout (binding=0) uniform sampler2D samp;

void main()
{	
    uint id = gl_WorkGroupID.x;
    vec4 scaledPos = s_matrix * vec4(inData[id].pos.xyz,1.0);
    outData[id].pos = vec4(scaledPos.xyz + (normalize(inData[id].norm.xyz)*height * texture(samp, inData[id].uv.xy).r),0);
}