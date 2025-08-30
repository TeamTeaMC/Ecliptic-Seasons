package com.teamtea.eclipticseasons.api.data.season.definition.selector;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import com.teamtea.eclipticseasons.api.data.season.definition.condition.IChangeCondition;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Builder
@Data
public class BlockSelector implements IChangeSelector {

    public static final MapCodec<BlockSelector> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            BlockState.CODEC.optionalFieldOf("block").forGetter(o -> o.state),
            Codec.BOOL.optionalFieldOf("replace").forGetter(o -> o.replace),
            Codec.BOOL.optionalFieldOf("copy_state", false).forGetter(o -> o.copyState),
            Codec.STRING.listOf().optionalFieldOf("copy_properties").forGetter(o -> o.copyStateProperties),
            Codec.INT.optionalFieldOf("weight", IChangeSelector.DEFAULT_WEIGHT).forGetter(o -> o.weight),
            CodecUtil.listFrom(IChangeCondition.CODEC).optionalFieldOf("conditions", List.of()).forGetter(o -> o.conditions),
            Vec3i.CODEC.optionalFieldOf("offset").forGetter(o -> o.offset),
            ResourceLocation.CODEC.optionalFieldOf("loot").forGetter(o -> o.loot)
    ).apply(ins, BlockSelector::new));
    @Builder.Default
    private final Optional<BlockState> state = Optional.empty();
    @Builder.Default
    private final Optional<Boolean> replace = Optional.empty();
    @Builder.Default
    private final boolean copyState = false;
    @Builder.Default
    private final Optional<List<String>> copyStateProperties = Optional.empty();

    @Builder.Default
    private final int weight = IChangeSelector.DEFAULT_WEIGHT;
    @Singular
    private final List<IChangeCondition> conditions;
    @Builder.Default
    private final Optional<Vec3i> offset = Optional.empty();
    @Builder.Default
    private final Optional<ResourceLocation> loot = Optional.empty();


    @Override
    public ResourceLocation getType() {
        return ChangeSelectors.BLOCK;
    }

    @Override
    public MapCodec<? extends IChangeSelector> codec() {
        return CODEC;
    }

    @Override
    public boolean place(ServerLevel level, BlockPos newpos, ISeasonChangeContext context) {
        boolean applied = false;
        if (state.isEmpty()) {
            applied = level.removeBlock(newpos, false);
        } else {
            BlockState newState = state.get();
            newpos = offset.isEmpty() ? newpos : newpos.offset(offset.get());
            if (offset.isEmpty()) {
                BlockState oldState = level.getBlockState(newpos);
                if (copyState) {
                    Set<String> propertyNameList = copyStateProperties.map(HashSet::new).orElse(null);
                    for (Property<?> property : oldState.getProperties()) {
                        if (newState.hasProperty(property) && (propertyNameList == null || propertyNameList.contains(property.getName()))) {
                            newState = newState.setValue((Property) property, oldState.getValue(property));
                        }
                    }
                }
                applied = NaturalPlantHandler.setBlockAndSelfCheck(level, newpos, newState, oldState);
            } else if (replace.isEmpty()) {
                BlockState oldState = level.getBlockState(newpos);
                if (oldState.canBeReplaced(Fluids.EMPTY))
                    applied = NaturalPlantHandler.setBlockAndSelfCheck(level, newpos, newState);
            } else if (replace.get() || level.isEmptyBlock(newpos)) {
                applied = NaturalPlantHandler.setBlockAndSelfCheck(level, newpos, newState);
            }
        }
        return applied;
    }
}
