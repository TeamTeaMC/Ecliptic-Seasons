package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends SyncBlockEntity {

    private boolean shaking = false;

    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.wind_chimes_entity_type.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("shaking", shaking);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        shaking = tag.getBoolean("shaking");
    }

    public boolean isShaking() {
        return shaking;
    }

    public void setShaking(boolean shaking) {
        this.shaking = shaking;
        inventoryChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ClientCon.agent.loadWindChime(this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
    }
}
