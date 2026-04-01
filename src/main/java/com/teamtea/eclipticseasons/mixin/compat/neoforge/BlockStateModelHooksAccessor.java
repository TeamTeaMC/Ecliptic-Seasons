package com.teamtea.eclipticseasons.mixin.compat.neoforge;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.client.model.block.BlockStateModelHooks;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStateModelHooks.class)
public interface BlockStateModelHooksAccessor {

    @Accessor("BLOCK_MODEL_DEFINITION_IDS")
    static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends CustomBlockModelDefinition>> getBlockModelDefinitionIds() {
        throw new AssertionError();
    }
}
