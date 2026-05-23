package com.teamtea.eclipticseasons.api.data.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.loot.SeasonCondition;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public record SeasonTrade(SolarTerm start, SolarTerm end,
                              MerchantOffer value) implements VillagerTrades.ItemListing {

        @Override
        public @Nullable MerchantOffer getOffer(Entity trader, @NotNull RandomSource random) {
            SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(trader.level());
            if (!solarTerm.isInTerms(start, end))
                return null;
            return value;
        }
    }

    public static void buildTrades(RegistryAccess registryAccess, List<VillagerTrades.ItemListing> trades) {
        for (SeasonQuest seasonQuest : ESSortInfo.sorted2(registryAccess.registryOrThrow(ESRegistries.SEASON_QUEST)).stream().toList()) {
            MerchantOffer trade = seasonQuest.toTrade();
            trades.add(new SeasonTrade(seasonQuest.start.orElse(SolarTerm.NONE),
                    seasonQuest.end.orElse(SolarTerm.NONE),
                    trade));
        }
    }

    public MerchantOffer toTrade() {
        // if (need.isEmpty() || need.size() > 2) {
        //     throw new IllegalArgumentException("SeasonQuest trade conversion requires 1 or 2 cost items.");
        // }

        if (award.isEmpty()) {
            throw new IllegalArgumentException("SeasonQuest trade conversion requires at least one award item.");
        }

        return new MerchantOffer(
                toTradeCost(need.get(0)),
                need.size() > 1 ? Optional.of(toTradeCost(need.get(1))) : Optional.empty(),
                award.getFirst(),
                max_count.orElse(1),
                100,
                0.0F
        );
    }

    public ItemCost toTradeCost(ItemPredicate predicate) {
        Holder<Item> item = predicate.items()
                .map(HolderSet::stream)
                .flatMap(Stream::findFirst)
                .orElse(Items.EMERALD.builtInRegistryHolder());

        int min = predicate.count()
                .min()
                .orElse(1);

        int max = predicate.count()
                .max()
                .orElse(min);

        return new ItemCost(
                item.value(),
                max
        );
    }

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
