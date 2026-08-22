package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import me.cortex.voxy.client.core.model.ModelFactory;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ModelFactory.class)
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public interface ModelFactoryInvoker {

    @Invoker("getTintSources")
    static List<BlockTintSource> eclipticseasons$getTintSources(BlockState state) {
        throw new AssertionError();
    }

    @Invoker("captureColourConstant")
    static int eclipticseasons$captureColourConstant(
            List<BlockTintSource> tintSources,
            BlockState state,
            Biome biome
    ) {
        throw new AssertionError();
    }
}
