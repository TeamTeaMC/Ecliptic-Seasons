package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.base.SimpleHumidityProviderBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.HumidityModifierBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HumidityTankBlock extends SimpleEntityBlock implements SimpleHumidityProviderBlock {

    public HumidityTankBlock(Properties properties) {
        super(properties);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return BlockEntityRegistry.humidity_modifier.get().create(worldPosition, blockState);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, BlockState state, BlockEntityType<T> blockEntityType) {
        return !worldIn.isClientSide() ?
                SimpleEntityBlock.createTickerHelper(blockEntityType, BlockEntityRegistry.humidity_modifier.get(), HumidityModifierBlockEntity::tick) : null;
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

    public float getHumidityModifiedLevel() {
        return 0.75f;
    }

    public float getHumidityModifiedRange() {
        return CommonConfig.Crop.humidityTankRange.get().floatValue();
    }
}
