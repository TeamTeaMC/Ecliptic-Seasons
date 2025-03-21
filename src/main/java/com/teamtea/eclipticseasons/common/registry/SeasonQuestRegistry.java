package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.CropClimateTags;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;

public class SeasonQuestRegistry {
    public static final ResourceKey<SeasonQuest> SPRING_CORE = createKey("spring_core");
    public static final ResourceKey<SeasonQuest> SUMMER_CORE = createKey("summer_core");
    public static final ResourceKey<SeasonQuest> AUTUMN_CORE = createKey("autumn_core");
    public static final ResourceKey<SeasonQuest> WINTER_CORE = createKey("winter_core");

    private static ResourceKey<SeasonQuest> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_QUEST, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstapContext<SeasonQuest> context) {
        var cropClimateTypeHolderGetter = context.lookup(ESRegistries.AGRO_CLIMATE);
        context.register(SPRING_CORE, SeasonQuest.builder()
                .setTittle(ESRegistries.createLangKey(SPRING_CORE))
                .setStart(SolarTerm.SPRING_EQUINOX)
                .setEnd(SolarTerm.BEGINNING_OF_SUMMER)
                .addNeed(Items.WHEAT.builtInRegistryHolder(), 640)
                .addAward(ItemRegistry.spring_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.SPRING.getColor().getColor())
                .build());
        context.register(SUMMER_CORE, SeasonQuest.builder()
                .setTittle(ESRegistries.createLangKey(SUMMER_CORE))
                .setStart(SolarTerm.SUMMER_SOLSTICE)
                .setEnd(SolarTerm.BEGINNING_OF_AUTUMN)
                .addNeed(Items.MELON.builtInRegistryHolder(), 320)
                .addAward(ItemRegistry.summer_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.SUMMER.getColor().getColor())
                .build());
        context.register(AUTUMN_CORE, SeasonQuest.builder()
                .setTittle(ESRegistries.createLangKey(AUTUMN_CORE))
                .setStart(SolarTerm.AUTUMNAL_EQUINOX)
                .setEnd(SolarTerm.BEGINNING_OF_WINTER)
                .addNeed(Items.PUMPKIN.builtInRegistryHolder(), 160)
                .addNeed(Items.CARROT.builtInRegistryHolder(), 128)
                .addNeed(Items.BEETROOT.builtInRegistryHolder(), 128)
                .addAward(ItemRegistry.autumn_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.AUTUMN.getColor().getColor())
                .build());
        context.register(WINTER_CORE, SeasonQuest.builder()
                .setTittle(ESRegistries.createLangKey(WINTER_CORE))
                .setStart(SolarTerm.WINTER_SOLSTICE)
                .setEnd(SolarTerm.BEGINNING_OF_SPRING)
                .addNeed(Items.SWEET_BERRIES.builtInRegistryHolder(), 160)
                .addAward(ItemRegistry.winter_greenhouse_essence_item.get())
                .setClimate(cropClimateTypeHolderGetter.getOrThrow(CropClimateTags.ALL))
                .setWeight(10)
                .setGlowing(true)
                .setColor(Season.WINTER.getColor().getColor())
                .build());
    }
}
