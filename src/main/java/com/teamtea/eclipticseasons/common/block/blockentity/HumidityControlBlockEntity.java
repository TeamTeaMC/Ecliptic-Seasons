package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.HumidityControlProvider;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("time", time);
        if (this.humidityControl != null) {
            RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            HumidityControl.CODEC
                    .encodeStart(registryops, this.humidityControl)
                    .resultOrPartial(EclipticSeasons::logger)
                    .ifPresent(tag1 -> tag.put("humidity_control", tag1));
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        time = tag.getInt("time");
        if (tag.contains("humidity_control")) {
            RegistryAccess registryAccess = SimpleUtil.getRegistryAccess(this);
            RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
            HumidityControl.CODEC
                    .parse(registryops, tag.get("humidity_control"))
                    .resultOrPartial(EclipticSeasons::logger)
                    .ifPresent(seasonQuest1 -> {
                        this.humidityControl = seasonQuest1;
                    });
        } else {
            this.humidityControl = null;
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
        float rl = blockEntity.getHumidityModifiedRange();
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
                if (!this.humidityControl.noCost()) {
                    this.time--;
                    setChanged();
                }
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
