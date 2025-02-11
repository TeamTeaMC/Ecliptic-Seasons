package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import com.teamtea.eclipticseasons.EclipticSeasons;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public final class CropInfoManager {
    private final static Map<Block, CropHumidityInfo> CROP_HUMIDITY_INFO = new HashMap<>();
    private final static Map<Block, CropSeasonInfo> CROP_SEASON_INFO = new HashMap<>();

    private static final Tags.IOptionalNamedTag<Block> ss1 = createBlockTag("sereneseasons", "spring_crops");
    private static final Tags.IOptionalNamedTag<Block> ss2 = createBlockTag("sereneseasons", "summer_crops");
    private static final Tags.IOptionalNamedTag<Block> ss3 = createBlockTag("sereneseasons", "autumn_crops");
    private static final Tags.IOptionalNamedTag<Block> ss4 = createBlockTag("sereneseasons", "winter_crops");

    private static final Tags.IOptionalNamedTag<Block> SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS = createBlockTag("sereneseasons", "unbreakable_infertile_crops");

    private static final Tags.IOptionalNamedTag<Item> ssi1 = createItemTag("sereneseasons", "spring_crops");
    private static final Tags.IOptionalNamedTag<Item> ssi2 = createItemTag("sereneseasons", "summer_crops");
    private static final Tags.IOptionalNamedTag<Item> ssi3 = createItemTag("sereneseasons", "autumn_crops");
    private static final Tags.IOptionalNamedTag<Item> ssi4 = createItemTag("sereneseasons", "winter_crops");

    private static final List<Integer> seasonInfoList = Stream.of(1, 2, 4, 8).collect(Collectors.toList());
    private static final List<Tags.IOptionalNamedTag<Block>> ss_blockList = Stream.of(ss1, ss2, ss3, ss4).collect(Collectors.toList());
    private static final List<Tags.IOptionalNamedTag<Item>> ss_itemList = Stream.of(ssi1, ssi2, ssi3, ssi4).collect(Collectors.toList());

    private static Tags.IOptionalNamedTag<Item> createItemTag(String modId, String path) {
        return ItemTags.createOptional(new ResourceLocation(modId, path));
    }

    private static Tags.IOptionalNamedTag<Block> createBlockTag(String modId, String path) {
        return BlockTags.createOptional(new ResourceLocation(modId, path));
    }

    @SubscribeEvent
    public static void init(TagsUpdatedEvent event) {
        CROP_HUMIDITY_INFO.clear();
        CROP_SEASON_INFO.clear();


        ITagCollection<Item> items = event.getTagManager().getItems();
        for (CropHumidityType cropHumidityType : CropHumidityType.values()) {
            ITag<Item> tagItems = items.getTag(cropHumidityType.getRes());
            if (tagItems != null)
                tagItems.getValues().forEach(action -> {
                    registerCropHumidityInfo(action, cropHumidityType);
                });
        }
        for (CropSeasonType cropSeasonType : CropSeasonType.values()) {
            ITag<Item> tagItems = items.getTag(cropSeasonType.getRes());
            if (tagItems != null)
                tagItems.getValues().forEach(action -> {
                    registerCropSeasonInfo(action, cropSeasonType);
                });
        }

        // Registry.BLOCK.
        ITagCollection<Block> blocks = event.getTagManager().getBlocks();
        for (CropHumidityType cropHumidityType : CropHumidityType.values()) {
            ITag<Block> tagBlocks = blocks.getTag(cropHumidityType.getRes());
            if (tagBlocks != null)
                tagBlocks.getValues().forEach(action -> {
                    registerCropHumidityInfo(action, cropHumidityType, true);
                });
        }
        for (CropSeasonType cropSeasonType : CropSeasonType.values()) {
            ITag<Block> tagBlocks = blocks.getTag(cropSeasonType.getRes());
            if (tagBlocks != null)
                tagBlocks.getValues().forEach(action -> {
                    registerCropSeasonInfo(action, cropSeasonType, true);
                });
        }

        registerForSS(blocks, true);
        registerForSS(items, false);

        // ForgeRegistries.BLOCKS.forEach(block ->
        // {
        //     registerCropHumidityInfo(block, CropHumidityType.AVERAGE_MOIST, false);
        //     registerCropSeasonInfo(block, CropSeasonType.SP_SU_AU, false);
        // });
    }

    public static <T> void registerForSS(ITagCollection<T> blocks1, boolean isBlock) {
        List<List<T>> nameBlockList = new ArrayList<>();

        List<Tags.IOptionalNamedTag<T>> useTag = isBlock ?
                (List) ss_blockList : (List) ss_itemList;
        for (Tags.IOptionalNamedTag<T> blockTagKey : useTag) {
            Optional<ITag<T>> tag = Optional.empty();
            tag = Optional.ofNullable(blocks1.getTag(blockTagKey.getName()));
            tag.ifPresent(holders -> nameBlockList.add(holders.getValues()));
        }

        List<T> nameBlockSet = new ArrayList<>(new HashSet<>(nameBlockList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())));

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

        }
    }

    public static void registerCropHumidityInfo(Item item, CropHumidityType info) {
        if (item instanceof BlockItem && !CROP_HUMIDITY_INFO.containsKey(((BlockItem) item).getBlock())) {
            CROP_HUMIDITY_INFO.put(((BlockItem) item).getBlock(), info.getInfo());
        }
    }

    public static void registerCropHumidityInfo(Block block, CropHumidityType info, boolean force) {
        if (force || block instanceof CropsBlock) {
            if (!CROP_HUMIDITY_INFO.containsKey(block)) {
                CROP_HUMIDITY_INFO.put(block, info.getInfo());
            }
        }
    }

    public static void registerCropSeasonInfo(Item item, CropSeasonType info) {
        if (item instanceof BlockItem && !CROP_SEASON_INFO.containsKey(((BlockItem) item).getBlock())) {
            CROP_SEASON_INFO.put(((BlockItem) item).getBlock(), info.getInfo());
        }
    }

    public static void registerCropSeasonInfo(Block block, CropSeasonType info, boolean force) {
        if (force || block instanceof CropsBlock) {
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

}
