package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.client.model.variant.MultiVariantLike;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SelectorLike extends Selector {

    public static final AndConditionLike EMPTY_CONDITION = new AndConditionLike(List.of());

    public static final Codec<SelectorLike> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ConditionLike.CODEC.optionalFieldOf("when", EMPTY_CONDITION).forGetter(o -> o.condition),
            MultiVariantLike.CODEC.fieldOf("apply").forGetter(o -> o.variant)
    ).apply(ins, SelectorLike::new));
    private final ConditionLike condition;
    private final MultiVariantLike variant;


    public SelectorLike(ConditionLike condition, MultiVariantLike variant) {
        super(condition, variant);
        this.condition = condition;
        this.variant = variant;
    }

    public SelectorLike(MultiVariantLike variant) {
        this(EMPTY_CONDITION, variant);
    }

    @Override
    public @NotNull MultiVariantLike getVariant() {
        return variant;
    }
}
