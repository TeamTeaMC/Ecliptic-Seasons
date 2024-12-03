package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import com.teamtea.eclipticseasons.EclipticSeasons;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public final class CropInfoManager
{
    private final static Map<Block, CropHumidityInfo> CROP_HUMIDITY_INFO = new HashMap<>();
    private final static Map<Block, CropSeasonInfo> CROP_SEASON_INFO = new HashMap<>();

    private static final TagKey<Block> ss1 = createBlockTag("sereneseasons", "spring_crops");
    private static final TagKey<Block> ss2 = createBlockTag("sereneseasons", "summer_crops");
    private static final TagKey<Block> ss3 = createBlockTag("sereneseasons", "autumn_crops");
    private static final TagKey<Block> ss4 = createBlockTag("sereneseasons", "winter_crops");

    private static final TagKey<Block> SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS = createBlockTag("sereneseasons", "unbreakable_infertile_crops");

    private static final TagKey<Item> ssi1 = createItemTag("sereneseasons", "spring_crops");
    private static final TagKey<Item> ssi2 = createItemTag("sereneseasons", "summer_crops");
    private static final TagKey<Item> ssi3 = createItemTag("sereneseasons", "autumn_crops");
    private static final TagKey<Item> ssi4 = createItemTag("sereneseasons", "winter_crops");

    private static final List<Integer> seasonInfoList = List.of(1, 2, 4, 8);
    private static final List<TagKey<Block>> ss_blockList = List.of(ss1, ss2, ss3, ss4);
    private static final List<TagKey<Item>> ss_itemList = List.of(ssi1, ssi2, ssi3, ssi4);

    private static TagKey<Item> createItemTag(String modId, String path) {
        return ItemTags.create(new ResourceLocation(modId, path));
    }

    private static TagKey<Block> createBlockTag(String modId, String path) {
        return BlockTags.create(new ResourceLocation(modId, path));
    }

    @SubscribeEvent
    public static void init(TagsUpdatedEvent event)
    {
        CROP_HUMIDITY_INFO.clear();
        CROP_SEASON_INFO.clear();

        var items= event.getRegistryAccess().registry(Registries.ITEM);
        if (items.isPresent()){
            for (CropHumidityType cropHumidityType : CropHumidityType.values()) {
               var tagItems= items.get().getTag(ItemTags.create(cropHumidityType.getRes()));
                tagItems.ifPresent(holders -> holders.stream().toList().forEach(action -> {
                    registerCropHumidityInfo(action.get(), cropHumidityType);
                }));
            }
            for (CropSeasonType cropSeasonType : CropSeasonType.values()) {
                var tagItems= items.get().getTag(ItemTags.create(cropSeasonType.getRes()));
                tagItems.ifPresent(holders -> holders.stream().toList().forEach(action -> {
                    registerCropSeasonInfo(action.get(), cropSeasonType);
                }));
            }
        }

        var blocks = event.getRegistryAccess().registry(Registries.BLOCK);
        if (blocks.isPresent()) {
            for (CropHumidityType cropHumidityType : CropHumidityType.values()) {
                var tagBlocks = blocks.get().getTag(BlockTags.create(cropHumidityType.getRes()));
                tagBlocks.ifPresent(holders -> holders.stream().toList().forEach(action -> {
                    registerCropHumidityInfo(action.value(), cropHumidityType, true);
                }));
            }
            for (CropSeasonType cropSeasonType : CropSeasonType.values()) {
                var tagBlocks = blocks.get().getTag(BlockTags.create(cropSeasonType.getRes()));
                tagBlocks.ifPresent(holders -> holders.stream().toList().forEach(action -> {
                    registerCropSeasonInfo(action.value(), cropSeasonType, true);
                }));
            }
        }
        // event.getRegistryAccess().registry(Registries.BLOCK).get().getTagNames().toList();

        if (ServerConfig.Compat.sereneSeasons.get()) {
            registerForSS(blocks, Registries.BLOCK);
            registerForSS(items, Registries.ITEM);
        }

        if (ServerConfig.Crop.useDefaultValue.get()) {
            BuiltInRegistries.BLOCK.forEach(block ->
            {
                registerCropHumidityInfo(block, CropHumidityType.AVERAGE_MOIST, false);
                registerCropSeasonInfo(block, CropSeasonType.SP_SU_AU, false);
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
                Block block = null;
                if (t instanceof Block) {
                    block = (Block) t;
                } else if (t instanceof BlockItem) {
                    block = ((BlockItem) t).getBlock();
                }

                if (block != null && !CROP_SEASON_INFO.containsKey(block)) {
                    CROP_SEASON_INFO.put(block, new CropSeasonInfo(season));
                }

                if (ServerConfig.Crop.useDefaultValue.get()) {
                    if (block != null && !CROP_HUMIDITY_INFO.containsKey(block)) {
                        CropHumidityType humid;
                        if (season == 1) {
                            humid = CropHumidityType.AVERAGE_HUMID;
                        } else if (season == 5) {
                            humid = CropHumidityType.AVERAGE_MOIST;
                        } else humid = CropHumidityType.DRY_MOIST;

                        CROP_HUMIDITY_INFO.put(block, humid.getInfo());
                    }
                }
            }

        });
    }

    public static void registerCropHumidityInfo(Item item, CropHumidityType info)
    {
        if (item instanceof BlockItem && !CROP_HUMIDITY_INFO.containsKey(((BlockItem) item).getBlock()))
        {
            CROP_HUMIDITY_INFO.put(((BlockItem) item).getBlock(), info.getInfo());
        }
    }

    public static void registerCropHumidityInfo(Block block, CropHumidityType info, boolean force)
    {
        if (force || block instanceof CropBlock)
        {
            if (!CROP_HUMIDITY_INFO.containsKey(block))
            {
                CROP_HUMIDITY_INFO.put(block, info.getInfo());
            }
        }
    }

    public static void registerCropSeasonInfo(Item item, CropSeasonType info)
    {
        if (item instanceof BlockItem && !CROP_SEASON_INFO.containsKey(((BlockItem) item).getBlock()))
        {
            CROP_SEASON_INFO.put(((BlockItem) item).getBlock(), info.getInfo());
        }
    }

    public static void registerCropSeasonInfo(Block block, CropSeasonType info, boolean force)
    {
        if (force || block instanceof CropBlock)
        {
            if (!CROP_SEASON_INFO.containsKey(block))
            {
                CROP_SEASON_INFO.put(block, info.getInfo());
            }
        }
    }

    public static Collection<Block> getHumidityCrops()
    {
        return CROP_HUMIDITY_INFO.keySet();
    }

    public static Collection<Block> getSeasonCrops()
    {
        return CROP_SEASON_INFO.keySet();
    }

    @Nullable
    public static CropHumidityInfo getHumidityInfo(Block crop)
    {
        return CROP_HUMIDITY_INFO.get(crop);
    }

    @Nullable
    public static CropSeasonInfo getSeasonInfo(Block crop)
    {
        return CROP_SEASON_INFO.get(crop);
    }

}
