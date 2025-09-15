package com.teamtea.eclipticseasons.mixin.common.chunk;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LevelChunkSection.class})
public interface MixinAccessorLevelChunkSection {
    @Accessor("states")
    PalettedContainer<BlockState> es$getStates();
}
