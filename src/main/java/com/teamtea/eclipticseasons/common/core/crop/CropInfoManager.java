package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.event.RegisterAndModifyCropInfoEvent;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import javax.annotation.Nullable;
import java.util.*;

public final class CropInfoManager {
    final static Map<Block, CropHumidityInfo> CROP_HUMIDITY_INFO = new HashMap<>();
    final static Map<Block, CropSeasonInfo> CROP_SEASON_INFO = new HashMap<>();

    final static Map<Item, CropHumidityInfo> ITEM_CROP_HUMIDITY_INFO = new HashMap<>();
    final static Map<Item, CropSeasonInfo> ITEM_CROP_SEASON_INFO = new HashMap<>();

    private static final TagKey<Block> ss1 = createBlockTag("sereneseasons", "spring_crops");
    private static final TagKey<Block> ss2 = createBlockTag("sereneseasons", "summer_crops");
    private static final TagKey<Block> ss3 = createBlockTag("sereneseasons", "autumn_crops");
    private static final TagKey<Block> ss4 = createBlockTag("sereneseasons", "winter_crops");

    private static final TagKey<Block> SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS = createBlockTag("sereneseasons", "unbreakable_infertile_crops");
    private static final TagKey<Block> SERENE_SEASONS_YEAR_ROUND_CROPS = createBlockTag("sereneseasons", "year_round_crops");
    private static final TagKey<Item> ITEM_SERENE_SEASONS_YEAR_ROUND_CROPS = createItemTag("sereneseasons", "year_round_crops");

    private static final TagKey<Item> ssi1 = createItemTag("sereneseasons", "spring_crops");
    private static final TagKey<Item> ssi2 = createItemTag("sereneseasons", "summer_crops");
    private static final TagKey<Item> ssi3 = createItemTag("sereneseasons", "autumn_crops");
    private static final TagKey<Item> ssi4 = createItemTag("sereneseasons", "winter_crops");

    private static final List<Integer> seasonInfoList = List.of(1, 2, 4, 8);
    private static final List<TagKey<Block>> ss_blockList = List.of(ss1, ss2, ss3, ss4);
    private static final List<TagKey<Item>> ss_itemList = List.of(ssi1, ssi2, ssi3, ssi4);


