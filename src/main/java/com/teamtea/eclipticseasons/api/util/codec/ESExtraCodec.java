package com.teamtea.eclipticseasons.api.util.codec;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ESExtraCodec {

    public static final StringRepresentable.EnumCodec<SolarTerm> SOLAR_TERM = StringRepresentable.fromEnum(SolarTerm::collectValues);

    public static final StringRepresentable.EnumCodec<Season> SEASON = StringRepresentable.fromEnum(Season::collectValues);

    public static final StringRepresentable.EnumCodec<Humidity> HUMIDITY = StringRepresentable.fromEnum(Humidity::collectValues);


    public static final StringRepresentable.EnumCodec<TimePeriod> TIME_PERIOD = StringRepresentable.fromEnum(TimePeriod::collectValues);


    public static final Codec<HolderSet<Block>> BLOCK_HOLDER_SET_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(b -> Optional.ofNullable(b))
            ).apply(instance, b -> b.orElseGet(HolderSet::direct))
    );

    public static final Codec<HolderSet<Biome>> BIOME_HOLDER_SET_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(b -> Optional.ofNullable(b))
            ).apply(instance, b -> b.orElseGet(HolderSet::direct))
    );

    public record FakeBlockPredicate(Optional<HolderSet<Block>> blocks, Optional<StatePropertiesPredicate> properties) {
        public static final Codec<FakeBlockPredicate> CODEC = RecordCodecBuilder.create(
                p_337342_ -> p_337342_.group(
                                RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(FakeBlockPredicate::blocks),
                                StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FakeBlockPredicate::properties)
                        )
                        .apply(p_337342_, FakeBlockPredicate::new)
        );

        private static final Codec<List<StatePropertiesPredicate.PropertyMatcher>> PROPERTIES_CODEC = Codec.unboundedMap(
                        Codec.STRING, StatePropertiesPredicate.ValueMatcher.CODEC
                )
                .xmap(
                        p_297916_ -> p_297916_.entrySet()
                                .stream()
                                .map(p_297914_ -> new StatePropertiesPredicate.PropertyMatcher(p_297914_.getKey(), p_297914_.getValue()))
                                .toList(),
                        p_297915_ -> p_297915_.stream()
                                .collect(Collectors.toMap(StatePropertiesPredicate.PropertyMatcher::name, StatePropertiesPredicate.PropertyMatcher::valueMatcher))
                );

    }

    static {

    }

    public record StatePropertiesPredicate(List<StatePropertiesPredicate.PropertyMatcher> properties) {
        private static final Codec<List<StatePropertiesPredicate.PropertyMatcher>> PROPERTIES_CODEC = Codec.unboundedMap(
                        Codec.STRING, StatePropertiesPredicate.ValueMatcher.CODEC
                )
                .xmap(
                        p_297916_ -> p_297916_.entrySet()
                                .stream()
                                .map(p_297914_ -> new StatePropertiesPredicate.PropertyMatcher(p_297914_.getKey(), p_297914_.getValue()))
                                .toList(),
                        p_297915_ -> p_297915_.stream()
                                .collect(Collectors.toMap(StatePropertiesPredicate.PropertyMatcher::name, StatePropertiesPredicate.PropertyMatcher::valueMatcher))
                );
        public static final Codec<StatePropertiesPredicate> CODEC = PROPERTIES_CODEC.xmap(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);

        public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> properties, S targetProperty) {
            for (StatePropertiesPredicate.PropertyMatcher statepropertiespredicate$propertymatcher : this.properties) {
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
            private final ImmutableList.Builder<StatePropertiesPredicate.PropertyMatcher> matchers = ImmutableList.builder();

            private Builder() {
            }

            public static StatePropertiesPredicate.Builder properties() {
                return new StatePropertiesPredicate.Builder();
            }

            public StatePropertiesPredicate.Builder hasProperty(Property<?> property, Object value) {
                this.matchers.add(new StatePropertiesPredicate.PropertyMatcher(property.getName(), new StatePropertiesPredicate.ExactMatcher(value.toString())));
                return this;
            }

            public StatePropertiesPredicate build() {
                return new StatePropertiesPredicate(this.matchers.build());
            }
        }

        static record ExactMatcher(String value) implements StatePropertiesPredicate.ValueMatcher {
            public static final Codec<StatePropertiesPredicate.ExactMatcher> CODEC = Codec.STRING
                    .xmap(StatePropertiesPredicate.ExactMatcher::new, StatePropertiesPredicate.ExactMatcher::value);

            @Override
            public <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_298379_, Property<T> p_299294_) {
                T t = p_298379_.getValue(p_299294_);
                Optional<T> optional = p_299294_.getValue(this.value);
                return optional.isPresent() && t.compareTo(optional.get()) == 0;
            }
        }

        static record PropertyMatcher(String name, StatePropertiesPredicate.ValueMatcher valueMatcher) {

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
                                    Optional<String> maxValue) implements StatePropertiesPredicate.ValueMatcher {
            public static final Codec<StatePropertiesPredicate.RangedMatcher> CODEC = RecordCodecBuilder.create(
                    p_337397_ -> p_337397_.group(
                                    Codec.STRING.optionalFieldOf("min").forGetter(StatePropertiesPredicate.RangedMatcher::minValue),
                                    Codec.STRING.optionalFieldOf("max").forGetter(StatePropertiesPredicate.RangedMatcher::maxValue)
                            )
                            .apply(p_337397_, StatePropertiesPredicate.RangedMatcher::new)
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
            Codec<StatePropertiesPredicate.ValueMatcher> CODEC = Codec.either(
                            StatePropertiesPredicate.ExactMatcher.CODEC,
                            StatePropertiesPredicate.RangedMatcher.CODEC
                    )
                    .xmap(
                            either -> either.map(Function.identity(), Function.identity()),
                            p_299089_ -> {
                                if (p_299089_ instanceof StatePropertiesPredicate.ExactMatcher statepropertiespredicate$exactmatcher) {
                                    return Either.left(statepropertiespredicate$exactmatcher);
                                } else if (p_299089_ instanceof StatePropertiesPredicate.RangedMatcher statepropertiespredicate$rangedmatcher) {
                                    return Either.right(statepropertiespredicate$rangedmatcher);
                                } else {
                                    throw new UnsupportedOperationException();
                                }
                            });

            <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property);
        }
    }
}
