package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.base.SimpleHumidityProviderBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.HumidityModifierBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class HumidityTankBlock extends SimpleEntityBlock implements SimpleHumidityProviderBlock {

    public HumidityTankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(HumidityTankBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return BlockEntityRegistry.humidity_modifier.get().create(worldPosition, blockState);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        return !worldIn.isClientSide() ?
                SimpleEntityBlock.createTickerHelper(blockEntityType, BlockEntityRegistry.humidity_modifier.get(), HumidityModifierBlockEntity::tick) : null;
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        // tryMoistenFarmland(level, pos, random);
    }

    private static boolean tryMoistenFarmland(ServerLevel level, BlockPos tankPos, RandomSource random) {
        int radius = 3;
        int maxDepth = 15;

        BlockPos.MutableBlockPos mutable = tankPos.mutable();
        for (int attempt = 0; attempt < 4; attempt++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;

            mutable.set(tankPos.getX() + 0, tankPos.getY(), tankPos.getZ() + 0);

            BlockState targetState = level.getBlockState(mutable);
            while (tankPos.getY() - mutable.getY() > maxDepth
                    || targetState.isAir() || !targetState.blocksMotion()) {
                targetState = level.getBlockState(mutable.setY(mutable.getY() - 1));
            }

            if (targetState.isAir() || !targetState.blocksMotion()) continue;

            if (targetState.getBlock() instanceof FarmlandBlock
                    && targetState.hasProperty(FarmlandBlock.MOISTURE)
                    && targetState.getValue(FarmlandBlock.MOISTURE) < FarmlandBlock.MAX_MOISTURE) {
                level.setBlock(
                        mutable.immutable(),
                        targetState.setValue(FarmlandBlock.MOISTURE, targetState.getValue(FarmlandBlock.MOISTURE) + 1),
                        FarmlandBlock.UPDATE_CLIENTS
                );
                return true;
            }
        }

        return false;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (random.nextFloat() < 0.02F) {
            level.addParticle(
                    ParticleTypes.DRIPPING_WATER,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.05D,
                    pos.getZ() + 0.5D,
                    0.0D, 0.0D, 0.0D
            );
        }
    }

    @Override
    public float getHumidityModifiedLevel() {
        return 0.75f;
    }

    @Override
    public float getHumidityModifiedRange() {
        return CommonConfig.Crop.humidityTankRange.get().floatValue();
    }
}
