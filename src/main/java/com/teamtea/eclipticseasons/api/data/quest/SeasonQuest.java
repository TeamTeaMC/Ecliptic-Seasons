package com.teamtea.eclipticseasons.api.data.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SeasonQuest(
        Optional<SolarTerm> end,
        Optional<SolarTerm> start,
        List<WarpItemPredicate> need,
        List<ItemStack> award,
        Optional<String> tittle,
        Optional<List<String>> description,
        Optional<HolderSet<AgroClimaticZone>> climate,
        Optional<Integer> max_count,
        Optional<Integer> seasonal_count,
        Optional<Integer> weight,
        Optional<Boolean> glowing,
        Optional<Integer> color
) {

    public static final Codec<String> TRANSLATE_CODEC = RecordCodecBuilder.create(ins -> ins.group(
                    Codec.STRING.fieldOf("translate").forGetter(c -> c))
            .apply(ins, String::new));

    public static final Codec<SeasonQuest> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            StringRepresentable.fromEnum(SolarTerm::collectValues).optionalFieldOf("end").forGetter(SeasonQuest::end),
            StringRepresentable.fromEnum(SolarTerm::collectValues).optionalFieldOf("start").forGetter(SeasonQuest::start),
            WarpItemPredicate.CODEC.listOf().fieldOf("need").forGetter(SeasonQuest::need),
            ItemStack.CODEC.listOf().fieldOf("award").forGetter(SeasonQuest::award),
            TRANSLATE_CODEC.optionalFieldOf("tittle").forGetter(SeasonQuest::tittle),
            TRANSLATE_CODEC.listOf().optionalFieldOf("description").forGetter(SeasonQuest::description),
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(SeasonQuest::climate),
            Codec.INT.optionalFieldOf("max_count").forGetter(SeasonQuest::max_count),
            Codec.INT.optionalFieldOf("seasonal_count").forGetter(SeasonQuest::seasonal_count),
            Codec.INT.optionalFieldOf("weight").forGetter(SeasonQuest::weight),
            Codec.BOOL.optionalFieldOf("glowing").forGetter(SeasonQuest::glowing),
            Codec.INT.optionalFieldOf("color").forGetter(SeasonQuest::color)
    ).apply(ins, SeasonQuest::new));

    public static final Codec<SeasonQuest> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            StringRepresentable.fromEnum(SolarTerm::collectValues).optionalFieldOf("end").forGetter(SeasonQuest::end),
            StringRepresentable.fromEnum(SolarTerm::collectValues).optionalFieldOf("start").forGetter(SeasonQuest::start),
            TRANSLATE_CODEC.optionalFieldOf("tittle").forGetter(SeasonQuest::tittle),
            TRANSLATE_CODEC.listOf().optionalFieldOf("description").forGetter(SeasonQuest::description),
            Codec.INT.optionalFieldOf("max_count").forGetter(SeasonQuest::max_count),
            Codec.INT.optionalFieldOf("seasonal_count").forGetter(SeasonQuest::seasonal_count),
            Codec.INT.optionalFieldOf("weight").forGetter(SeasonQuest::weight),
            Codec.BOOL.optionalFieldOf("glowing").forGetter(SeasonQuest::glowing),
            Codec.INT.optionalFieldOf("color").forGetter(SeasonQuest::color)
    ).apply(ins, (solarTerm, solarTerm2, s, strings, integer, integer2, integer3, aBoolean, integer4) ->
            new SeasonQuest(solarTerm, solarTerm2, List.of(), List.of(), s, strings, Optional.empty(), integer, integer2, integer3, aBoolean, integer4)));


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private SolarTerm end;
        private SolarTerm start;
        private List<WarpItemPredicate> need = new ArrayList<>();
        private List<ItemStack> award = new ArrayList<>();
        private String tittle;
        private List<String> description = new ArrayList<>();
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

        public Builder addNeed(WarpItemPredicate need) {
            this.need.add(need);
            return this;
        }

        public Builder addNeed(Holder<Item> need, int count) {
            this.need.add(new WarpItemPredicate(HolderSet.direct(need), count));
            return this;
        }

        public Builder addNeed(HolderSet<Item> need, int count) {
            this.need.add(new WarpItemPredicate(need, count));
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

        public Builder setTittle(String tittle) {
            this.tittle = tittle;
            return this;
        }

        public Builder addDescription(String description) {
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
