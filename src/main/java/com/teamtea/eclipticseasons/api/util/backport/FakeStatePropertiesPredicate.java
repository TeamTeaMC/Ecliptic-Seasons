package com.teamtea.eclipticseasons.api.util.backport;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

// Backport from StatePropertiesPredicate
public record FakeStatePropertiesPredicate(List<PropertyMatcher> properties) {
    private static final Codec<List<PropertyMatcher>> PROPERTIES_CODEC = Codec.unboundedMap(
                    Codec.STRING, ValueMatcher.CODEC
            )
            .xmap(
                    map -> map.entrySet()
                            .stream()
                            .map(p_297914_ -> new PropertyMatcher(p_297914_.getKey(), p_297914_.getValue()))
                            .toList(),
                    p_297915_ -> p_297915_.stream()
                            .collect(Collectors.toMap(PropertyMatcher::name, PropertyMatcher::valueMatcher))
            );
    public static final Codec<FakeStatePropertiesPredicate> CODEC = PROPERTIES_CODEC.xmap(FakeStatePropertiesPredicate::new, FakeStatePropertiesPredicate::properties);

    public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> properties, S targetProperty) {
        for (PropertyMatcher statepropertiespredicate$propertymatcher : this.properties) {
            if (!statepropertiespredicate$propertymatcher.match(properties, targetProperty)) {
                return false;
            }
        }
        return true;
    }

    public boolean matches(BlockState state) {
        return this.matches(state.getBlock().getStateDefinition(), state);
    }


    public static class Builder {
        private final ImmutableList.Builder<PropertyMatcher> matchers = ImmutableList.builder();

        private Builder() {
        }

        public static Builder properties() {
            return new Builder();
        }

        public Builder hasProperty(Property<?> property, Object value) {
            this.matchers.add(new PropertyMatcher(property.getName(), new ExactMatcher(value.toString())));
            return this;
        }

        public FakeStatePropertiesPredicate build() {
            return new FakeStatePropertiesPredicate(this.matchers.build());
        }
    }

    static record ExactMatcher(String value) implements ValueMatcher {
        public static final Codec<ExactMatcher> CODEC = Codec.STRING
                .xmap(ExactMatcher::new, ExactMatcher::value);

        @Override
        public <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_298379_, Property<T> p_299294_) {
            T t = p_298379_.getValue(p_299294_);
            Optional<T> optional = p_299294_.getValue(this.value);
            return optional.isPresent() && t.compareTo(optional.get()) == 0;
        }
    }

    static record PropertyMatcher(String name, ValueMatcher valueMatcher) {

        public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> properties, S propertyToMatch) {
            Property<?> property = properties.getProperty(this.name);
            return property != null && this.valueMatcher.match(propertyToMatch, property);
        }

        public Optional<String> checkState(StateDefinition<?, ?> state) {
            Property<?> property = state.getProperty(this.name);
            return property != null ? Optional.empty() : Optional.of(this.name);
        }
    }

    static record RangedMatcher(Optional<String> minValue,
                                Optional<String> maxValue) implements ValueMatcher {
        public static final Codec<RangedMatcher> CODEC = RecordCodecBuilder.create(
                p_337397_ -> p_337397_.group(
                                Codec.STRING.optionalFieldOf("min").forGetter(RangedMatcher::minValue),
                                Codec.STRING.optionalFieldOf("max").forGetter(RangedMatcher::maxValue)
                        )
                        .apply(p_337397_, RangedMatcher::new)
        );

        @Override
        public <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property) {
            T t = stateHolder.getValue(property);
            if (this.minValue.isPresent()) {
                Optional<T> optional = property.getValue(this.minValue.get());
                if (optional.isEmpty() || t.compareTo(optional.get()) < 0) {
                    return false;
                }
            }

            if (this.maxValue.isPresent()) {
                Optional<T> optional1 = property.getValue(this.maxValue.get());
                if (optional1.isEmpty() || t.compareTo(optional1.get()) > 0) {
                    return false;
                }
            }

            return true;
        }
    }

    interface ValueMatcher {
        Codec<ValueMatcher> CODEC = Codec.either(
                        ExactMatcher.CODEC,
                        RangedMatcher.CODEC
                )
                .xmap(
                        either -> either.map(Function.identity(), Function.identity()),
                        p_299089_ -> {
                            if (p_299089_ instanceof ExactMatcher statepropertiespredicate$exactmatcher) {
                                return Either.left(statepropertiespredicate$exactmatcher);
                            } else if (p_299089_ instanceof RangedMatcher statepropertiespredicate$rangedmatcher) {
                                return Either.right(statepropertiespredicate$rangedmatcher);
                            } else {
                                throw new UnsupportedOperationException();
                            }
                        });

        <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property);
    }
}
