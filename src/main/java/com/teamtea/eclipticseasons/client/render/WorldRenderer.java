package com.teamtea.eclipticseasons.client.render;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.client.ClientEventHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.ArrayList;

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
                        gameRenderer.loadEffect(EclipticSeasons.rl("shaders/post/box_blur.json"));
//                        gameRenderer.loadEffect(ResourceLocation.withDefaultNamespace("shaders/post/blur.json"));
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
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        int pSectionX = centerPos.x();
        //int pSectionY = centerPos.y();
        int pSectionZ = centerPos.z();
        int d = (int) Minecraft.getInstance().levelRenderer.getLastViewDistance();
        for (int j = pSectionZ - d; j <= pSectionZ + d; j++) {
            for (int i = pSectionX - d; i <= pSectionX + d; i++) {
                var chunk = MapChecker.getChunkView(level, i, j);
                if (chunk != null) {
                    IntArraySet set = new IntArraySet();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            set.add(SectionPos.posToSectionCoord(chunk.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)));
                        }
                    }
                    for (IntIterator it = set.iterator(); it.hasNext(); ) {
                        int pSectionY = it.nextInt();
                        setSectionDirty(SectionPos.of(i, pSectionY, j));
                    }
                    //for (int k = pSectionY - 3; k <= pSectionY + 1; k++) {
                    //    setSectionDirty(SectionPos.of(i, k, j));
                    //}
                }
            }
        }
    }
}
