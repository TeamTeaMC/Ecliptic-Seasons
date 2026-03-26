package com.teamtea.eclipticseasons.common.core.crop.internal;

import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SaplingBlock;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.teamtea.eclipticseasons.common.core.crop.CropInfoManager.createBlockTag;
import static com.teamtea.eclipticseasons.common.core.crop.CropInfoManager.createItemTag;

public class CompatCropHookInternal {

    private static final TagKey<Block> ss1 = createBlockTag("sereneseasons", "spring_crops");
    private static final TagKey<Block> ss2 = createBlockTag("sereneseasons", "summer_crops");
    private static final TagKey<Block> ss3 = createBlockTag("sereneseasons", "autumn_crops");
    private static final TagKey<Block> ss4 = createBlockTag("sereneseasons", "winter_crops");

    private static final TagKey<Item> ssi1 = createItemTag("sereneseasons", "spring_crops");
    private static final TagKey<Item> ssi2 = createItemTag("sereneseasons", "summer_crops");
    private static final TagKey<Item> ssi3 = createItemTag("sereneseasons", "autumn_crops");
    private static final TagKey<Item> ssi4 = createItemTag("sereneseasons", "winter_crops");

    private static final List<Integer> seasonInfoList = List.of(1, 2, 4, 8);
    private static final List<TagKey<Block>> ss_blockList = List.of(ss1, ss2, ss3, ss4);
    private static final List<TagKey<Item>> ss_itemList = List.of(ssi1, ssi2, ssi3, ssi4);


    private static final Map<CropSeasonType, CropHumidityType> SEASON_HUMIDITY_MAP = Map.ofEntries(
            Map.entry(CropSeasonType.SPRING, CropHumidityType.MOIST_HUMID),
            Map.entry(CropSeasonType.SUMMER, CropHumidityType.DRY_AVERAGE),
            Map.entry(CropSeasonType.AUTUMN, CropHumidityType.AVERAGE_MOIST),
            Map.entry(CropSeasonType.WINTER, CropHumidityType.ARID_DRY),
            Map.entry(CropSeasonType.SP_SU, CropHumidityType.AVERAGE_MOIST),
            Map.entry(CropSeasonType.SP_AU, CropHumidityType.AVERAGE_MOIST),
            Map.entry(CropSeasonType.SP_WI, CropHumidityType.DRY_MOIST),
            Map.entry(CropSeasonType.SU_AU, CropHumidityType.DRY_MOIST),
            Map.entry(CropSeasonType.SU_WI, CropHumidityType.ARID_DRY),
            Map.entry(CropSeasonType.AU_WI, CropHumidityType.DRY_AVERAGE),
            Map.entry(CropSeasonType.SP_SU_AU, CropHumidityType.AVERAGE_MOIST),
            Map.entry(CropSeasonType.SP_SU_WI, CropHumidityType.AVERAGE_MOIST),
            Map.entry(CropSeasonType.SP_AU_WI, CropHumidityType.DRY_MOIST),
            Map.entry(CropSeasonType.SU_AU_WI, CropHumidityType.DRY_MOIST),
            Map.entry(CropSeasonType.ALL, CropHumidityType.ARID_HUMID)
    );

    // const names = [...document.querySelectorAll(".react-directory-truncate>a")]
    //     .map(a => a.textContent.replace(".json", "").replace(/^seeds\//, ""));
    //
    //     console.log(names.join(", "));

    public static Map<TagKey<Item>, CropHumidityType> TAG_HUMIDITY_MAP;

