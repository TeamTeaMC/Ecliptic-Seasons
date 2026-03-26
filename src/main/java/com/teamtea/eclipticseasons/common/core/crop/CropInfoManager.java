package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.event.RegisterAndModifyCropInfoEvent;
import com.teamtea.eclipticseasons.common.core.crop.internal.CompatCropHookInternal;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import javax.annotation.Nullable;
import java.util.*;

public final class CropInfoManager {
    final static Map<Block, CropHumidityInfo> CROP_HUMIDITY_INFO = new HashMap<>();
    final static Map<Block, CropSeasonInfo> CROP_SEASON_INFO = new HashMap<>();

    final static Map<Item, CropHumidityInfo> ITEM_CROP_HUMIDITY_INFO = new HashMap<>();
    final static Map<Item, CropSeasonInfo> ITEM_CROP_SEASON_INFO = new HashMap<>();

    public final static Map<Block, Holder<Block>> CROPS_WOULD_NOT_KILLED_BY_CLIMATE = new IdentityHashMap<>();

    public static TagKey<Item> createItemTag(String modId, String path) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(modId, path));
    }

    public static TagKey<Block> createBlockTag(String modId, String path) {
        return BlockTags.create(Identifier.fromNamespaceAndPath(modId, path));
    }

    public static CropSeasonType getCropSeasonTypeFrom(CropSeasonInfo cropSeasonInfo) {
        for (CropSeasonType value : CropSeasonType.collectValues()) {
            if (value.getInfo().equals(cropSeasonInfo))
                return value;
        }
        return null;
    }

    public static CropHumidityType getCropHumidityTypeFrom(CropHumidityInfo cropSeasonInfo) {
        for (CropHumidityType value : CropHumidityType.collectValues()) {
            if (value.getInfo().equals(cropSeasonInfo))
                return value;
        }
        return null;
    }


    public static void init(TagsUpdatedEvent event) {
        CROP_HUMIDITY_INFO.clear();
        CROP_SEASON_INFO.clear();
        CROPS_WOULD_NOT_KILLED_BY_CLIMATE.clear();

        var items = event.getLookupProvider().lookup(Registries.ITEM);
        var blocks = event.getLookupProvider().lookup(Registries.BLOCK);

        if (blocks.isPresent()) {
            blocks.get().get(EclipticBlockTags.NOT_KILLED_BY_CLIMATE).ifPresent(
                    blocksG -> {
                        for (Holder<Block> blockHolder : blocksG.stream().toList()) {
                            CROPS_WOULD_NOT_KILLED_BY_CLIMATE.put(blockHolder.value(), blockHolder);
                        }
                    }
            );

            for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
                var tagBlocks = blocks.get().get(cropHumidityType.getBlockTag());
                tagBlocks.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropHumidityInfo(action.value(), cropHumidityType, true);
                }));
            }
            for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
                var tagBlocks = blocks.get().get(cropSeasonType.getBlockTag());
                tagBlocks.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropSeasonInfo(action.value(), cropSeasonType, true);
                }));
            }
        }

        if (items.isPresent()) {
            for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
                var tagItems = items.get().get(cropHumidityType.getTag());
                tagItems.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropHumidityInfo(action.value(), cropHumidityType);
                }));
            }
            for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
                var tagItems = items.get().get(cropSeasonType.getTag());
                tagItems.ifPresent(holders -> holders.stream().forEach(action -> {
                    registerCropSeasonInfo(action.value(), cropSeasonType);
                }));
            }
        }

        // event.getRegistryAccess().registry(Registries.BLOCK).get().getNames().toList();

        if (CompatModule.CommonConfig.sereneSeasons.getAsBoolean()) {
            CompatCropHookInternal.registerForSS(items, Registries.ITEM);
            CompatCropHookInternal.registerForSS(blocks, Registries.BLOCK);
            CompatCropHookInternal.registerForSSALL(items, blocks);

        }

        if (CommonConfig.Crop.registerCropDefaultValue.getAsBoolean()) {
            BuiltInRegistries.BLOCK.forEach(block ->
            {
                if (block instanceof CropBlock) {
                    registerCropHumidityInfo(block, CropHumidityType.AVERAGE_MOIST, true);
                    registerCropSeasonInfo(block, CropSeasonType.SP_SU_AU, true);
                }
            });
        }

        NeoForge.EVENT_BUS.post(new RegisterAndModifyCropInfoEvent(CROP_HUMIDITY_INFO, CROP_SEASON_INFO));

        removeBlockAndItemShouldBeIgnored(blocks, items);
    }

    private static void removeBlockAndItemShouldBeIgnored(Optional<? extends HolderLookup.RegistryLookup<Block>> blocks, Optional<? extends HolderLookup.RegistryLookup<Item>> items) {
        if (blocks.isPresent() && items.isPresent()) {
            // Block → Item
            blocks.get().get(EclipticBlockTags.UNAFFECTED_BY_SEASONS).ifPresent(tag ->
                    tag.forEach(holder -> clearCropInfo(holder.value(), holder.value().asItem())));
            blocks.get().get(EclipticBlockTags.UNAFFECTED_BY_HUMIDITY).ifPresent(tag ->
                    tag.forEach(holder -> clearCropInfo(holder.value(), holder.value().asItem())));

            // Item → Block
            items.get().get(ESItemTags.UNAFFECTED_BY_SEASONS).ifPresent(tag ->
                    tag.forEach(holder -> clearCropInfo(Block.byItem(holder.value()), holder.value())));
            items.get().get(ESItemTags.UNAFFECTED_BY_HUMIDITY).ifPresent(tag ->
                    tag.forEach(holder -> clearCropInfo(Block.byItem(holder.value()), holder.value())));
        }
    }

    private static void clearCropInfo(Block block, Item item) {
        if (block != Blocks.AIR) {
            CROP_SEASON_INFO.remove(block);
            CROP_HUMIDITY_INFO.remove(block);
        }
        if (item != Items.AIR) {
            ITEM_CROP_SEASON_INFO.remove(item);
            ITEM_CROP_HUMIDITY_INFO.remove(item);
        }
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

    public static boolean mayKilledByClimate(BlockState blockState) {
        return !CROPS_WOULD_NOT_KILLED_BY_CLIMATE.containsKey(blockState.getBlock());
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
