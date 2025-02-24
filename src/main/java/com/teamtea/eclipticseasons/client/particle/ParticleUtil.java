package com.teamtea.eclipticseasons.client.particle;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ParticleUtil {
    public static void createParticle(ClientLevel clientLevel, int x, int y, int z) {
        if (!ClientConfig.Renderer.particle.get()) return;

        var pos = new BlockPos(x, y, z);
        // clientLevel.addParticle(ParticleTypes.CHERRY_LEAVES, (double) pos.getX(), (double) pos.getY() - 0.5f, (double) pos.getZ(), 0.0D, 0.0D, 0.0D);
        // if (clientLevel.getGameTime() % 10 == 0) {
        //     if (Minecraft.getInstance().cameraEntity instanceof AbstractClientPlayer player) {
        //         // Ecliptic.logger(player.move(0.0f));
        //         // clientLevel.addDestroyBlockEffect(pos, clientLevel.getBlockState(pos.below()));
        //         destroy(clientLevel, pos.above(), clientLevel.getBlockState(pos.below()));
        //
        //     }
        // }
        // if (EclipticUtil.getNowSolarTerm(clientLevel).getSeason() == Season.AUTUMN)
        {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            var sd = EclipticUtil.getNowSolarTerm(clientLevel).getSeason();
            int chanceW = 19;
            switch (sd) {
                case SPRING -> chanceW = 17;
                case SUMMER -> chanceW = 27;
                case AUTUMN -> chanceW = 9;
                case WINTER -> chanceW = 15;
            }

            for (int j = 0; j < 667; ++j) {
                if (clientLevel.getRandom().nextInt(chanceW) == 0) {
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
        if (blockstate.getBlock() instanceof LeavesBlock) {
            fallenLeaves(clientLevel, blockpos$mutableblockpos, blockstate);
        } else if (blockstate.is(BlockTags.FLOWERS)) {
            // CampfireBlock.
            if (EclipticUtil.getNowSolarTerm(clientLevel).getSeason() == Season.SUMMER
                    && EclipticUtil.isEvening(clientLevel)
                    && !clientLevel.isRainingAt(blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt(3) == 0
            )
                clientLevel.addParticle(ParticleRegistry.FIREFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);

        }

        if (EclipticUtil.getNowSolarTerm(clientLevel).getSeason() == Season.AUTUMN
                && EclipticUtil.isNoon(clientLevel)
                && clientLevel.canSeeSky(blockpos$mutableblockpos)
                && clientLevel.isEmptyBlock(blockpos$mutableblockpos)
                && !clientLevel.isRainingAt(blockpos$mutableblockpos)
                && random.nextInt(255) == 0) {
            clientLevel.addParticle(ParticleRegistry.WILD_GOOSE, false, x + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), y + random.nextInt(15, 16 * 2), z + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), 0.0D, 5.0E-4D, 0.0D);
        }
    }

    public static void fallenLeaves(ClientLevel level, BlockPos pos, BlockState state) {
        if (!state.isAir()) {
            VoxelShape voxelshape = state.getShape(level, pos);
            double d0 = 0.25D;
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
                            Minecraft.getInstance().particleEngine.add(
                                    new LeavesTerrainParticle(level,
                                            (double) pos.getX() + d7,
                                            (double) pos.getY() + d8,
                                            (double) pos.getZ() + d9,
                                            d4 - 0.5D,
                                            d5 - 0.5D,
                                            d6 - 0.5D,
                                            state, pos).updateSprite(state, pos));
                        }
                    }
                }

            });
        }
    }
}
