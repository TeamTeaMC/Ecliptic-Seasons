package com.teamtea.eclipticseasons.client.particle;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.SeasonalBlockTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.awt.*;

public class ParticleUtil {
    public static void createParticle(ClientLevel clientLevel, int x, int y, int z) {
        if (!ClientConfig.Particle.seasonParticle.get()) return;
        if (MapChecker.isValidDimension(clientLevel)) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();


            for (int j = 0; j < 667; ++j) {
                // if (clientLevel.getRandom().nextInt(chanceW) == 0)
                {
                    doAnimateTick(clientLevel, x, y, z, 16, clientLevel.getRandom(), blockpos$mutableblockpos);
                    doAnimateTick(clientLevel, x, y, z, 32, clientLevel.getRandom(), blockpos$mutableblockpos);
                }
            }
        }
    }

    // can refer TerrainParticle
    private static void doAnimateTick(ClientLevel clientLevel, int x, int y, int z, int b, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        int i = x + random.nextInt(b) - random.nextInt(b);
        int j = y + random.nextInt(b) - random.nextInt(b);
        int k = z + random.nextInt(b) - random.nextInt(b);

        blockpos$mutableblockpos.set(i, j, k);
        BlockState blockstate = clientLevel.getBlockState(blockpos$mutableblockpos);
        if (ClientConfig.Particle.fallenLeaves.get()
                && blockstate.getBlock() instanceof LeavesBlock) {
            if (!blockstate.is(SeasonalBlockTags.NONE_FALLEN_LEAVES)) {
                var sd = EclipticUtil.getNowSolarTerm(clientLevel).getSeason();
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
        if (ClientConfig.Particle.butterfly.get()
                && EclipticUtil.getNowSolarTerm(clientLevel).getSeason() == Season.SPRING
                && EclipticUtil.isDay(clientLevel)
        ) {
            if (blockstate.is(SeasonalBlockTags.HABITAT_BUTTERFLY)
                    && !clientLevel.isRainingAt(blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt(1024 * (int) (ClientConfig.Particle.butterflySpawnWeight.get() * 0.1f)) == 0
            ) {
                clientLevel.addParticle(EclipticSeasons.ParticleRegistry.BUTTERFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
            }
        }
        if (ClientConfig.Particle.firefly.get()
                && EclipticUtil.getNowSolarTerm(clientLevel).getSeason() == Season.SUMMER
                && EclipticUtil.isEvening(clientLevel)
        ) {
            if (blockstate.is(SeasonalBlockTags.HABITAT_FIREFLY)
                    && !clientLevel.isRainingAt(blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt(160 * (int) (ClientConfig.Particle.fireflySpawnWeight.get() * 0.1f)) == 0
            ) {
                clientLevel.addParticle(EclipticSeasons.ParticleRegistry.FIREFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
            }
        }

        if (ClientConfig.Particle.wildGoose.get()
                && EclipticUtil.getNowSolarTerm(clientLevel).getSeason() == Season.AUTUMN
                && EclipticUtil.isNoon(clientLevel)
                && clientLevel.canSeeSky(blockpos$mutableblockpos)
                && clientLevel.isEmptyBlock(blockpos$mutableblockpos)
                && !clientLevel.isRainingAt(blockpos$mutableblockpos)
                && clientLevel.getBiome(blockpos$mutableblockpos).value().getBaseTemperature() < 0.95f
                && random.nextInt(2295 * (int) (ClientConfig.Particle.wildGooseSpawnWeight.get() * 0.1f)) == 0) {
            clientLevel.addParticle(EclipticSeasons.ParticleRegistry.WILD_GOOSE, false, x + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), y + random.nextInt(15, 16 * 2), z + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), 0.0D, 5.0E-4D, 0.0D);
        }

        if ( random.nextInt(b)==0&&
        Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == EclipticSeasons.ModContents.snowy_maker_item.get()) {
            var data = clientLevel.getChunkAt(blockpos$mutableblockpos).getData(EclipticSeasons.ModContents.SNOWY_REMOVER);

            if (data.notSnowyAt(blockpos$mutableblockpos)) {
                j = clientLevel.getHeight(Heightmap.Types.MOTION_BLOCKING,i,k);
                clientLevel.addParticle(random.nextInt(3)>0?ParticleTypes.SMOKE:ParticleTypes.CLOUD,
                        false, i + 0.5, j + 0.3, k + 0.5, 0.0D, -0.0001, 0.0D);

                // clientLevel.addParticle(ParticleTypes.SMOKE, false, i + 0.5, j + 0.1, k + 0.5, 0.0D, -0.0001, 0.0D);
                // clientLevel.addParticle(ParticleTypes.CLOUD, false, i + 0.5, j + 0.5, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
                // clientLevel.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL,0)), (double) i + 0.5, (double) j + 0.5, (double) k + 0.5, 0.0, 0.0, 0.0);
                // Minecraft.getInstance().particleEngine.add(new SnowCleaner(clientLevel,(double) i + 0.5, (double) j + 0.5, (double) k + 0.5, Items.SNOWBALL.getDefaultInstance()));

            }
        }
    }

    public static void fallenLeaves(ClientLevel level, BlockPos pos, BlockState state) {
        if (!state.isAir()) {
            int color = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 2);
            if (new Color(color).equals(Color.WHITE)) {
                color = Minecraft.getInstance().getBlockColors().getColor(state, level, pos);
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

                            level.addParticle(ColorParticleOption.create(EclipticSeasons.ParticleRegistry.FALLEN_LEAVES, finalColor),
                                    (double) pos.getX() + d7,
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
}