    static {
        HashMap<TagKey<Item>, CropHumidityType> builder = new HashMap<>();
        for (String ns : List.of("forge", "c")) {
            builder.put(createItemTag(ns, "seeds/agave"), CropHumidityType.ARID_DRY);
            builder.put(createItemTag(ns, "seeds/amaranth"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/arrowroot"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/artichoke"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/asparagus"), CropHumidityType.DRY_MOIST);

            builder.put(createItemTag(ns, "seeds/barley"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/basil"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/bean"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/beans"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/bellpepper"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/blackbean"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/blackberry"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/blueberry"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/broccoli"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/brusselsprout"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/cabbage"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/cactusfruit"), CropHumidityType.ARID_DRY);
            builder.put(createItemTag(ns, "seeds/candleberry"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/cantaloupe"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/cassava"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/cauliflower"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/celery"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/chickpea"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/chilipepper"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/cinnamon"), CropHumidityType.MOIST_HUMID);

            builder.put(createItemTag(ns, "seeds/coffee_beans"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/coffeebean"), CropHumidityType.MOIST_HUMID);

            builder.put(createItemTag(ns, "seeds/corn"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/cotton"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/cranberry"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/cucumber"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/currant"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/eggplant"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/elderberry"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/flax"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/garlic"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/ginger"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/grape"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/greengrape"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/hops"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/honeydew"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/huckleberry"), CropHumidityType.MOIST_HUMID);

            builder.put(createItemTag(ns, "seeds/jicama"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/juniperberry"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/jute"), CropHumidityType.MOIST_HUMID);

            builder.put(createItemTag(ns, "seeds/kale"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/kenaf"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/kiwi"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/kohlrabi"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/leek"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/lentil"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/lettuce"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/millet"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/mustardseeds"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/mulberry"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/oat"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/okra"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/olive"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/onion"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/parsnip"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/pea"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/peanut"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/pepper"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/pineapple"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/quinoa"), CropHumidityType.DRY_AVERAGE);

            builder.put(createItemTag(ns, "seeds/radish"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/raspberry"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/rhubarb"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/rice"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/rutabaga"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/rye"), CropHumidityType.DRY_AVERAGE);

            builder.put(createItemTag(ns, "seeds/saguaro"), CropHumidityType.ARID_DRY);
            builder.put(createItemTag(ns, "seeds/sesameseeds"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/sisal"), CropHumidityType.DRY_AVERAGE);
            builder.put(createItemTag(ns, "seeds/sorghum"), CropHumidityType.DRY);
            builder.put(createItemTag(ns, "seeds/soybean"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/spiceleaf"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/spinach"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/strawberry"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/sweet_potato"), CropHumidityType.AVERAGE_MOIST);

            builder.put(createItemTag(ns, "seeds/taro"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/tea"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/tea_leaves"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/tealeaf"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/tomatillo"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/tomato"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/turmeric"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/turnip"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/vanilla"), CropHumidityType.MOIST_HUMID);

            builder.put(createItemTag(ns, "seeds/waterchestnut"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/whitemushroom"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/wintersquash"), CropHumidityType.AVERAGE_MOIST);
            builder.put(createItemTag(ns, "seeds/yam"), CropHumidityType.MOIST_HUMID);
            builder.put(createItemTag(ns, "seeds/zucchini"), CropHumidityType.AVERAGE_MOIST);
        }
        CompatCropHookInternal.TAG_HUMIDITY_MAP = builder;
    }

    public static Map<Item, CropHumidityType> getBultinHumidityTypeMap(HolderLookup.RegistryLookup<Item> registry) {
        return registry.listTags()
                .flatMap(p -> {
                    CropHumidityType orDefault = TAG_HUMIDITY_MAP.getOrDefault(p.key(), null);
                    if (orDefault == null) return Stream.of();
                    return p.stream().map(h -> Map.entry(h.value(), orDefault));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue
                        , (a, b) -> b, HashMap::new
                ));
    }

    public static CropHumidityType mapSeasonToHumidity(CropSeasonType seasonType) {
        if (SEASON_HUMIDITY_MAP.containsKey(seasonType)) {
            return SEASON_HUMIDITY_MAP.get(seasonType);
        }
        return CropHumidityType.AVERAGE;
    }

    public static <T> void registerForSS(Optional<? extends HolderLookup.RegistryLookup<T>> tRegistry, ResourceKey<Registry<T>> registryResourceKey) {

        Set<String> blackMods = Set.copyOf(CompatModule.CommonConfig.modsWithoutSereneSeasonBasedHumidity.get());

        tRegistry.ifPresent(registry -> {
            List<List<Holder<T>>> nameBlockList = new ArrayList<>();

            List<TagKey<T>> useTag = registryResourceKey.equals(Registries.BLOCK) ?
                    (List) ss_blockList : (List) ss_itemList;
            for (TagKey<T> blockTagKey : useTag) {
                Optional<HolderSet.Named<T>> tag = Optional.empty();
                tag = registry.get(blockTagKey);
                tag.ifPresent(holders -> nameBlockList.add(holders.stream().toList()));
            }

            List<Holder<T>> nameBlockSet = new ArrayList<>(new HashSet<>(nameBlockList.stream()
                    .flatMap(Collection::stream)
                    .toList()));

            Map<Item, CropHumidityType> bultinHumidityTypeMap = Map.of();
            if (registry.key().equals(Registries.ITEM)) {
                bultinHumidityTypeMap = getBultinHumidityTypeMap((HolderLookup.RegistryLookup<Item>) registry);
            }

            for (Holder<T> t : nameBlockSet) {
                int season = 0;
                for (int i = 0; i < nameBlockList.size(); i++) {
                    if (nameBlockList.get(i).contains(t)) {
                        season += seasonInfoList.get(i);
                    }
                }

                CropSeasonType cropSeasonTypeFrom = CropInfoManager.getCropSeasonTypeFrom(new CropSeasonInfo(season));
                boolean isWaterPlant = false;
                if (t.value() instanceof Block block) {
                    if (block instanceof SaplingBlock
                            && CompatModule.CommonConfig.sereneSeasonsIgnoreSapling.get())
                        continue;
                    CropInfoManager.registerCropSeasonInfo(block, cropSeasonTypeFrom, true);
                    isWaterPlant = block instanceof LiquidBlockContainer;
                } else if (t.value() instanceof Item item) {
                    if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SaplingBlock
                            && CompatModule.CommonConfig.sereneSeasonsIgnoreSapling.get())
                        continue;
                    CropInfoManager.registerCropSeasonInfo(item, cropSeasonTypeFrom);
                    isWaterPlant = item instanceof BlockItem blockItem && blockItem.getBlock() instanceof LiquidBlockContainer;
                }

                if (!CompatModule.CommonConfig.sereneSeasonBasedHumidity.get()) continue;

                Identifier key = t.getKey().identifier();
                if (key != null)
                    if (!blackMods.contains(key.getNamespace())) {
                        CropHumidityType humid =
                                isWaterPlant ? CropHumidityType.MOIST_HUMID :
                                        mapSeasonToHumidity(cropSeasonTypeFrom);
                        if (t.value() instanceof Block block) {
                            CropInfoManager.registerCropHumidityInfo(block, humid, true);
                        } else if (t.value() instanceof Item item) {
                            humid = bultinHumidityTypeMap.getOrDefault(item, humid);
                            CropInfoManager.registerCropHumidityInfo(item, humid);
                        }
                    }
            }
        });
    }


    public static final TagKey<Block> SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS = createBlockTag("sereneseasons", "unbreakable_infertile_crops");
    private static final TagKey<Block> SERENE_SEASONS_YEAR_ROUND_CROPS = createBlockTag("sereneseasons", "year_round_crops");
    private static final TagKey<Item> ITEM_SERENE_SEASONS_YEAR_ROUND_CROPS = createItemTag("sereneseasons", "year_round_crops");


    public static void registerForSSALL(Optional<? extends HolderLookup.RegistryLookup<Item>> items, Optional<? extends HolderLookup.RegistryLookup<Block>> blocks) {
        blocks.flatMap(br -> br.get(SERENE_SEASONS_YEAR_ROUND_CROPS)).ifPresent(nblocks -> {
            for (Holder<Block> nblock : nblocks) {
                CropInfoManager.registerCropSeasonInfo(nblock.value(), CropSeasonType.ALL, true);
                if (CommonConfig.Crop.registerCropDefaultValue.getAsBoolean()) {
                    CropInfoManager.registerCropHumidityInfo(nblock.value(), CropHumidityType.AVERAGE_MOIST, true);
                }
            }
        });
        items.flatMap(br -> br.get(ITEM_SERENE_SEASONS_YEAR_ROUND_CROPS)).ifPresent(itemNamed -> {
            for (Holder<Item> itemHolder : itemNamed) {
                CropInfoManager.registerCropSeasonInfo(itemHolder.value(), CropSeasonType.ALL);
                if (CommonConfig.Crop.registerCropDefaultValue.get()) {
                    CropInfoManager.registerCropHumidityInfo(itemHolder.value(), CropHumidityType.AVERAGE_MOIST);
                }
            }
        });
        blocks.flatMap(blocks1 -> blocks.get().get(SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS)).ifPresent(blocksG -> {
            for (Holder<Block> blockHolder : blocksG.stream().toList()) {
                CropInfoManager.CROPS_WOULD_NOT_KILLED_BY_CLIMATE.put(blockHolder.value(), blockHolder);
            }
        });
    }
}
