#version 150

uniform sampler2D uNoiseSampler;
uniform sampler2D uDepthSampler;

uniform vec4 uFogData;
uniform vec4 uNoiseData;
uniform vec4 uFogBaseColor;

uniform vec3 uCameraPosition;

uniform mat4 uInverseViewMatrix;
uniform mat4 uInverseProjectionMatrix;

#define uTerrainDensity uFogData.x
#define uSkyDensity uFogData.y
#define uFadeTransition uFogData.z
#define uNearClarity uFogData.w

#define uNoiseOffset uNoiseData.xy
#define uNoiseAmplifier uNoiseData.z
#define uNoiseScale uNoiseData.w

in vec2 texCoord;
out vec4 fragColor;

vec3 getWorldPosition(vec2 fragPos, float depth) {
    vec3 ndc = vec3(fragPos, depth) * 2.0 - 1.0;
    vec4 viewPos = uInverseProjectionMatrix * vec4(ndc, 1.0);
    viewPos /= viewPos.w;
    return (uInverseViewMatrix * viewPos).xyz;
}

#define DEBUG 0

void main() {
    vec4 fogColor = uFogBaseColor;
    float depth = texture(uDepthSampler, texCoord).r;

    vec3 relativePos = getWorldPosition(texCoord, depth);
    vec3 worldPos = relativePos + uCameraPosition;
    float noise = texture(uNoiseSampler, (worldPos.xz + uNoiseOffset) * uNoiseScale).r;

    float offsetY = (noise - 0.5) * uNoiseAmplifier;
    float distance2cam = length(relativePos + vec3(0.0, offsetY, 0.0));

    float isTerrain = step(depth, 1.0 - 1e-6);

    // terrain fog
    float density = uTerrainDensity;
    float d = max(distance2cam, 0.0);

    float fogFactor = 1.0 - exp(-d * d * density * density);

    float halfFadeTransition = uFadeTransition * 0.5;
    float start = max(64.0 - uTerrainDensity * 128.0 - halfFadeTransition, 0.0);
    float dark = start + uFadeTransition + halfFadeTransition;
    float darkness = smoothstep(dark, start, distance2cam) * smoothstep(0.3, 0.5, fogFactor);

    vec4 terrainFog = fogColor;
    terrainFog.rgb *= 0.95 - min(uNearClarity, 0.8) * darkness;
    terrainFog.a *= fogFactor;

    // sky fog
    vec4 skyFog = fogColor;
    float up = max(dot(normalize(relativePos), vec3(0.0, 1.0, 0.0)), 0.0);
    float skyFogFactor = 1.0 - up * (1.0 - uSkyDensity);
    skyFog.rgb *= mix(0.95, 1.0, up);
    skyFog.a *= skyFogFactor;

    #if DEBUG == 1
    fragColor = vec4(vec3(darkness), 1.0);
    #else
    fragColor = mix(skyFog, terrainFog, isTerrain);
    fragColor.a *= fogFactor;
    #endif
}