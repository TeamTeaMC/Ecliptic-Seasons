package com.teamtea.eclipticseasons.api.data.season.definition.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import com.teamtea.eclipticseasons.api.data.season.definition.condition.IChangeCondition;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

@Builder
@Data
public class FeatureSelector implements IChangeSelector {

    public static final MapCodec<FeatureSelector> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            CodecUtil.holderCodec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter(o -> o.feature),
            Codec.INT.optionalFieldOf("weight", IChangeSelector.DEFAULT_WEIGHT).forGetter(o -> o.weight),
            CodecUtil.listFrom(IChangeCondition.CODEC).optionalFieldOf("conditions",List.of()).forGetter(o -> o.conditions),
            Vec3i.CODEC.optionalFieldOf("offset").forGetter(o -> o.offset),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("loot").forGetter(o -> o.loot)
    ).apply(ins, FeatureSelector::new));


    private final Holder<ConfiguredFeature<?, ?>> feature;

    @Builder.Default
    private final int weight = IChangeSelector.DEFAULT_WEIGHT;
    @Singular
    private final List<IChangeCondition> conditions;
    @Builder.Default
    private final Optional<Vec3i> offset = Optional.empty();
    @Builder.Default
    private final Optional<ResourceKey<LootTable>> loot = Optional.empty();

    @Override
    public Identifier getType() {
        return ChangeSelectors.FEATURE;
    }

    @Override
    public MapCodec<? extends IChangeSelector> codec() {
        return CODEC;
    }

    @Override
    public boolean place(ServerLevel level, BlockPos origin, ISeasonChangeContext context) {
        return feature.value().place(level, level.getChunkSource().getGenerator(), level.getRandom(),
                offset.map(origin::offset).orElse(origin));
    }
}
