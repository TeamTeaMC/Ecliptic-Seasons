package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;

public record GrowParameter(
        float grow_chance,
        float death_chance,
        float fertile_chance,
        Optional<BlockState> deadState
) {

    public static final Codec<GrowParameter> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.optionalFieldOf("grow_chance",1f).forGetter(GrowParameter::grow_chance),
            Codec.FLOAT.optionalFieldOf("death_chance",0f).forGetter(GrowParameter::death_chance),
            Codec.FLOAT.optionalFieldOf("fertile_chance",1f).forGetter(GrowParameter::fertile_chance),
            BlockState.CODEC.optionalFieldOf("dead_state").forGetter(GrowParameter::deadState)
    ).apply(ins, (GrowParameter::new)));

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float growChance = 1.0f;
        private float deathChance = 0.0f;
        private float fertileChance = 1.0f;
        private Optional<BlockState> deadState = Optional.empty();

        public Builder growChance(float growChance) {
            this.growChance = growChance;
            return this;
        }

        public Builder deathChance(float deathChance) {
            this.deathChance = deathChance;
            return this;
        }

        public Builder fertileChance(float fertileChance) {
            this.fertileChance = fertileChance;
            return this;
        }

        public Builder deadState(BlockState state) {
            this.deadState = Optional.ofNullable(state);
            return this;
        }

        public Builder deadState(Optional<BlockState> deadState) {
            this.deadState = deadState;
            return this;
        }

        public GrowParameter end() {
            return new GrowParameter(
                    growChance,
                    deathChance,
                    fertileChance,
                    deadState
            );
        }
    }
}
