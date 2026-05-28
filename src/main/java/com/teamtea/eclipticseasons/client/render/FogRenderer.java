package com.teamtea.eclipticseasons.client.render;//package com.teamtea.eclipticseasons.client.render;
//
//import com.mojang.blaze3d.pipeline.RenderTarget;
//import com.mojang.blaze3d.pipeline.TextureTarget;
//import com.mojang.blaze3d.platform.Window;
//import com.mojang.blaze3d.shaders.AbstractUniform;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.*;
//import com.teamtea.eclipticseasons.EclipticSeasons;
//import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
//import com.teamtea.eclipticseasons.api.data.weather.special_effect.FogEffect;
//import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
//import com.teamtea.eclipticseasons.api.util.WeatherUtil;
//import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
//import com.teamtea.eclipticseasons.client.util.ClientCon;
//import com.teamtea.eclipticseasons.config.ClientConfig;
//import net.minecraft.client.Camera;
//import net.minecraft.client.DeltaTracker;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.client.renderer.ShaderInstance;
//import net.minecraft.client.renderer.texture.AbstractTexture;
//import net.minecraft.core.BlockPos;
//import net.minecraft.resources.Identifier;
//import net.minecraft.util.ARGB;
//import net.minecraft.world.level.LightLayer;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.client.event.RegisterShadersEvent;
//import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
//import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
//import net.neoforged.neoforge.event.GameShuttingDownEvent;
//import net.neoforged.neoforge.event.tick.LevelTickEvent;
//import org.joml.Matrix4f;
//import org.joml.Vector2f;
//import org.joml.Vector3f;
//import org.joml.Vector4f;
//import org.lwjgl.opengl.GL30;
//
//import java.io.IOException;
//
///*
// * 如果需要把事件订阅并入 ClientEventHandler
// * 请按需把单例改为静态
// * */
//@Deprecated
//@SuppressWarnings("removal")
//@EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasonsApi.MODID, bus = EventBusSubscriber.Bus.GAME)
//public class FogRenderer {
//
//    public static final FogRenderer INSTANCE = new FogRenderer();
//    static final Identifier NOISE = EclipticSeasons.rl("textures/noise/perlin_256x_r.png");
//
//    private RenderTarget tempTarget;
//    private VertexBuffer quad;
//
//    private float uTerrainFogDensity;
//    private float uSkyFogDensity;
//    private float uFadeTransition;
//    private float uNearClarity;
//    private float mTimer;
//
//    private float uNoiseAmplifier;
//    private float uNoiseScale;
//
//    private final Vector4f uFogColor;
//
//    private final Vector2f mWindDirection;
//    private float mWindSpeed;
//
//    private boolean bufferInitialized = false;
//
//
//    /*
//     * 需要在 render thread 进行初始化, 最好在 Tesselator 初始化后进行
//     * 如果后续需要改为静态类, 最好把那几个final去掉, 然后把这个构造器改为 init()
//     * */
//    FogRenderer() {
//
//        this.mWindDirection = new Vector2f(0.0f, 0.0f);
//        this.uFogColor = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
//
//        this.debugInit();
//    }
//
//    @SubscribeEvent
//    public static void onRenderLevelStage(final RenderLevelStageEvent event) {
//
//        if (Minecraft.getInstance().cameraEntity == null
//                || Minecraft.getInstance().cameraEntity.getEyeInFluidType() != net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value())
//            return;
//        if (!ClientConfig.Debug.fogWeather.get()) return;
//        if (fogDensity <= 0) return;
//
//        RenderLevelStageEvent.Stage stage = event.getStage();
//
//        if (stage == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
//            INSTANCE.prepareBuffer();
//        } else if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
//            INSTANCE.render(event.getCamera(), event.getProjectionMatrix(), event.getPartialTick());
//        }
//    }
//
//    @SubscribeEvent
//    public static void onShutdown(final GameShuttingDownEvent event) {
//        INSTANCE.cleanup();
//    }
//
//    static float fogDensity = 0f;
//
//    @SubscribeEvent
//    public static void onLevelTick(LevelTickEvent.Post event) {
//        if (event.getLevel() instanceof ClientLevel clientLevel) {
//            WeatherEffect effect = WeatherUtil.getWeatherEffectByEntity(ClientCon.agent.getCameraEntity());
//            float target = 0f;
//            if (effect != null && effect.withFog()) {
//                BlockPos containing = BlockPos.containing(ClientCon.agent.getCameraEntity().getEyePosition());
//                target = effect.getFogDensity(clientLevel,containing);
//            }
//            fogDensity += (target - fogDensity) * 0.05f;
//        }
//    }
//
//    /*
//     * 测试的初始化值
//     * */
//    public void debugInit() {
//        this.setFogColor(0.8f, 0.8f, 0.8f, 1.0f);
//        this.setTerrainFogDensity(112f);
//        this.setSkyFogDensity(94.3f);
//        this.setNearClarity(12.0f);
//        this.setFadeTransition(6.0f);
//
//        this.setNoiseAmplifier(32.0f);
//        this.setNoiseScale(0.1f);
//
//        this.setWindSpeed(0.01f);
//        this.setWindDirection(0.0f, 1.0f);
//    }
//
//    public void prepareBuffer() {
//
//        if (this.tempTarget == null) {
//            Window window = Minecraft.getInstance().getWindow();
//            this.tempTarget = new TextureTarget(window.getWidth(), window.getHeight(), true, true);
//        }
//
//        this.tempTarget.clear(Minecraft.ON_OSX);
//        RenderTarget src = Minecraft.getInstance().getMainRenderTarget();
//
//        final int width = src.width;
//        final int height = src.height;
//
//        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, src.frameBufferId);
//        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, tempTarget.frameBufferId);
//
//        GL30.glBlitFramebuffer(
//                0, 0, width, height,
//                0, 0, width, height,
//                GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT,
//                GL30.GL_NEAREST
//        );
//
//        src.bindWrite(false);
//    }
//
//    public void render(Camera camera, final Matrix4f projectionMatrix, DeltaTracker deltaTracker) {
//        this.mTimer += deltaTracker.getGameTimeDeltaTicks() * this.mWindSpeed;
//
//        if (this.mTimer > 256.0) {
//            this.mTimer -= 256.0f;
//        }
//
//        if (this.uTerrainFogDensity < 1e-6 || tempTarget == null) {
//            return;
//        }
//
//        if (!this.bufferInitialized) {
//            this.quad = new VertexBuffer(VertexBuffer.Usage.STATIC);
//
//            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
//            bufferbuilder.addVertex(0.0F, 0.0F, 0.0F);
//            bufferbuilder.addVertex(1.0F, 0.0F, 0.0F);
//            bufferbuilder.addVertex(1.0F, 1.0F, 0.0F);
//            bufferbuilder.addVertex(0.0F, 1.0F, 0.0F);
//
//            this.quad.bind();
//            this.quad.upload(bufferbuilder.buildOrThrow());
//            this.bufferInitialized = true;
//            VertexBuffer.unbind();
//        }
//
//        ShaderInstance instance = Shader.screenFog;
//        final Matrix4f invProjectionMatrix = new Matrix4f(projectionMatrix).invert();
//        final Vector3f position = camera.getPosition().toVector3f();
//
//        // inverse view matrix without translation
//        final Matrix4f invViewMatrix = new Matrix4f()
//                .rotation(camera.rotation());
//
//        Shader.uNoiseData.set(
//                this.mWindDirection.x() * this.mTimer,
//                this.mWindDirection.y() * this.mTimer,
//                this.uNoiseAmplifier,
//                this.uNoiseScale
//        );
//        float rate = 0.5f + 0.5f * ClientCon.getUseLevel().getBrightness(LightLayer.SKY, ClientCon.agent.getCameraEntity().blockPosition()) / 15f;
//        rate *= (ClientWeatherChecker.lastBiomeRainLevel * ClientWeatherChecker.lastBiomeRainLevel);
//        rate *= fogDensity;
//        Shader.uFogData.set(
//                rate * this.uTerrainFogDensity / 10f,
//                rate * this.uSkyFogDensity * 20,
//                this.uFadeTransition, this.uNearClarity
//        );
//        Shader.uFogBaseColor.set(this.uFogColor);
//        Shader.uCameraPosition.set(position);
//        Shader.uInverseViewMatrix.set(invViewMatrix);
//        Shader.uInverseProjectionMatrix.set(invProjectionMatrix);
//
//        Minecraft minecraft = Minecraft.getInstance();
//        AbstractTexture noiseTexture = minecraft.getTextureManager().getTexture(NOISE);
//        noiseTexture.setFilter(true, false);
////        instance.setSampler("uScreenSampler", tempTarget.getColorTextureId());
//        instance.setSampler("uNoiseSampler", noiseTexture);
//        instance.setSampler("uDepthSampler", tempTarget.getDepthTextureId());
//
//        RenderSystem.disableDepthTest();
//        RenderSystem.enableBlend();
//
//        RenderTarget main = minecraft.getMainRenderTarget();
////        main.bindWrite(true);
//        instance.apply();
//
//        this.draw();
//
//        instance.clear();
//        RenderSystem.disableBlend();
//
//        main.bindWrite(true);
//    }
//
//    public void setTerrainFogDensity(float density) {
//        this.uTerrainFogDensity = density / 128.0f;
//    }
//
//    public float getTerrainFogDensity() {
//        return this.uTerrainFogDensity * 128.0f;
//    }
//
//    public void setSkyFogDensity(float density) {
//        this.uSkyFogDensity = density / 128.0f;
//    }
//
//    public float getSkyFogDensity() {
//        return this.uSkyFogDensity * 128.0f;
//    }
//
//    public void setFadeTransition(float transition) {
//        this.uFadeTransition = transition;
//    }
//
//    public float getFadeTransition() {
//        return this.uFadeTransition;
//    }
//
//    public void setNearClarity(float factor) {
//        this.uNearClarity = factor / 128.0f;
//    }
//
//    public float getNearClarity() {
//        return this.uNearClarity * 128.0f;
//    }
//
//    public void setFogColor(int packedColor) {
//        this.uFogColor.set(
//                ARGB.red(packedColor) / 255.0f,
//                ARGB.green(packedColor) / 255.0f,
//                ARGB.blue(packedColor) / 255.0f,
//                ARGB.alpha(packedColor) / 255.0f
//        );
//    }
//
//    public void setFogColor(float r, float g, float b, float a) {
//        this.uFogColor.set(r, g, b, a);
//    }
//
//    public Vector4f getFogColor() {
//        return this.uFogColor;
//    }
//
//    public void setNoiseAmplifier(float amplifier) {
//        this.uNoiseAmplifier = amplifier * 0.25f;
//    }
//
//    public float getNoiseAmplifier() {
//        return this.uNoiseAmplifier * 4.0f;
//    }
//
//    public void setNoiseScale(float scale) {
//        this.uNoiseScale = scale / 1024.0f;
//    }
//
//    public float getNoiseScale() {
//        return this.uNoiseScale * 1024.0f;
//    }
//
//    public void setWindSpeed(float speed) {
//        this.mWindSpeed = speed;
//    }
//
//    public float getWindSpeed() {
//        return this.mWindSpeed;
//    }
//
//    public void setWindDirection(float x, float y) {
//        this.mWindDirection.set(x, y).normalize();
//    }
//
//    public Vector2f getWindDirection() {
//        return this.mWindDirection;
//    }
//
//    public void resize(int width, int height) {
//        if (width > 0 && height > 0 && tempTarget != null)
//            tempTarget.resize(width, height, true);
//    }
//
//    public void cleanup() {
//        if (tempTarget != null) tempTarget.destroyBuffers();
//        if (quad != null) quad.close();
//    }
//
//    private void draw() {
//        this.quad.bind();
//        this.quad.draw();
//        VertexBuffer.unbind();
//    }
//
//    @EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasonsApi.MODID, bus = EventBusSubscriber.Bus.MOD)
//    public static final class Shader {
//        static ShaderInstance screenFog;
//
//        static AbstractUniform uFogData;
//        static AbstractUniform uNoiseData;
//        static AbstractUniform uFogBaseColor;
//        static AbstractUniform uCameraPosition;
//        static AbstractUniform uInverseViewMatrix;
//        static AbstractUniform uInverseProjectionMatrix;
//
//        @SubscribeEvent
//        public static void onRegisterShaders(final RegisterShadersEvent event) throws IOException {
//            event.registerShader(
//                    new ShaderInstance(
//                            event.getResourceProvider(),
//                            EclipticSeasons.rl("screen_fog"),
//                            DefaultVertexFormat.POSITION
//                    ),
//                    shader -> {
//                        screenFog = shader;
//
//                        uNoiseData = shader.safeGetUniform("uNoiseData");
//                        uFogData = shader.safeGetUniform("uFogData");
//                        uFogBaseColor = shader.safeGetUniform("uFogBaseColor");
//                        uInverseViewMatrix = shader.safeGetUniform("uInverseViewMatrix");
//                        uInverseProjectionMatrix = shader.safeGetUniform("uInverseProjectionMatrix");
//                        uCameraPosition = shader.safeGetUniform("uCameraPosition");
//                    }
//            );
//        }
//    }
//}
