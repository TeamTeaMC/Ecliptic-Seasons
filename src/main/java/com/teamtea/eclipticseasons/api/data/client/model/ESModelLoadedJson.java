package com.teamtea.eclipticseasons.api.data.client.model;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;

import java.util.Optional;

@Builder
@Data
public class ESModelLoadedJson {

    public static final String ALL_VARIANT = "";

    @Builder.Default
    public final Optional<BlockStateModelDispatcher.SimpleModelSelectors> variants = Optional.empty();
    @Builder.Default
    public final Optional<BlockStateModelDispatcher.MultiPartDefinition> multiPart = Optional.empty();
    @Builder.Default
    public final ESBlockModelDefinition customDefinition = ESBlockModelDefinition.builder().build();

    public static final com.mojang.serialization.MapCodec<ESModelLoadedJson> CODEC = RecordCodecBuilder.<ESModelLoadedJson>mapCodec(
                    i -> i.group(
                                    BlockStateModelDispatcher.SimpleModelSelectors.CODEC.optionalFieldOf("variants").forGetter(ESModelLoadedJson::getVariants),
                                    BlockStateModelDispatcher.MultiPartDefinition.CODEC.optionalFieldOf("multipart").forGetter(ESModelLoadedJson::getMultiPart),
                                    ESBlockModelDefinition.CODEC.forGetter(ESModelLoadedJson::getCustomDefinition)
                            )
                            .apply(i, ESModelLoadedJson::new)
            )
            .validate(
                    o -> o.getVariants().isEmpty() && o.getMultiPart().isEmpty()
                            ? DataResult.error(() -> "Neither 'variants' nor 'multipart' found")
                            : DataResult.success(o)
            );
}