    private static TagKey<Item> createItemTag(String modId, String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    private static TagKey<Block> createBlockTag(String modId, String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    static CropSeasonType getCropSeasonTypeFrom(CropSeasonInfo cropSeasonInfo) {
        for (CropSeasonType value : CropSeasonType.collectValues()) {
            if (value.getInfo().equals(cropSeasonInfo))
                return value;
        }
        return null;
    }

    static CropHumidityType getCropHumidityTypeFrom(CropHumidityInfo cropSeasonInfo) {
        for (CropHumidityType value : CropHumidityType.collectValues()) {
            if (value.getInfo().equals(cropSeasonInfo))
                return value;
        }
        return null;
    }


    public static void init(TagsUpdatedEvent event) {
        CROP_HUMIDITY_INFO.clear();
        CROP_SEASON_INFO.clear();

        Optional<Registry<Item>> items = event.getRegistryAccess().registry(Registries.ITEM);
        Optional<Registry<Block>> blocks = event.getRegistryAccess().registry(Registries.BLOCK);

        if (blocks.isPresent()) {
            for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
                var tagBlocks = blocks.get().getTag(cropHumidityType.getBlockTag());
                tagBlocks.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropHumidityInfo(action.value(), cropHumidityType, true);
                }));
            }
            for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
                var tagBlocks = blocks.get().getTag(cropSeasonType.getBlockTag());
                tagBlocks.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropSeasonInfo(action.value(), cropSeasonType, true);
                }));
            }
        }

        if (items.isPresent()) {
            for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
                var tagItems = items.get().getTag(cropHumidityType.getTag());
                tagItems.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropHumidityInfo(action.value(), cropHumidityType);
                }));
            }
            for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
                var tagItems = items.get().getTag(cropSeasonType.getTag());
                tagItems.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropSeasonInfo(action.value(), cropSeasonType);
                }));
            }
        }

        // event.getRegistryAccess().registry(Registries.BLOCK).get().getTagNames().toList();

        if (CompatModule.CommonConfig.sereneSeasons.getAsBoolean()) {
            registerForSS(blocks, Registries.BLOCK);
            registerForSS(items, Registries.ITEM);
            blocks.flatMap(br -> br.getTag(SERENE_SEASONS_YEAR_ROUND_CROPS)).ifPresent(nblocks -> {
                for (Holder<Block> nblock : nblocks) {
                    registerCropSeasonInfo(nblock.value(), CropSeasonType.ALL, true);
                    if (CommonConfig.Crop.registerCropDefaultValue.getAsBoolean()) {
                        registerCropHumidityInfo(nblock.value(), CropHumidityType.AVERAGE_MOIST, true);
                    }
                }
            });
            items.flatMap(br -> br.getTag(ITEM_SERENE_SEASONS_YEAR_ROUND_CROPS)).ifPresent(itemNamed -> {
                for (Holder<Item> itemHolder : itemNamed) {
                    registerCropSeasonInfo(itemHolder.value(), CropSeasonType.ALL);
                    if (CommonConfig.Crop.registerCropDefaultValue.get()) {
                        registerCropHumidityInfo(itemHolder.value(), CropHumidityType.AVERAGE_MOIST);
                    }
                }
            });
        }


        NeoForge.EVENT_BUS.post(new RegisterAndModifyCropInfoEvent(CROP_HUMIDITY_INFO, CROP_SEASON_INFO));
        if (com.teamtea.eclipticseasons.config.CommonConfig.Crop.registerCropDefaultValue.getAsBoolean()) {
            BuiltInRegistries.BLOCK.forEach(block ->
            {
                if (block instanceof CropBlock) {
                    registerCropHumidityInfo(block, CropHumidityType.AVERAGE_MOIST, true);
                    registerCropSeasonInfo(block, CropSeasonType.SP_SU_AU, true);
                }
            });
        }
    }

    public static <T> void registerForSS(Optional<Registry<T>> blocks, ResourceKey<Registry<T>> registryResourceKey) {
        blocks.ifPresent(blocks1 -> {
            List<List<T>> nameBlockList = new ArrayList<>();

            List<TagKey<T>> useTag = registryResourceKey.equals(Registries.BLOCK) ?
                    (List) ss_blockList : (List) ss_itemList;
            for (TagKey<T> blockTagKey : useTag) {
                Optional<HolderSet.Named<T>> tag = Optional.empty();
                tag = blocks1.getTag(blockTagKey);
                tag.ifPresent(holders -> nameBlockList.add(holders.stream().map(Holder::value).toList()));
            }

            List<T> nameBlockSet = new ArrayList<>(new HashSet<>(nameBlockList.stream()
                    .flatMap(Collection::stream)
                    .toList()));

            for (T t : nameBlockSet) {
                int season = 0;
                for (int i = 0; i < nameBlockList.size(); i++) {
                    if (nameBlockList.get(i).contains(t)) {
                        season += seasonInfoList.get(i);
                    }
                }

                if (t instanceof Block block) {
                    registerCropSeasonInfo(block, getCropSeasonTypeFrom(new CropSeasonInfo(season)), true);
                } else if (t instanceof Item item) {
                    registerCropSeasonInfo(item, getCropSeasonTypeFrom(new CropSeasonInfo(season)));
                }

                if (CommonConfig.Crop.registerCropDefaultValue.getAsBoolean()) {
                    CropHumidityType humid;
                    if (season == 1) {
                        humid = CropHumidityType.AVERAGE_HUMID;
                    } else if (season == 5) {
                        humid = CropHumidityType.AVERAGE_MOIST;
                    } else humid = CropHumidityType.DRY_MOIST;
                    if (t instanceof Block block) {
                        registerCropHumidityInfo(block, humid, true);
                    } else if (t instanceof Item item) {
                        registerCropHumidityInfo(item, humid);
                    }
                }
            }

        });
    }


    public static void registerCropHumidityInfo(Item item, CropHumidityType info) {
        if (item instanceof BlockItem blockItem) {
            registerCropHumidityInfo(blockItem.getBlock(), info, false);
        }
        if (!ITEM_CROP_HUMIDITY_INFO.containsKey(item)) {
            ITEM_CROP_HUMIDITY_INFO.put(item, info.getInfo());
        }
    }

    public static void registerCropHumidityInfo(Block block, CropHumidityType info, boolean force) {
        // if (force || block instanceof CropBlock)
        {
            if (!CROP_HUMIDITY_INFO.containsKey(block)) {
                CROP_HUMIDITY_INFO.put(block, info.getInfo());
            }
        }
    }

    public static void registerCropSeasonInfo(Item item, CropSeasonType info) {
        if (item instanceof BlockItem blockItem) {
            registerCropSeasonInfo(blockItem.getBlock(), info, false);
        }

        if (!ITEM_CROP_SEASON_INFO.containsKey(item)) {
            ITEM_CROP_SEASON_INFO.put(item, info.getInfo());
        }
    }

    public static void registerCropSeasonInfo(Block block, CropSeasonType info, boolean force) {
        // if (force || block instanceof CropBlock)
        {
            if (!CROP_SEASON_INFO.containsKey(block)) {
                CROP_SEASON_INFO.put(block, info.getInfo());
            }
        }
    }

    public static Collection<Block> getHumidityCrops() {
        return CROP_HUMIDITY_INFO.keySet();
    }

    public static Collection<Block> getSeasonCrops() {
        return CROP_SEASON_INFO.keySet();
    }

    @Nullable
    public static CropHumidityInfo getHumidityInfo(Block crop) {
        return CROP_HUMIDITY_INFO.get(crop);
    }

    @Nullable
    public static CropSeasonInfo getSeasonInfo(Block crop) {
        return CROP_SEASON_INFO.get(crop);
    }


    @Nullable
    public static CropHumidityInfo getHumidityInfo(Item crop) {
        return ITEM_CROP_HUMIDITY_INFO.get(crop);
    }

    @Nullable
    public static CropSeasonInfo getSeasonInfo(Item crop) {
        return ITEM_CROP_SEASON_INFO.get(crop);
    }

    public static List<Component> appendInfo(Block block) {
        List<Component> toolTip = new ArrayList<>();
        if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            if (CropInfoManager.getHumidityCrops().contains(block)) {
                CropHumidityInfo info = CropInfoManager.getHumidityInfo(block);
                if (info != null) toolTip.addAll(info.getTooltip());
            }
        }
        if (CommonConfig.Crop.enableCrop.get()) {
            if (CropInfoManager.getSeasonCrops().contains(block)) {
                CropSeasonInfo info = CropInfoManager.getSeasonInfo(block);
                if (info != null) toolTip.addAll(info.getTooltip());
            }
        }
        return toolTip;
    }

    public static List<Component> appendInfo(Item item) {
        List<Component> toolTip = new ArrayList<>();
        if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            if (ITEM_CROP_HUMIDITY_INFO.containsKey(item)) {
                CropHumidityInfo info = CropInfoManager.getHumidityInfo(item);
                if (info != null) toolTip.addAll(info.getTooltip());
            }
        }
        if (CommonConfig.Crop.enableCrop.get()) {
            if (ITEM_CROP_SEASON_INFO.containsKey(item)) {
                CropSeasonInfo info = CropInfoManager.getSeasonInfo(item);
                if (info != null) toolTip.addAll(info.getTooltip());
            }
        }
        return toolTip;
    }
}
