package com.teamtea.eclipticseasons.api.data.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Data
@Builder
public class ESBlockModelDefinition implements CustomBlockModelDefinition {

    public static final MapCodec<ESBlockModelDefinition> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(o -> o.replace),
            CodecUtil.listFrom(Codec.STRING).optionalFieldOf("require", List.of()).forGetter(c -> c.require)
    ).apply(ins, ESBlockModelDefinition::new));

    @Builder.Default
    private final boolean replace = false;
    @Singular("requirement")
    private final List<String> require;

    @Override
    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier) {
        return Map.of();
    }

    @Override
    public @NonNull MapCodec<? extends CustomBlockModelDefinition> codec() {
        return CODEC;
    }
}
