package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.HumidityControlProvider;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HumidityTankBlockEntity extends SyncBlockEntity {
    public HumidityTankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.humidity_tank.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, HumidityTankBlockEntity blockEntity) {
        SolarDataManager manager = SolarHolders.getSaveData(level);
        float hl = 0.75f;
        float rl = 4.5f;
        if (manager != null) {
            HumidityControlProvider nearHumidityControlProvider = manager.queryHumidityControlProvider(blockPos);
            if (nearHumidityControlProvider != null
                    && hl == nearHumidityControlProvider.getLevel()
                    && rl == nearHumidityControlProvider.getRange()
            ) {
                if (nearHumidityControlProvider.getRemainTime() < 10) {
                    nearHumidityControlProvider.addRemainTime(100);
                }
            } else {
                if (hl != 0) {
                    manager.addHumidityControlProvider(blockPos, new HumidityControlProvider(hl, rl, 240));
                } else if (nearHumidityControlProvider != null) {
                    manager.removeHumidityControlProvider(blockPos);
                }
            }
        }
    }

    @Override
    public void setRemoved() {
        SolarDataManager manager = SolarHolders.getSaveData(level);
        if (manager != null) {
            manager.removeHumidityControlProvider(getBlockPos());
        }
        super.setRemoved();
    }
}
