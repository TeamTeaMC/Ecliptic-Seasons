package com.teamtea.eclipticseasons.common.block.blockentity;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WindChimesBlockEntity extends SyncBlockEntity {

    private boolean shaking = false;

    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.wind_chimes_entity_type.get(), pos, state);
    }


    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("shaking", Codec.BOOL, shaking);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        shaking = input.read("shaking", Codec.BOOL).orElse(false);
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
