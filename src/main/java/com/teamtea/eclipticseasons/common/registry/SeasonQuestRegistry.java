package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.CropClimateTags;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class SeasonQuestRegistry {
    public static final ResourceKey<SeasonQuest> SPRING_CORE = createKey("spring_core");
    public static final ResourceKey<SeasonQuest> SUMMER_CORE = createKey("summer_core");
    public static final ResourceKey<SeasonQuest> AUTUMN_CORE = createKey("autumn_core");
    public static final ResourceKey<SeasonQuest> WINTER_CORE = createKey("winter_core");

    private static ResourceKey<SeasonQuest> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_QUEST, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<SeasonQuest> context) {
        var cropClimateTypeHolderGetter = context.lookup(ESRegistries.AGRO_CLIMATE);
        HolderGetter<Item> itemHolderGetter = context.lookup(Registries.ITEM);
        context.register(SPRING_CORE, SeasonQuest.builder()
                .setTittle(Component.translatable(ESRegistries.createLangKey(SPRING_CORE)))
                .setStart(SolarTerm.SPRING_EQUINOX)
                .setEnd(SolarTerm.BEGINNING_OF_SUMMER)
                .addNeed(itemHolderGetter, Items.WHEAT, 3 * 64)
                .addAward(ItemRegistry.spring_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.SPRING.getTextColor().getValue())
                .build());
        context.register(SUMMER_CORE, SeasonQuest.builder()
                .setTittle(Component.translatable(ESRegistries.createLangKey(SUMMER_CORE)))
                .setStart(SolarTerm.SUMMER_SOLSTICE)
                .setEnd(SolarTerm.BEGINNING_OF_AUTUMN)
                .addNeed(itemHolderGetter, Items.MELON, 48)
                .addAward(ItemRegistry.summer_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.SUMMER.getTextColor().getValue())
                .build());
        context.register(AUTUMN_CORE, SeasonQuest.builder()
                .setTittle(Component.translatable(ESRegistries.createLangKey(AUTUMN_CORE)))
                .setStart(SolarTerm.AUTUMNAL_EQUINOX)
                .setEnd(SolarTerm.BEGINNING_OF_WINTER)
                .addNeed(itemHolderGetter, Items.PUMPKIN, 96)
                .addNeed(itemHolderGetter, Items.CARROT, 64)
                .addNeed(itemHolderGetter, Items.BEETROOT, 64)
                .addAward(ItemRegistry.autumn_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.AUTUMN.getTextColor().getValue())
                .build());
        context.register(WINTER_CORE, SeasonQuest.builder()
                .setTittle(Component.translatable(ESRegistries.createLangKey(WINTER_CORE)))
                .setStart(SolarTerm.WINTER_SOLSTICE)
                .setEnd(SolarTerm.BEGINNING_OF_SPRING)
                .addNeed(itemHolderGetter, Items.SWEET_BERRIES, 96)
                .addAward(ItemRegistry.winter_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.WINTER.getTextColor().getValue())
                .build());
    }
}
