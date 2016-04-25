#version 150 core

in vec2 texCoords;

out vec4 outColor;

uniform sampler2D sampler;

uniform vec4 color;
uniform vec4 borderColor;

uniform vec2 thickness;
uniform vec2 borderThickness;

uniform vec2 borderOffset;

void main() {
	float distance = 1 - texture(sampler, texCoords).a;
	float alpha = 1 - smoothstep(thickness.x, thickness.x + thickness.y, distance);

	float borderDistance = 1 - texture(sampler, texCoords + borderOffset).a;
	float borderAlpha = 1 - smoothstep(borderThickness.x, borderThickness.x + borderThickness.y, borderDistance);

	float overallAlpha = alpha + (1 - alpha) * borderAlpha;
	vec4 overallColor = mix(borderColor, color, alpha / overallAlpha);

	outColor = vec4(overallColor.rgb, overallAlpha * overallColor.a);
}
