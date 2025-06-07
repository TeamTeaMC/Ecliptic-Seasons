package com.teamtea.eclipticseasons.api.data.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SeasonQuest(
        Optional<SolarTerm> end,
        Optional<SolarTerm> start,
        List<ItemPredicate> need,
        List<ItemStack> award,
        Optional<Component> tittle,
        Optional<List<Component>> description,
        Optional<HolderSet<AgroClimaticZone>> climate,
        Optional<Integer> max_count,
        Optional<Integer> seasonal_count,
        Optional<Integer> weight,
        Optional<Boolean> glowing,
        Optional<Integer> color
) {


    public static final Codec<SeasonQuest> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ESExtraCodec.SOLAR_TERM.optionalFieldOf("end").forGetter(SeasonQuest::end),
            ESExtraCodec.SOLAR_TERM.optionalFieldOf("start").forGetter(SeasonQuest::start),
            ItemPredicate.CODEC.listOf().fieldOf("need").forGetter(SeasonQuest::need),
            ItemStack.CODEC.listOf().fieldOf("award").forGetter(SeasonQuest::award),
            ComponentSerialization.CODEC.optionalFieldOf("tittle").forGetter(SeasonQuest::tittle),
            ComponentSerialization.CODEC.listOf().optionalFieldOf("description").forGetter(SeasonQuest::description),
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(SeasonQuest::climate),
            Codec.INT.optionalFieldOf("max_count").forGetter(SeasonQuest::max_count),
            Codec.INT.optionalFieldOf("seasonal_count").forGetter(SeasonQuest::seasonal_count),
            Codec.INT.optionalFieldOf("weight").forGetter(SeasonQuest::weight),
            Codec.BOOL.optionalFieldOf("glowing").forGetter(SeasonQuest::glowing),
            Codec.INT.optionalFieldOf("color").forGetter(SeasonQuest::color)
    ).apply(ins, SeasonQuest::new));


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private SolarTerm end;
        private SolarTerm start;
        private List<ItemPredicate> need = new ArrayList<>();
        private List<ItemStack> award = new ArrayList<>();
        private Component tittle;
        private List<Component> description = new ArrayList<>();
        private HolderSet<AgroClimaticZone> climate;
        private Integer max_count;
        private Integer seasonal_count;
        private Integer weight;
        private Boolean glowing;
        private Integer color;

        private Builder() {
        }

        public Builder setEnd(SolarTerm end) {
            this.end = end;
            return this;
        }

        public Builder setStart(SolarTerm start) {
            this.start = start;
            return this;
        }

        public Builder addNeed(ItemPredicate need) {
            this.need.add(need);
            return this;
        }

        public Builder addNeed(Item need, int count) {
            this.need.add(ItemPredicate.Builder.item().of(need).withCount(MinMaxBounds.Ints.exactly(count)).build());
            return this;
        }

        public Builder addNeed(TagKey<Item> need, int count) {
            this.need.add(ItemPredicate.Builder.item().of(need).withCount(MinMaxBounds.Ints.exactly(count)).build());
            return this;
        }

        public Builder addAward(ItemStack award) {
            this.award.add(award);
            return this;
        }

        public Builder addAward(Item award) {
            this.award.add(award.getDefaultInstance());
            return this;
        }

        public Builder setTittle(Component tittle) {
            this.tittle = tittle;
            return this;
        }

        public Builder addDescription(Component description) {
            this.description.add(description);
            return this;
        }

        public Builder setClimate(HolderSet<AgroClimaticZone> climate) {
            this.climate = climate;
            return this;
        }

        public Builder setMaxCount(Integer max_count) {
            this.max_count = max_count;
            return this;
        }

        public Builder setSeasonalCount(Integer seasonal_count) {
            this.seasonal_count = seasonal_count;
            return this;
        }

        public Builder setWeight(Integer weight) {
            this.weight = weight;
            return this;
        }

        public Builder setGlowing(Boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public Builder setColor(Integer color) {
            this.color = color;
            return this;
        }

        public SeasonQuest build() {
            return new SeasonQuest(
                    Optional.ofNullable(end),
                    Optional.ofNullable(start),
                    need,
                    award,
                    Optional.ofNullable(tittle),
                    description.isEmpty() ? Optional.empty() : Optional.of(description),
                    Optional.ofNullable(climate),
                    Optional.ofNullable(max_count),
                    Optional.ofNullable(seasonal_count),
                    Optional.ofNullable(weight),
                    Optional.ofNullable(glowing),
                    Optional.ofNullable(color)
            );
        }
    }

}
