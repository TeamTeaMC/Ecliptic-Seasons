package com.teamtea.eclipticseasons.client.particle;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ParticleUtil {

    private static Holder<Biome> nowBiome = null;

    public static void createParticle(ClientLevel clientLevel, int x, int y, int z) {
        if (!ClientConfig.Particle.seasonParticle.get()) return;

        // BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        // for (int j = 0; j < 667; ++j) {
        //     // if (clientLevel.getRandom().nextInt(chanceW) == 0)
        //     {
        //         doAnimateTick(clientLevel, x, y, z, 16, clientLevel.getRandom(), blockpos$mutableblockpos);
        //         doAnimateTick(clientLevel, x, y, z, 32, clientLevel.getRandom(), blockpos$mutableblockpos);
        //     }
        // }
    }

    public static void doAnimateTick(ClientLevel clientLevel, int x, int y, int z, int b, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        int i = x + random.nextInt(b) - random.nextInt(b);
        int j = y + random.nextInt(b) - random.nextInt(b);
        int k = z + random.nextInt(b) - random.nextInt(b);

        blockpos$mutableblockpos.set(i, j, k);
        BlockState blockstate = clientLevel.getBlockState(blockpos$mutableblockpos);
        doAnimateTick(clientLevel, x, y, z, b, random, blockpos$mutableblockpos, blockstate);
    }

    // can refer TerrainParticle
    public static void doAnimateTick(ClientLevel clientLevel, int x, int y, int z, int b, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos, BlockState blockstate) {
        if (!ClientConfig.Particle.seasonParticle.get()) return;
        int i = blockpos$mutableblockpos.getX();
        int j = blockpos$mutableblockpos.getY();
        int k = blockpos$mutableblockpos.getZ();
        if (ClientConfig.Particle.fallenLeaves.get()
                && blockstate.getBlock() instanceof LeavesBlock) {
            if (!blockstate.is(EclipticBlockTags.NONE_FALLEN_LEAVES)) {
                var sd = ClientCon.nowSolarTerm.getSeason();
                if (sd != Season.NONE) {
                    int chanceW = 19;
                    switch (sd) {
                        case SPRING -> chanceW = 17;
                        case SUMMER -> chanceW = 27;
                        case AUTUMN -> chanceW = 9;
                        case WINTER -> chanceW = 15;
                    }
                    chanceW *= (int) (ClientConfig.Particle.fallenLeavesDropWeight.get() * 0.4f);
                    // chanceW*=4;
                    if (random.nextInt(chanceW) == 0) {
                        fallenLeaves(clientLevel, blockpos$mutableblockpos, blockstate);
                    }
                }
            }
        }
        if (ClientConfig.Particle.butterfly.get()
                && ClientCon.nowSolarTerm.getSeason() == Season.SPRING
                && ClientCon.isDay
        ) {
            if (blockstate.is(EclipticBlockTags.HABITAT_BUTTERFLY)
                    && !EclipticSeasonsApi.getInstance().isRainOrSnowAt(clientLevel, blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt(1024 * (int) (ClientConfig.Particle.butterflySpawnWeight.get() * 0.1f)) == 0
            ) {
                clientLevel.addParticle(ParticleRegistry.BUTTERFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
            }
        }
        if (ClientConfig.Particle.firefly.get()
                && ClientCon.nowSolarTerm.getSeason() == Season.SUMMER
                && ClientCon.isEvening
        ) {
            if (blockstate.is(EclipticBlockTags.HABITAT_FIREFLY)
                    && !EclipticSeasonsApi.getInstance().isRainOrSnowAt(clientLevel, blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt(160 * (int) (ClientConfig.Particle.fireflySpawnWeight.get() * 0.1f)) == 0
            ) {
                clientLevel.addParticle(ParticleRegistry.FIREFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
            }
        }

        if (ClientConfig.Particle.wildGoose.get()
                && ClientCon.nowSolarTerm.getSeason() == Season.AUTUMN
                && ClientCon.isNoon
                && clientLevel.canSeeSky(blockpos$mutableblockpos)
                && clientLevel.isEmptyBlock(blockpos$mutableblockpos)
                && !EclipticSeasonsApi.getInstance().isRainAt(clientLevel, blockpos$mutableblockpos)
                && clientLevel.getBiome(blockpos$mutableblockpos).value().getBaseTemperature() < 0.95f
                && random.nextInt(2295 * (int) (ClientConfig.Particle.wildGooseSpawnWeight.get() * 0.1f)) == 0) {
            clientLevel.addParticle(ParticleRegistry.WILD_GOOSE, false, x + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), y + random.nextInt(15, 16 * 2), z + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), 0.0D, 5.0E-4D, 0.0D);
        }


    }

    public static void fallenLeaves(ClientLevel level, BlockPos pos, BlockState state) {
        if (!state.isAir()) {
            int color = getOrCreateColor(state).getRGB();

            if (new Color(color).equals(Color.WHITE)) {
                color = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
            }
            if (new Color(color).equals(Color.WHITE)) {
                color = state.getMapColor(level, pos).col;
                // color = Color.PINK.getRGB();
            }
            VoxelShape voxelshape = state.getShape(level, pos);
            double d0 = 0.25D;
            int finalColor = color;
            voxelshape.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
                double x = Math.min(1.0D, x1 - x0);
                double y = Math.min(1.0D, y1 - y0);
                double z = Math.min(1.0D, z1 - z0);
                int aX = Math.min(1, Mth.ceil(x / 0.25D));
                int aY = Math.min(1, Mth.ceil(y / 0.25D));
                int aZ = Math.min(1, Mth.ceil(z / 0.25D));

                for (int pX = 0; pX < aX; ++pX) {
                    for (int pY = -aY; pY < 0; ++pY) {
                        for (int pZ = 0; pZ < aZ; ++pZ) {
                            double d4 = ((double) pX + 0.5D) / (double) aX;
                            double d5 = ((double) pY + 0.5D) / (double) aY;
                            double d6 = ((double) pZ + 0.5D) / (double) aZ;
                            double d7 = d4 * x + x0;
                            double d8 = d5 * y + y0;
                            double d9 = d6 * z + z0;
                            // Minecraft.getInstance().particleEngine.add(
                            //         new LeavesTerrainParticle(level,
                            //                 (double) pos.getX() + d7,
                            //                 (double) pos.getY() + d8,
                            //                 (double) pos.getZ() + d9,
                            //                 d4 - 0.5D,
                            //                 d5 - 0.5D,
                            //                 d6 - 0.5D,
                            //                 state, pos).updateSprite(state, pos));
                            if (d5 > 0.49f) {
                                d5 = 0.42f;
                            }

                            level.addParticle(new ColorParticleOptions(new Vector3f(FastColor.ARGB32.red(finalColor) / 255.0f, FastColor.ARGB32.green(finalColor) / 255.0f, FastColor.ARGB32.blue(finalColor) / 255.0f), 1.0f), (double) pos.getX() + d7,
                                    (double) pos.getY() + d8,
                                    (double) pos.getZ() + d9,
                                    Mth.clamp(d4 - 0.5D, -0.25f, 0.25f),
                                    (d5 - 0.5D) * 0.75,
                                    Mth.clamp(d6 - 0.5D, -0.25f, 0.25f)
                            );
                        }
                    }
                }

            });
        }
    }


    public static void onReloadResource() {
        LEAVES_COLOR_MAP.clear();
    }

    private static final Map<BlockState, Color> LEAVES_COLOR_MAP = new IdentityHashMap<>();

    public static Color getOrCreateColor(BlockState state) {
        Minecraft mc = Minecraft.getInstance();
        Color c = Color.WHITE;
        try {
            Color orDefault = LEAVES_COLOR_MAP.getOrDefault(state, null);
            if (orDefault != null) {
                return orDefault;
            }
            BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
            boolean isColored = false;
            // for (Direction direction : new Direction[]{Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST, null}) {
            //     List<BakedQuad> quads = blockModel.getQuads(state, direction, mc.level.getRandom(), ModelData.EMPTY, RenderType.cutout());
            //     for (BakedQuad quad : quads) {
            //         if (quad.getTintIndex() != -1) {
            //             isColored = true;
            //         }
            //     }
            //     if (isColored) break;
            // }
            if (isColored) {
                c = Color.WHITE;
            } else {
                TextureAtlasSprite texture = blockModel.getParticleIcon(ModelData.EMPTY);
                ArrayList<Integer> rlist = new ArrayList<>();
                ArrayList<Integer> glist = new ArrayList<>();
                ArrayList<Integer> blist = new ArrayList<>();
                for (int i = 0; i < texture.contents().width(); i++) {
                    for (int j = 0; j < texture.contents().height(); j++) {
                        int color = 0xff000000 | texture.getPixelRGBA(0, i, j);
                        int r = color & 0xff;
                        int g = (color >> 8) & 0xff;
                        int b = (color >> 16) & 0xff;
//                                int a = color >>> 24;
                        if (r * g * b > 0) {
                            rlist.add(r);
                            glist.add(g);
                            blist.add(b);
                        }
                    }
                }
                c = new Color((int) ColorHelper.getAvg(rlist), (int) ColorHelper.getAvg(glist), (int) ColorHelper.getAvg(blist));
                if (c.getRed() == c.getBlue() && c.getBlue() == c.getGreen()) {
                    c = Color.WHITE;
                }
            }
            LEAVES_COLOR_MAP.put(state, c);

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return c;
    }

}
