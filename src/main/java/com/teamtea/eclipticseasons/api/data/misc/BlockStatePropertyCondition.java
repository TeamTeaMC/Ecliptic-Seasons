package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;

@TestOnly
@Deprecated(forRemoval = true)
public record BlockStatePropertyCondition(
        Optional<TagKey<Block>> tag,
        Optional<Holder<Block>> id, Optional<StatePropertiesPredicate> properties) {
    public static final MapCodec<BlockStatePropertyCondition> CODEC = RecordCodecBuilder.<BlockStatePropertyCondition>mapCodec(
                    instance -> instance.group(
                                    TagKey.codec(Registries.BLOCK).optionalFieldOf("tag").forGetter(BlockStatePropertyCondition::tag),
                                    BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("id").forGetter(BlockStatePropertyCondition::id),
                                    StatePropertiesPredicate.CODEC.optionalFieldOf("properties").forGetter(BlockStatePropertyCondition::properties)
                            )
                            .apply(instance, BlockStatePropertyCondition::new)
            )
            .validate(BlockStatePropertyCondition::validate);

    private static DataResult<BlockStatePropertyCondition> validate(BlockStatePropertyCondition condition) {
        if (condition.tag.isPresent()) return DataResult.success(condition);
        if (condition.id().isEmpty()) return DataResult.error(() -> "Without id or tag in condition");
        return condition.properties()
                .flatMap(p_298822_ -> p_298822_.checkState(condition.id().get().value().getStateDefinition()))
                .map(p_299129_ -> DataResult.<BlockStatePropertyCondition>error(() -> "Block " + condition.id() + " has no property" + p_299129_))
                .orElse(DataResult.success(condition));
    }
    
    public boolean match(BlockState stateTested){
        if (tag().isPresent()) {
            if (!stateTested.is(tag().get())) {
                return false;
            }
        } else {
            if (id().isEmpty()
                    || stateTested.getBlock() != id().get().value()
                    || (properties().isPresent() && !properties().get().matches(stateTested))) {
                return false;
            }
        }
        return true;
    }
}
