package com.teamtea.eclipticseasons.client.render.chunk;

import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class IceKeeper {
    public static final LongOpenHashSet ICE_SHOULD_BE_IGNORED = new LongOpenHashSet();

    public static void checkIfPlayerStepInFrozenWater(Entity player) {
        if (!CommonConfig.isSnowyWinter()) return;
        if (!ClientConfig.Debug.frozenWater.get() || !ClientConfig.Debug.frozenWaterBreakable.get()) return;
        if (!player.isInWater()) return;

        Level level = player.level();
        BlockPos blockPos = player.blockPosition();
        // player.getBoundingBoxForCulling()
        if (MapChecker.getHeight(level, blockPos) != blockPos.getY()) return;
        BlockState water = Blocks.WATER.defaultBlockState();
        if (water.getBlock() instanceof SimpleWaterloggedBlock) return;
        if (!ExtraModelManager.maySnowyAt(level, null, water, blockPos, level.getRandom(), water.getSeed(blockPos)))
            return;


        if (!ICE_SHOULD_BE_IGNORED.contains(blockPos.asLong())) {
            BlockPos above = blockPos.above();
            if (ClientConfig.Debug.frozenWaterCheckLight.get() && !ExtraModelManager.notTooBright(level, null, blockPos))
                return;
            if (!level.getBlockState(blockPos).getFluidState().isSource()) return;
            ICE_SHOULD_BE_IGNORED.add(blockPos.asLong());
            try {
                WorldRenderer.setSectionDirtyWithNeighbors(SectionPos.of(player));
            } catch (Exception e) {
                e.printStackTrace();
            }

            int count = level.getRandom().nextInt(7, 12);
            Direction direction = Direction.UP;
            Vec3 vec3 = above.getBottomCenter();
            BlockParticleOption blockParticleOption = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BLUE_ICE.defaultBlockState());
            for (int k = 0; k < count * 2; k++) {
                level.addParticle(
                        blockParticleOption,
                        vec3.x - (double) (direction == Direction.WEST ? 1.0E-6F : 0.0F),
                        vec3.y,
                        vec3.z - (double) (direction == Direction.NORTH ? 1.0E-6F : 0.0F),
                        0.15f,
                        0.1f,
                        0.15f
                );
            }
            level.playSound(ClientCon.agent.getCameraEntity(), blockPos, Blocks.ICE.defaultBlockState()
                    .getSoundType()
                    .getBreakSound(), SoundSource.BLOCKS, 0.35F, 0.65f);

        }
    }

    public static void clearAll() {
        ICE_SHOULD_BE_IGNORED.clear();
    }


    public static boolean notFrozen(BlockAndTintGetter worldSlice, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (!CommonConfig.isSnowyWinter()) return true;
        if (!ClientConfig.Debug.frozenWater.get()) return true;
        if (!fluidState.isSourceOfType(Fluids.WATER)) return true;
        if (blockState.getBlock() instanceof SimpleWaterloggedBlock) return true;
        if (!(worldSlice instanceof IMapSlice mapSlice)) return true;
        if (mapSlice.getBlockHeight(blockPos) != blockPos.getY()) return true;
        if (!ExtraModelManager.maySnowyAt(ClientCon.getUseLevel(), mapSlice, blockState, blockPos, null, blockState.getSeed(blockPos)))
            return true;
        if (ClientConfig.Debug.frozenWaterCheckLight.get() && !ExtraModelManager.notTooBright(worldSlice, mapSlice, blockPos))
            return true;
        return ClientConfig.Debug.frozenWaterBreakable.get() && ICE_SHOULD_BE_IGNORED.contains(blockPos.asLong());
    }

    public static BakedModel getIceModel(BlockState state, FluidState fluidState) {
        return ExtraModelManager.models.get(ExtraModelManager.ice);
    }

    public static BlockState getFakeState(BlockState state, FluidState fluidState) {
        return Blocks.ICE.defaultBlockState();
    }

    // int y = blockPos.getY();
    // int x = blockPos.getX();
    // int z = blockPos.getZ();
    // blockPos.setY(y + 1);
    // boolean isEmptyAbove = !mapSlice.getBlockState(blockPos).isAir();
    // blockPos.setY(y);
    // if (isEmptyAbove) {
    //     return;
    // }
    // int maxRadius = 2;
    // outer:
    // for (int r = 1; r <= maxRadius; r++) {
    //     for (int dx = -r; dx <= r; dx++) {
    //         for (int dz = -r; dz <= r; dz++) {
    //             if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
    //             if (r == 2 && Mth.abs(blockState.getSeed(blockPos) % 100) > 80) {
    //                 continue;
    //             }
    //
    //             blockPos.setX(x + dx);
    //             blockPos.setZ(z + dz);
    //             if (mapSlice.getBlockState(blockPos).isSolidRender(mapSlice, blockPos)) {
    //                 findSolid = true;
    //                 break outer;
    //             }
    //         }
    //     }
    // }
    // findSolid = true;
    //     blockPos.set(x, y, z);
}
