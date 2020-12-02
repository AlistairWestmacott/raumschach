#version 330 core

// Input vertex data, different for all executions of this shader.
layout(location = 0) in vec3 vertexPosition_m;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 vertexNormal_m;

out vec2 UV;
out vec3 barycentric;
out vec3 fragNormalV;
out vec3 fragPosV;

out vec3 cameraPosV;
out vec3 lightPosV;

out vec3 triangleColour;

uniform vec3 cameraV;
uniform vec3 lightV;

uniform mat4 MVP;
uniform mat4 MV;

uniform mat3 normalMVP;

void main(){

    // Output position of the vertex, in clip space : MVP * position
    gl_Position =  MVP * vec4(vertexPosition_m, 1);

    vec4 fragNormalHomoW = MV * vec4(vertexNormal_m, 0);
    vec4 fragPosHomoW = MV * vec4(vertexPosition_m, 1);

    int triangleIndex = gl_VertexID;
    triangleIndex = triangleIndex % 12;

    float triR = 0, triG = 0, triB = 0;
    if (triangleIndex < 3 || triangleIndex > 9) {
        triR = 1;
    } else if (triangleIndex < 4 || triangleIndex > 8) {
        triR = 0.5;
    }
    if (triangleIndex < 7 && triangleIndex > 1) {
        triG = 1;
    } else if (triangleIndex < 8 && triangleIndex > 0) {
        triG = 0.5;
    }
    if (triangleIndex < 10 && triangleIndex > 5) {
        triB = 1;
    } else if (triangleIndex > 4) {
        triB = 0.5;
    }
    triangleColour = vec3(triR, triG, triB);

    fragNormalV = fragNormalHomoW.xyz;
    fragPosV = fragPosHomoW.xyz;

    // UV of the vertex. No special space for this one.
    UV = vertexUV;

    barycentric = vec3(0);

    cameraPosV = cameraV;
    lightPosV = lightV;

}