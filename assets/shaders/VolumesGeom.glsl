#version 430

layout (triangles_adjacency) in;

in vec4 varyingOffsetDir[];
in vec2 varyingTexCoord[];

layout (triangle_strip, max_vertices=18) out;
//3 for near cap + 3 for far cap + 4 * 3 for sides = 18!

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform vec4 light_pos;  // Light position (set w to zero for directional light)  
uniform float shadow_distance; 

//using the algorithm described here:
//https://developer.nvidia.com/gpugems/gpugems3/part-ii-light-and-shadows/chapter-11-efficient-and-robust-shadow-volumes-using
//I implemented the slower robust algorithm because my models all have holes and self intersections unfortunately :(      

float offset = 0.0001;

void main() {   
    //dont do any shadows for outlines
    if (varyingTexCoord[0].x > 1 || 
        varyingTexCoord[2].x > 1 ||
        varyingTexCoord[4].x > 1){
            return;
    }
    //artifact reduction for z fighting volume caps
    //this is a hack but it works 
    float dist = -((v_matrix * gl_in[0].gl_Position).z);
    offset = clamp(dist*dist * 0.05 * offset,offset,0.03);
    offset += 0.1;
    vec3 ns[3];  // Normals    
    vec3 d[3];  // Directions toward light    
    vec4 v[4];  // Temporary vertices    
    vec4 or_pos[3] = {  // Triangle oriented toward light source      
        gl_in[0].gl_Position + (varyingOffsetDir[0]*offset),      
        gl_in[2].gl_Position + (varyingOffsetDir[2]*offset),      
        gl_in[4].gl_Position + (varyingOffsetDir[4]*offset)    
    };    
    // Compute normal at each vertex.    
    ns[0] = cross(      
        gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz,      
        gl_in[4].gl_Position.xyz - gl_in[0].gl_Position.xyz );    
    ns[1] = cross(      
        gl_in[4].gl_Position.xyz - gl_in[2].gl_Position.xyz,      
        gl_in[0].gl_Position.xyz - gl_in[2].gl_Position.xyz );    
    ns[2] = cross(      
        gl_in[0].gl_Position.xyz - gl_in[4].gl_Position.xyz,      
        gl_in[2].gl_Position.xyz - gl_in[4].gl_Position.xyz );    
    // Compute direction from vertices to light.    
    d[0] = light_pos.xyz-light_pos.w*gl_in[0].gl_Position.xyz;    
    d[1] = light_pos.xyz-light_pos.w*gl_in[2].gl_Position.xyz;    
    d[2] = light_pos.xyz-light_pos.w*gl_in[4].gl_Position.xyz;    
    // Check if the main triangle faces the light.    
    bool faces_light = true;    
    if ( !(dot(ns[0],d[0])>0 || dot(ns[1],d[1])>0 ||           
        dot(ns[2],d[2])>0) ) {  
        // Flip vertex winding order in or_pos.      
        or_pos[1] = gl_in[4].gl_Position + (varyingOffsetDir[4]*offset);      
        or_pos[2] = gl_in[2].gl_Position + (varyingOffsetDir[2]*offset);    
		vec3 temp = d[1];
		d[1] = d[2];
		d[2] = temp;  
        faces_light = false;   
    }   
    // Render caps. This is only needed for z-fail.    
    gl_Position = p_matrix * v_matrix * or_pos[0];      
    EmitVertex();      
    gl_Position = p_matrix * v_matrix * or_pos[1];      
    EmitVertex();      
    gl_Position = p_matrix * v_matrix * or_pos[2];      
    EmitVertex(); 
    EndPrimitive();      
    // Far cap: extrude positions to infinity. 
	//I have 0 clue why this works     
    // v[0] =vec4(light_pos.w*or_pos[0].xyz-light_pos.xyz,0);      
    // v[1] =vec4(light_pos.w*or_pos[2].xyz-light_pos.xyz,0);      
    // v[2] =vec4(light_pos.w*or_pos[1].xyz-light_pos.xyz,0);      
	v[0] =vec4(or_pos[0].xyz - (normalize(d[0])*shadow_distance),1);      
    v[1] =vec4(or_pos[2].xyz - (normalize(d[2])*shadow_distance),1);      
    v[2] =vec4(or_pos[1].xyz - (normalize(d[1])*shadow_distance),1);  
    gl_Position = p_matrix * v_matrix * v[0];      
    EmitVertex();      
    gl_Position = p_matrix * v_matrix * v[1];      
    EmitVertex();      
    gl_Position = p_matrix * v_matrix * v[2];      
    EmitVertex(); 
    EndPrimitive();  
    // Loop over all edges and extrude if needed.    
    for ( int i=0; i<3; i++ ) {      
        // Compute indices of neighbor triangle.    
        int v0 = i*2;      
        int nb = (i*2+1);      
        int v1 = (i*2+2) % 6;      
        // Compute normals at vertices, the *exact*    
        // same way as done above!      
        ns[0] = cross(        
            gl_in[nb].gl_Position.xyz-gl_in[v0].gl_Position.xyz,        
            gl_in[v1].gl_Position.xyz-gl_in[v0].gl_Position.xyz);      
        ns[1] = cross(        
            gl_in[v1].gl_Position.xyz-gl_in[nb].gl_Position.xyz,        
            gl_in[v0].gl_Position.xyz-gl_in[nb].gl_Position.xyz);      
        ns[2] = cross(        
            gl_in[v0].gl_Position.xyz-gl_in[v1].gl_Position.xyz,        
            gl_in[nb].gl_Position.xyz-gl_in[v1].gl_Position.xyz);      
        // Compute direction to light, again as above.      
        d[0] =light_pos.xyz-light_pos.w*gl_in[v0].gl_Position.xyz;      
        d[1] =light_pos.xyz-light_pos.w*gl_in[nb].gl_Position.xyz;      
        d[2] =light_pos.xyz-light_pos.w*gl_in[v1].gl_Position.xyz;      
        // Extrude the edge if it does not have a    
        // neighbor, or if it's a possible silhouette.    
        if ( gl_in[nb].gl_Position.x > 1e16 ||           
            ( faces_light != (dot(ns[0],d[0])>0 ||                             
            dot(ns[1],d[1])>0 ||                             
            dot(ns[2],d[2])>0) )){        
            // Make sure sides are oriented correctly.    
            int i0 = faces_light ? v0 : v1;        
            int i1 = faces_light ? v1 : v0;  
			vec4 pos0 = gl_in[i0].gl_Position + (varyingOffsetDir[i0]*offset);
			vec4 pos1 = gl_in[i1].gl_Position + (varyingOffsetDir[i1]*offset);
            vec3 dir0 = faces_light ? d[0] : d[2];
            vec3 dir1 = faces_light ? d[2] : d[0];
            v[0] = pos0;        
            //v[1] = vec4(light_pos.w*pos0.xyz - light_pos.xyz, 0); 
			v[1] = vec4(pos0.xyz - (normalize(dir0)*shadow_distance),1);       
            v[2] = pos1;        
            //v[3] = vec4(light_pos.w*pos1.xyz - light_pos.xyz, 0); 
			v[3] = vec4(pos1.xyz - (normalize(dir1)*shadow_distance),1);        
            // Emit a quad as a triangle strip.        
            gl_Position = p_matrix * v_matrix * v[0];        
            EmitVertex();        
            gl_Position = p_matrix * v_matrix * v[1];        
            EmitVertex();        
            gl_Position = p_matrix * v_matrix * v[2];        
            EmitVertex();        
            gl_Position = p_matrix * v_matrix * v[3];        
            EmitVertex(); 
            EndPrimitive();      
        }   
    } 
}


//hello world geometry shader v

// void main(void) {
// 	for (int i = 0; i < 3; i++){
// 		gl_Position = p_matrix * v_matrix * gl_in[i*2].gl_Position;
// 		//varyingNormalG = varyingNormal[i*2];
// 		EmitVertex();
// 	}
// 	EndPrimitive();

// 	for (int i = 0; i < 3; i++){
// 		gl_Position = p_matrix * v_matrix * (gl_in[i*2].gl_Position + vec4(0f,1f,0f,0f));
// 		//varyingNormalG = varyingNormal[i*2];
// 		EmitVertex();
// 	}
// 	EndPrimitive();
// }