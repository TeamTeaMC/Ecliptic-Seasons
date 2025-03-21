package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.HumidityControlProvider;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HumidityControlBlockEntity extends SyncBlockEntity {
    protected HumidityControl humidityControl;
    protected int time = 0;

    public HumidityControlBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.humidity_control_entity_type.get(), pos, state);
    }

    public HumidityControlBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("time", time);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        time = tag.getInt("time");
    }

    @Override
    public void setRemoved() {
        SolarDataManager manager = SolarHolders.getSaveData(level);
        if (manager != null) {
            manager.removeHumidityControlProvider(getBlockPos());
        }
        super.setRemoved();
    }

    protected boolean isRecipeCacheValid(@NotNull HumidityControl humidityControl) {
        if (level instanceof ServerLevel serverLevel) {
            for (PosAndBlockStateCheck c : humidityControl.checks()) {
                if (!c.matches(serverLevel, getBlockPos())) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, HumidityControlBlockEntity blockEntity) {
        SolarDataManager manager = SolarHolders.getSaveData(level);
        int hl = blockEntity.getHumidityModifiedLevel();
        float rl = blockEntity.getHumidityModifiedRange() * blockEntity.getHumidityModifiedRange();
        if (manager != null) {
            HumidityControlProvider nearHumidityControlProvider = manager.queryHumidityControlProvider(blockPos);
            if (nearHumidityControlProvider != null
                    && hl == nearHumidityControlProvider.getLevel()
                    && rl == nearHumidityControlProvider.getRange()
            ) {
                if (nearHumidityControlProvider.getRemainTime() < 100000) {
                    nearHumidityControlProvider.addRemainTime(100);
                }
            } else {
                if (hl != 0)
                    manager.addMap(blockPos, new HumidityControlProvider(hl, rl, 500));
            }
        }


        blockEntity.endTick();

        blockEntity.searchRecipe();
    }

    protected void searchRecipe() {
        if (humidityControl == null) {
            for (HumidityControl humidityControl : level.registryAccess().registryOrThrow(ESRegistries.HUMIDITY_CONTROL)) {
                if (isRecipeCacheValid(humidityControl)
                ) {
                    this.humidityControl = humidityControl;
                    this.time = humidityControl.lasting_time();
                    setChanged();
                    break;
                }
            }
        }
    }

    protected boolean hasNoRecipe() {
        return this.humidityControl == null;
    }

    protected void endTick() {
        if (this.humidityControl != null && this.time <= 0) {
            endRecipe();
        } else {
            if (this.humidityControl != null && isRecipeCacheValid(this.humidityControl)) {
                this.time--;
                setChanged();
            } else {
                if (this.humidityControl != null || this.time > 0)
                    resetRecipe();
            }
        }
    }

    protected void endRecipe() {
        this.humidityControl = null;
        this.time = 0;
        setChanged();
    }

    protected void resetRecipe() {
        this.humidityControl = null;
        this.time = 0;
        setChanged();
    }

    public int getHumidityModifiedLevel() {
        return this.humidityControl != null
                && isRecipeCacheValid(this.humidityControl) ?
                this.humidityControl.level() : 0;
    }

    public int getHumidityModifiedRange() {
        return this.humidityControl != null
                && isRecipeCacheValid(this.humidityControl) ?
                this.humidityControl.range() : 0;
    }
}
