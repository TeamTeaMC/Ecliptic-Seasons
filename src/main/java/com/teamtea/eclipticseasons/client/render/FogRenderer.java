package com.teamtea.eclipticseasons.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.config.ClientConfig;
import jdk.jfr.Experimental;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

/*
 * 如果需要把 FORGE bus 的事件订阅并入 ClientEventHandler
 * 请按需把单例改为静态
 * */
@Experimental
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FogRenderer {

    public static final FogRenderer INSTANCE = new FogRenderer();
    static final ResourceLocation NOISE = EclipticSeasons.rl("textures/noise/perlin_256x_r.png");

    private final RenderTarget tempTarget;
    private VertexBuffer quad;

    // uniforms
    private float uTerrainFogDensity;
    private float uSkyFogDensity;
    private float uFadeTransition;
    private float uNearClarity;
    private float uNoiseAmplifier;
    private float uNoiseScale;
    private final Vector4f uFogColor;

    // upload data
    private final Vector2f mWindDirection;
    private float mWindSpeed;
    private float mTimer;

    private boolean bufferInitialized = false;

    /*
     * 需要在 render thread 进行初始化, 最好在 Tesselator 初始化后进行
     * 如果后续需要改为静态类, 最好把那几个final去掉, 然后把这个构造器改为 init()
     * */
    FogRenderer() {

        if (Minecraft.getInstance() != null) {
            Window window = Minecraft.getInstance().getWindow();
            this.tempTarget = new TextureTarget(window.getWidth(), window.getHeight(), true, true);
        } else this.tempTarget = null;

        this.mWindDirection = new Vector2f(0.0f, 0.0f);
        this.uFogColor = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);

        this.debugInit();
    }


    @SubscribeEvent
    public static void onRenderLevelStage(final RenderLevelStageEvent event) {

        if (Minecraft.getInstance().cameraEntity == null
                || Minecraft.getInstance().cameraEntity.getEyeInFluidType() != net.minecraftforge.common.ForgeMod.EMPTY_TYPE.get())
            return;
        if (!ClientConfig.Debug.fogWeather.get()) return;

        RenderLevelStageEvent.Stage stage = event.getStage();

        if (stage == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            Matrix4f modelViewMatrix = RenderSystem.getModelViewMatrix();
            INSTANCE.prepareBuffer();
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Matrix4f modelViewMatrix = RenderSystem.getModelViewMatrix();
            INSTANCE.render(event.getCamera(), event.getProjectionMatrix(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public static void onShutdown(final GameShuttingDownEvent event) {
        INSTANCE.cleanup();
    }

    /*
     * 测试的初始化值
     * */
    public void debugInit() {
        this.setFogColor(0.8f, 0.8f, 0.8f, 1.0f);
        this.setTerrainFogDensity(94.3f);
        this.setSkyFogDensity(94.3f);
        this.setNearClarity(12.0f);
        this.setFadeTransition(6.0f);

        this.setNoiseAmplifier(32.0f);
        this.setNoiseScale(0.1f);

        this.setWindSpeed(0.01f);
        this.setWindDirection(0.0f, 1.0f);
    }

    public void prepareBuffer() {
        this.tempTarget.clear(Minecraft.ON_OSX);
        RenderTarget src = Minecraft.getInstance().getMainRenderTarget();

        final int width = src.width;
        final int height = src.height;

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, src.frameBufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, tempTarget.frameBufferId);

        GL30.glBlitFramebuffer(
                0, 0, width, height,
                0, 0, width, height,
                GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT,
                GL30.GL_NEAREST
        );

        src.bindWrite(false);
    }

    public void render(Camera camera, final Matrix4f projectionMatrix, float partialTick) {

        if (!Minecraft.getInstance().isPaused()) {
            this.mTimer += partialTick * this.mWindSpeed;

            if (this.mTimer > 256.0) {
                this.mTimer -= 256.0f;
            }
        }

        if (this.uTerrainFogDensity < 1e-6) {
            return;
        }

        if (!this.bufferInitialized) {
            this.quad = new VertexBuffer(VertexBuffer.Usage.STATIC);

            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            bufferbuilder.vertex(0.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(1.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0F, 1.0F, 0.0F).endVertex();

            this.quad.bind();
            this.quad.upload(bufferbuilder.end());
            this.bufferInitialized = true;
            VertexBuffer.unbind();
        }

        ShaderInstance instance = Shader.instance;
        final Matrix4f invProjectionMatrix = new Matrix4f(projectionMatrix).invert();
        final Vector3f position = camera.getPosition().toVector3f();

        // inverse view matrix without translation
        final Matrix4f invViewMatrix = new Matrix4f()
                .rotation(camera.rotation());

        Shader.uNoiseData.set(
                this.mWindDirection.x() * this.mTimer,
                this.mWindDirection.y() * this.mTimer,
                this.uNoiseAmplifier,
                this.uNoiseScale
        );
        float rate = 0.5f + 0.5f * ClientCon.getUseLevel().getBrightness(LightLayer.SKY, ClientCon.agent.getCameraEntity().blockPosition()) / 15f;
        rate *= (ClientWeatherChecker.lastBiomeRainLevel * ClientWeatherChecker.lastBiomeRainLevel);
        Shader.uFogData.set(
                rate * this.uTerrainFogDensity / 10f,
                rate * this.uSkyFogDensity * 20,
                this.uFadeTransition, this.uNearClarity
        );
        Shader.uFogBaseColor.set(this.uFogColor);
        Shader.uCameraPosition.set(position);
        Shader.uInverseViewMatrix.set(invViewMatrix);
        Shader.uInverseProjectionMatrix.set(invProjectionMatrix);

        Minecraft minecraft = Minecraft.getInstance();
        AbstractTexture noiseTexture = minecraft.getTextureManager().getTexture(NOISE);
        noiseTexture.setFilter(true, false);
//        instance.setSampler("uScreenSampler", tempTarget.getColorTextureId());
        instance.setSampler("uNoiseSampler", noiseTexture);
        instance.setSampler("uDepthSampler", tempTarget.getDepthTextureId());

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        RenderTarget main = minecraft.getMainRenderTarget();
//        main.bindWrite(true);
        instance.apply();

        this.draw();

        instance.clear();
        RenderSystem.disableBlend();

        main.bindWrite(true);
    }

    public void setTerrainFogDensity(float density) {
        this.uTerrainFogDensity = density / 128.0f;
    }

    public float getTerrainFogDensity() {
        return this.uTerrainFogDensity * 128.0f;
    }

    public void setSkyFogDensity(float density) {
        this.uSkyFogDensity = density / 128.0f;
    }

    public float getSkyFogDensity() {
        return this.uSkyFogDensity * 128.0f;
    }

    public void setFadeTransition(float transition) {
        this.uFadeTransition = transition;
    }

    public float getFadeTransition() {
        return this.uFadeTransition;
    }

    public void setNearClarity(float factor) {
        this.uNearClarity = factor / 128.0f;
    }

    public float getNearClarity() {
        return this.uNearClarity * 128.0f;
    }

    public void setFogColor(int packedColor) {
        this.uFogColor.set(
                FastColor.ARGB32.red(packedColor) / 255.0f,
                FastColor.ARGB32.green(packedColor) / 255.0f,
                FastColor.ARGB32.blue(packedColor) / 255.0f,
                FastColor.ARGB32.alpha(packedColor) / 255.0f
        );
    }

    public void setFogColor(float r, float g, float b, float a) {
        this.uFogColor.set(r, g, b, a);
    }

    public Vector4f getFogColor() {
        return this.uFogColor;
    }

    public void setNoiseAmplifier(float amplifier) {
        this.uNoiseAmplifier = amplifier * 0.25f;
    }

    public float getNoiseAmplifier() {
        return this.uNoiseAmplifier * 4.0f;
    }

    public void setNoiseScale(float scale) {
        this.uNoiseScale = scale / 1024.0f;
    }

    public float getNoiseScale() {
        return this.uNoiseScale * 1024.0f;
    }

    public void setWindSpeed(float speed) {
        this.mWindSpeed = speed;
    }

    public float getWindSpeed() {
        return this.mWindSpeed;
    }

    public void setWindDirection(float x, float y) {
        this.mWindDirection.set(x, y).normalize();
    }

    public Vector2f getWindDirection() {
        return this.mWindDirection;
    }

    //
    public void resize(int width, int height) {
        tempTarget.resize(width, height, true);
    }

    public void cleanup() {
        tempTarget.destroyBuffers();
        if (quad != null) quad.close();
    }

    // fast quad draw
    private void draw() {
        this.quad.bind();
        this.quad.draw();
        VertexBuffer.unbind();
    }

    // 如果未来还添加更多的 shader, 请把该部分独立出去, 并把 shader 进行封装
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Shader {
        static ShaderInstance instance;

        static AbstractUniform uFogData;
        static AbstractUniform uNoiseData;
        static AbstractUniform uFogBaseColor;
        static AbstractUniform uCameraPosition;
        static AbstractUniform uInverseViewMatrix;
        static AbstractUniform uInverseProjectionMatrix;

        @SubscribeEvent
        public static void onRegisterShaders(final RegisterShadersEvent event) throws IOException {
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            EclipticSeasons.rl("screen_fog"),
                            DefaultVertexFormat.BLIT_SCREEN
                    ),
                    shader -> {
                        instance = shader;

                        uNoiseData = shader.safeGetUniform("uNoiseData");
                        uFogData = shader.safeGetUniform("uFogData");
                        uFogBaseColor = shader.safeGetUniform("uFogBaseColor");
                        uInverseViewMatrix = shader.safeGetUniform("uInverseViewMatrix");
                        uInverseProjectionMatrix = shader.safeGetUniform("uInverseProjectionMatrix");
                        uCameraPosition = shader.safeGetUniform("uCameraPosition");
                    }
            );
        }
    }
}