package com.teamtea.eclipticseasons.client.model.block.unbake.multipart;

import com.google.common.base.Splitter;
import lombok.Getter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Predicate;

@Getter
public class KeyValueConditionLike implements ConditionLike {

    public static final Splitter PIPE_SPLITTER = Splitter.on('|').omitEmptyStrings();
    public final String key;
    public final String value;

    public KeyValueConditionLike(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValueConditionLike(Property<?> property, Object object) {
        this(property.getName(), object.toString());
    }

    @Override
    public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> definition) {
        boolean negate = value.startsWith("!");
        String val = negate ? value.substring(1) : value;
        List<String> targetValues = PIPE_SPLITTER.splitToList(val);
        Predicate<S> base = blockState -> {
            String currentValue = getStringValue(blockState, key);
            return targetValues.contains(currentValue);
        };

        return negate ? base.negate() : base;
    }


    private <O, S extends StateHolder<O, S>> String getStringValue(S state, String key) {
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals(key)) {
                Object value = state.getValue(property);
                return String.valueOf(value);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("KeyValueStringCondition{key='%s', value='%s'}", key, value);
    }

}
