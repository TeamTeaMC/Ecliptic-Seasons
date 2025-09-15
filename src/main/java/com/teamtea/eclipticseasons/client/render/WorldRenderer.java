package com.teamtea.eclipticseasons.client.render;

import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.client.ClientEventHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.FogType;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class WorldRenderer {
    public static long reMainTick = 0;

    private static float getProgress(boolean fadeIn) {
        return Math.min(fadeIn ? (1 - reMainTick / 100f) : reMainTick / 100f, 1);
    }

    public static final int NONE_BLUR = 1;
    public static final int ON_BLUR = 2;
    public static final int TO_BLUR = 3;
    public static final int CLEAR_BLUR = 3;

    public static int oldBlurStatus = NONE_BLUR;

    public static void applyEffect(GameRenderer gameRenderer, LocalPlayer player) {
        if (player == null) return;

        if (Minecraft.getInstance().isPaused()) {
            if (oldBlurStatus == ON_BLUR || reMainTick > 0) {
                gameRenderer.shutdownEffect();
                oldBlurStatus = NONE_BLUR;
                reMainTick = 0;
                updateUniform("RadiusMultiplier", 0f);
            }
            return;
        }

        if (CommonConfig.Temperature.heatStroke.get()) {
            // var holder = BuiltInRegistries.MOB_EFFECT.getHolder(EclipticSeasons.EffectRegistry.Effects.HEAT_STROKE).get();
            boolean hasEffect = false;
            for (var activeEffect : player.getActiveEffectsMap().keySet()) {
                if (activeEffect.is(EffectRegistry.Effects.HEAT_STROKE)) {
                    hasEffect = true;
                    break;
                }
            }
            int blurStatus = hasEffect ? ON_BLUR : NONE_BLUR;
            if (blurStatus != oldBlurStatus) {
                if (blurStatus == ON_BLUR) {
                    {

                        // 我们写的shader好像有问题？
                        // gameRenderer.loadEffect(EclipticSeasonsMod.rl("shaders/post/fade_in_blur.json"));
                        gameRenderer.loadEffect(ResourceLocation.withDefaultNamespace("shaders/post/blur.json"));
                    }
                }

                if (reMainTick > 0) {
                    reMainTick -= 10;
                } else reMainTick = 100;

                float progress = getProgress(blurStatus == ON_BLUR) * 0.03f;
                // if (progress != prevProgress)
                {
                    // prevProgress = progress;
                    // 用于mc原版blur
                    progress *= 10f;

                    updateUniform("RadiusMultiplier", progress);
                }
                // EclipticSeasons.logger(reMainTick, progress, blurStatus, oldBlurStatus);
                if (reMainTick <= 0) {
                    oldBlurStatus = blurStatus;
                    if (oldBlurStatus == NONE_BLUR) {
                        gameRenderer.shutdownEffect();
                    }
                    reMainTick = 0;
                }
            }
        }
    }

    public static void updateUniform(String name, float value) {
        var postChain = Minecraft.getInstance().gameRenderer.currentEffect();
        if (postChain != null)
            for (PostPass postPass : postChain.passes) {
                var uniform = postPass.getEffect().getUniform(name);
                if (uniform != null) {
                    uniform.set(value);
                }
            }
    }


    public static void renderFogColors(Camera camera, float partialTick, ViewportEvent.ComputeFogColor event) {
        if (camera.getEntity() instanceof Player player && camera.getFluidInCamera() == FogType.NONE && ClientEventHandler.prevFogDensity > 0f) {
            // Calculate color based on time of day
            final float angle = player.level().getSunAngle(partialTick);
            final float height = Mth.cos(angle);
            final float delta = Mth.clamp((height + 0.4f) / 0.8f, 0, 1);

            final int colorDay = 0xbfbfd8;
            final int colorNight = 0x0c0c19;
            final float red = ((colorDay >> 16) & 0xFF) * delta + ((colorNight >> 16) & 0xFF) * (1 - delta);
            final float green = ((colorDay >> 8) & 0xFF) * delta + ((colorNight >> 8) & 0xFF) * (1 - delta);
            final float blue = (colorDay & 0xFF) * delta + (colorNight & 0xFF) * (1 - delta);

            event.setRed(red / 255f);
            event.setBlue(blue / 255f);
            event.setGreen(green / 255f);

            ClientEventHandler.r = red / 255f;
            ClientEventHandler.g = blue / 255f;
            ClientEventHandler.b = green / 255f;
        }
    }

    public static void renderFogDensity(Camera camera, ViewportEvent.RenderFog event) {
        if (camera.getEntity() instanceof Player player) {
            final long thisTick = Util.getMillis();
            final boolean firstTick = ClientEventHandler.prevFogTick == -1;
            final float deltaTick = firstTick ? 1e10f : (thisTick - ClientEventHandler.prevFogTick) * 0.00015f;

            ClientEventHandler.prevFogTick = thisTick;

            float expectedFogDensity = 0f;

            final Level level = player.level();
            final Biome biome = level.getBiome(camera.getBlockPosition()).value();
            if (level.isRaining() && biome.coldEnoughToSnow(camera.getBlockPosition())) {
                final int light = level.getBrightness(LightLayer.SKY, BlockPos.containing(player.getEyePosition()));
                expectedFogDensity = Mth.clampedMap(light, 0f, 15f, 0f, 1f);
            }

            // Smoothly interpolate fog towards the expected value - increasing faster than it decreases
            if (expectedFogDensity > ClientEventHandler.prevFogDensity) {
                ClientEventHandler.prevFogDensity = Math.min(ClientEventHandler.prevFogDensity + 4f * deltaTick, expectedFogDensity);
            } else if (expectedFogDensity < ClientEventHandler.prevFogDensity) {
                ClientEventHandler.prevFogDensity = Math.max(ClientEventHandler.prevFogDensity - deltaTick, expectedFogDensity);
            }

            if (camera.getFluidInCamera() != FogType.NONE) {
                ClientEventHandler.prevFogDensity = -1; // Immediately cancel fog if there's another fog effect going on
                ClientEventHandler.prevFogTick = -1;
            }

            if (ClientEventHandler.prevFogDensity > 0) {
                final float scaledDelta = 1 - (1 - ClientEventHandler.prevFogDensity) * (1 - ClientEventHandler.prevFogDensity);
                final float fogDensity = 0.1f;
                final float farPlaneScale = Mth.lerp(scaledDelta, 1f, fogDensity);
                final float nearPlaneScale = Mth.lerp(scaledDelta, 1f, 0.3f * fogDensity);

                event.scaleNearPlaneDistance(nearPlaneScale);
                event.scaleFarPlaneDistance(farPlaneScale);
                event.setCanceled(true);
            }
        }
    }

    public static boolean isSectionLoad(SectionPos sectionPos) {
        return isSectionLoad(sectionPos, 1);
    }

    public static boolean isSectionLoad(SectionPos pPos, int range) {
        boolean load = true;
        if (Minecraft.getInstance().level instanceof Level level) {
            for (int i = -range + 1; i < range; i++) {
                for (int j = -range + 1; j < range; j++) {
                    load &= level.getChunk(pPos.getX() + i, pPos.getZ() + j,
                            ChunkStatus.FULL, false) != null;
                    if (!load) break;
                }
            }
        }
        // if(!load)
        //     EclipticSeasons.logger(pPos);
        return load;
    }

    public static void setSectionDirty(SectionPos sectionPos) {
        if (isSectionLoad(sectionPos)) {
            Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
        }
    }

    public static void setSectionDirtyWithNeighbors(SectionPos sectionPos) {
        if (isSectionLoad(sectionPos, 2)) {
            Minecraft.getInstance().levelRenderer.setSectionDirtyWithNeighbors(sectionPos.x(), sectionPos.y(), sectionPos.z());
        }
    }

    public static void setSectionDirtyRandomly(SectionPos sectionPos) {
        if (Minecraft.getInstance().level != null) {
            RandomSource random = Minecraft.getInstance().level.random;
            int lastViewDistance = (int) (Minecraft.getInstance().levelRenderer.getLastViewDistance() - 1);
            for (int i = 0; i < random.nextInt(8) + 4; i++) {
                {
                    setSectionDirtyWithNeighbors(SectionPos.of(sectionPos.x() + 2 * (random.nextInt(lastViewDistance)) - lastViewDistance,
                            sectionPos.y(),
                            sectionPos.z() + 2 * (random.nextInt(lastViewDistance)) - lastViewDistance));
                }
            }
        }
    }


    public static void setAllDirty(SectionPos centerPos) {
        int pSectionX = centerPos.x();
        int pSectionY = centerPos.y();
        int pSectionZ = centerPos.z();
        int d = (int) Minecraft.getInstance().levelRenderer.getLastViewDistance();
        for (int j = pSectionZ - d; j <= pSectionZ + d; j++) {
            for (int i = pSectionX - d; i <= pSectionX + d; i++) {
                for (int k = pSectionY - 3; k <= pSectionY + 1; k++) {
                    setSectionDirty(SectionPos.of(i, k, j));
                }
            }
        }
    }
}
