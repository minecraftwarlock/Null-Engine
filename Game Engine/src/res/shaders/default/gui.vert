#version 150 core

in vec3 inPosition;
in vec2 inTexCoords;

out vec2 texCoords;

uniform mat4 mvp;

uniform vec2 pos;
uniform vec2 size;

void main() {
	gl_Position = mvp * vec4(inPosition.xy * size + pos, 0, 1);
	texCoords = inTexCoords;
}
