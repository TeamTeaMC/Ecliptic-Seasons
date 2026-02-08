package com.teamtea.eclipticseasons.client.render;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

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


        int blurStatus =
                CommonConfig.Temperature.heatStroke.get() &&
                        player.hasEffect(EffectRegistry.HEAT_STROKE)
                        ? ON_BLUR : NONE_BLUR;
        if (blurStatus != oldBlurStatus) {
            if (blurStatus == ON_BLUR) {
                {
                    gameRenderer.loadEffect(EclipticSeasons.rl("shaders/post/fade_in_blur.json"));
                }
            }

            if (reMainTick > 0) {
                reMainTick-=10;
            } else reMainTick = 100;

            float progress = getProgress(blurStatus == ON_BLUR) * 0.03f;
            // if (progress != prevProgress)
            {
                // prevProgress = progress;
                updateUniform("Progress", progress);
            }
            // EclipticSeasons.logger(reMainTick, progress, blurStatus, oldBlurStatus);
            if (reMainTick == 0) {
                oldBlurStatus = blurStatus;
                if (oldBlurStatus == NONE_BLUR) {
                    gameRenderer.shutdownEffect();
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
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            for (int i = -range + 1; i < range; i++) {
                for (int j = -range + 1; j < range; j++) {
                    load &= level.getChunk(pPos.getX() + i, pPos.getZ() + j,
                            ChunkStatus.FULL, false) != null;
                    if (!load) break;
                }
            }
        }
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
