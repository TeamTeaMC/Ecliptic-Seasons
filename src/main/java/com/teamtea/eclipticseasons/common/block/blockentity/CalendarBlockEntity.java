package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public class CalendarBlockEntity extends SyncBlockEntity {
    public CalendarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.calendar_entity_type.get(), pos, state);
    }

    private boolean init = false;
    private Holder<Biome> biome = null;

    public Holder<Biome> getBiome() {
        return biome;
    }

    public void setBiome(Holder<Biome> biome) {
        this.biome = biome;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
