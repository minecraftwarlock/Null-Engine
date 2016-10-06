#version 150 core

in vec2 texCoords;
in vec3 normal;
in vec4 position;

in vec3 cameraDirection;

out vec4 outColor;
out vec4 outPosition;
out vec4 outNormal;
out vec4 outSpecular;

uniform sampler2D aTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform vec4 reflectivity;
uniform vec4 shineDamper;
uniform vec4 lightingAmount;
uniform sampler2D blend;
uniform float tileCount;

uniform sampler2D normals;

void main() {
	vec4 blendColor = texture(blend, texCoords);
	float aTextureAmt = clamp(1 - (blendColor.r + blendColor.g + blendColor.b), 0, 1);
	vec2 tiled = texCoords * tileCount;
	vec4 aColor = texture(aTexture, tiled) * aTextureAmt;
	vec4 rColor = texture(rTexture, tiled) * blendColor.r;
	vec4 gColor = texture(gTexture, tiled) * blendColor.g;
	vec4 bColor = texture(bTexture, tiled) * blendColor.b;

	float reflectivity0 = reflectivity.a * aTextureAmt + reflectivity.r * blendColor.r + reflectivity.g * blendColor.g + reflectivity.b * blendColor.b;
	float shineDamper0 = shineDamper.a * aTextureAmt + shineDamper.r * blendColor.r + shineDamper.g * blendColor.g + shineDamper.b * blendColor.b;
	float lightingAmount0 = lightingAmount.a * aTextureAmt + lightingAmount.r * blendColor.r + lightingAmount.g * blendColor.g + lightingAmount.b * blendColor.b;

	outColor = aColor + rColor + gColor + bColor;
	outPosition = position;
	outNormal = vec4(normal, 1);
	outSpecular = vec4(reflectivity0, shineDamper0, lightingAmount0, 1);
}
