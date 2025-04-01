package com.teamtea.eclipticseasons.common.core.crop;


import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.crop.CropGrow;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.CropRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }

    public static void beforeCropGrowUp(BonemealEvent event) {
        var block = event.getBlock();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }


    public static void beforeCropGrowUp(SaplingGrowTreeEvent event) {
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, world.getBlockState(pos));
    }

    private final static Map<Biome, Holder<AgroClimaticZone>> cropClimateTypeMap = new IdentityHashMap<>();
    private final static Map<ResourceLocation, com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder> CropGrowControlBuilder = new HashMap<>();
    private final static Map<Block, Map<Holder<AgroClimaticZone>, CropGrowControl>> CROP_GROW_MAP = new IdentityHashMap<>();

    private final static IdentityHashMap<Boolean, Holder<AgroClimaticZone>> DefaultCropClimateType = new IdentityHashMap<>();


    public static void resetUpdate(RegistryAccess registryAccess, boolean isServer) {


        long startTime = System.currentTimeMillis();

        if (isServer) {
            cropClimateTypeMap.clear();
            CropGrowControlBuilder.clear();
            CROP_GROW_MAP.clear();
            DefaultCropClimateType.clear();
        }

        RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);

        Registry<AgroClimaticZone> cropClimateTypeRegistry = registryAccess.registryOrThrow(ESRegistries.AGRO_CLIMATE);
        for (Map.Entry<ResourceKey<AgroClimaticZone>, AgroClimaticZone> entry : cropClimateTypeRegistry.entrySet()) {
            Optional<Holder.Reference<AgroClimaticZone>> holder = cropClimateTypeRegistry.getHolder(cropClimateTypeRegistry.getId(entry.getValue()));
            if (holder.isPresent()) {
                HolderSet<Biome> biomes = entry.getValue().biomes();
                for (int i = 0; i < biomes.size(); i++) {
                    cropClimateTypeMap.put(biomes.get(i).value(), holder.get());
                }
            }

            // todo: test sync
            // Optional<Tag> tag = AgroClimaticZone.CODEC
            //         .encodeStart(registryops, entry.getValue())
            //         .resultOrPartial(EclipticSeasons::logger);
            // EclipticSeasons.logger(tag.isPresent()?tag.get():"");
        }
        DefaultCropClimateType.put(isServer, cropClimateTypeRegistry.getHolder(AgroClimateRegistry.TEMPERATE).get());

        Registry<Item> itemRegistry = registryAccess.registryOrThrow(Registries.ITEM);
        Registry<Block> blockRegistry = registryAccess.registryOrThrow(Registries.BLOCK);
        for (Map.Entry<ResourceKey<CropGrowControlBuilder>, CropGrowControlBuilder> entry : registryAccess.registryOrThrow(ESRegistries.CROP).entrySet()) {
            CropGrowControlBuilder builder = entry.getValue();
            CropGrowControlBuilder.put(entry.getKey().location(), builder);
            Optional<HolderSet<Block>> blocks = Optional.of(builder.applyTarget());
            if (blocks.isEmpty()) continue;

            EnumMap<SolarTerm, GrowParameter> solarTermGrowParameterEnumMap = new EnumMap<>(builder.solarTermList());
            EnumMap<Season, GrowParameter> seasonGrowParameterEnumMap = new EnumMap<>(builder.seasonList());
            EnumMap<Humidity, GrowParameter> humidityGrowParameterEnumMap = new EnumMap<>(builder.humidList());
            Optional<GrowParameter> solarTermGrowParameter = builder.defaultSolarTermGrowParameter();
            Optional<GrowParameter> humidityGrowParameter = builder.defaultHumidityGrowParameter();

            if (builder.parent().size() > 0) {
                List<HolderSet<CropGrowControlBuilder>> holderSets = new ArrayList<>();
                holderSets.add(builder.parent());
                while (!holderSets.isEmpty()) {
                    HolderSet<CropGrowControlBuilder> currentParentSet = holderSets.remove(0);
                    for (int i = 0; i < currentParentSet.size(); i++) {
                        CropGrowControlBuilder parentBuilder = builder.parent().get(i).value();
                        if (!builder.isChildClimateType(parentBuilder.cropClimateType())) continue;
                        for (Map.Entry<SolarTerm, GrowParameter> entry1 : parentBuilder.solarTermList().entrySet()) {
                            solarTermGrowParameterEnumMap.putIfAbsent(entry1.getKey(), entry1.getValue());
                        }

                        for (Map.Entry<Season, GrowParameter> entry1 : parentBuilder.seasonList().entrySet()) {
                            seasonGrowParameterEnumMap.putIfAbsent(entry1.getKey(), entry1.getValue());
                        }

                        for (Map.Entry<Humidity, GrowParameter> entry1 : parentBuilder.humidList().entrySet()) {
                            humidityGrowParameterEnumMap.putIfAbsent(entry1.getKey(), entry1.getValue());
                        }

                        if (solarTermGrowParameter.isEmpty() && parentBuilder.defaultSolarTermGrowParameter().isPresent()) {
                            solarTermGrowParameter = parentBuilder.defaultSolarTermGrowParameter();
                        }
                        if (humidityGrowParameter.isEmpty() && parentBuilder.defaultHumidityGrowParameter().isPresent()) {
                            humidityGrowParameter = parentBuilder.defaultHumidityGrowParameter();
                        }
                        if (parentBuilder.parent().size() > 0) {
                            holderSets.add(parentBuilder.parent());
                        }
                    }
                }
            }

            // Note:TODO:注意这里的链接关系，必要时可以增加内存
            if (blocks.isPresent()) {
                HolderSet<Block> holders = blocks.get();
                Optional<TagKey<Block>> blockTagKey = blocks.get().unwrapKey();
                if (blockTagKey.isPresent() && blockTagKey.get().location().getNamespace().equals(EclipticSeasonsApi.MODID)) {
                    TagKey<Item> itemTagKey = ItemTags.create(blockTagKey.get().location());
                    {
                        Optional<HolderSet.Named<Item>> itemNamed = itemRegistry.getTag(itemTagKey);
                        if (itemNamed.isPresent()) {
                            ArrayList<Holder<Block>> holderArrayList = new ArrayList<>(holders.stream().toList());
                            for (Holder<Item> blockHolder : itemNamed.get()) {
                                if (blockHolder.value() instanceof BlockItem blockItem)
                                    holderArrayList.add(blockRegistry.getHolderOrThrow(blockRegistry.getResourceKey(blockItem.getBlock()).get()));
                            }
                            if (!holderArrayList.isEmpty())
                                holders = HolderSet.direct(holderArrayList);
                        }
                    }
                }
                for (int i = 0; i < holders.size(); i++) {
                    Block block = holders.get(i).value();

                    Map<Holder<AgroClimaticZone>, CropGrowControl> c = CROP_GROW_MAP.getOrDefault(block, null);
                    if (c == null) {
                        c = new HashMap<>();
                        CROP_GROW_MAP.put(block, c);
                    }

                    for (int j = 0; j < builder.cropClimateType().size(); j++) {
                        CropGrow cropGrow = new CropGrow(
                                solarTermGrowParameter,
                                humidityGrowParameter,
                                new EnumMap<>(solarTermGrowParameterEnumMap),
                                new EnumMap<>(seasonGrowParameterEnumMap),
                                new EnumMap<>(humidityGrowParameterEnumMap));
                        // 一个Block，对应一个cropGrow，绑定到一个CropGrowControl上
                        // 由于有些Block有自己的湿润度，因此容易出问题
                        // 而且不同群系湿润度系统不一样
                        CropGrowControl newControlCache = new CropGrowControl(
                                cropGrow, Optional.empty(), Optional.empty()
                        );
                        Holder<AgroClimaticZone> cropClimateTypeHolder = builder.cropClimateType().get(j);
                        if (cropClimateTypeHolder.get() != null) {
                            c.compute(cropClimateTypeHolder, (resourceLocation, oldControl) -> {
                                if (oldControl == null) return newControlCache;
                                oldControl.base().solarTermsMap().putAll(newControlCache.base().solarTermsMap());
                                oldControl.base().seasonMap().putAll(newControlCache.base().seasonMap());
                                oldControl.base().humidMap().putAll(newControlCache.base().humidMap());
                                return oldControl;
                            });
                        }
                    }
                }
            }
        }

        // note 这里客户端时，由于接受不到信息，所以会存一个空位，但是这样也好，标志着有吧
        // note 推荐当前版本还是用标签，因为这样便于指示
        CropInfoManager.CROP_SEASON_INFO.forEach((block, cropSeasonInfo) -> {
            // if (CROP_GROW_MAP.containsKey(block)) return;
            CropSeasonType name = CropInfoManager.getCropSeasonTypeFrom(cropSeasonInfo);
            if (name != null) {
                ResourceLocation location = CropRegistry.createKey(name).location();
                extracted(block, location);
            }
        });
        CropInfoManager.CROP_HUMIDITY_INFO.forEach((block, cropHumidityInfo) -> {
            // if (CROP_GROW_MAP.containsKey(block)) return;
            CropHumidityType name = CropInfoManager.getCropHumidityTypeFrom(cropHumidityInfo);
            if (name != null) {
                ResourceLocation location = CropRegistry.createKey(name).location();
                extracted(block, location);
            }
        });

        EclipticSeasons.logger("Reload crop data cost %s ms in %s side.".formatted(System.currentTimeMillis() - startTime, isServer ? "server" : "client")
        );
    }

    private static void extracted(Block block, ResourceLocation location) {
        Map<Holder<AgroClimaticZone>, CropGrowControl> blockClimateMap;
        CropGrowControlBuilder builder = CropGrowControlBuilder.getOrDefault(location, null);
        if (builder != null) {
            CropGrow cropGrow = new CropGrow(builder.defaultSolarTermGrowParameter(),
                    builder.defaultHumidityGrowParameter(),
                    new EnumMap<>(builder.solarTermList()),
                    new EnumMap<>(builder.seasonList()),
                    new EnumMap<>(builder.humidList()));
            CropGrowControl newControlCache = new CropGrowControl(
                    cropGrow, Optional.empty(), Optional.empty()
            );

            blockClimateMap = CROP_GROW_MAP.getOrDefault(block, null);
            if (blockClimateMap == null) {
                blockClimateMap = new HashMap<>();
                CROP_GROW_MAP.put(block, blockClimateMap);
            }

            for (int j = 0; j < builder.cropClimateType().size(); j++) {
                Holder<AgroClimaticZone> cropClimateTypeHolder = builder.cropClimateType().get(j);
                if (cropClimateTypeHolder.get() != null) {
                    blockClimateMap.compute(cropClimateTypeHolder, (resourceLocation, oldControl) -> {
                        if (oldControl == null) return newControlCache;
                        oldControl.base().solarTermsMap().putAll(newControlCache.base().solarTermsMap());
                        oldControl.base().seasonMap().putAll(newControlCache.base().seasonMap());
                        oldControl.base().humidMap().putAll(newControlCache.base().humidMap());
                        return oldControl;
                    });
                }
            }
        }
    }

    public static void clearOnClientExitOrServerClose() {
        cropClimateTypeMap.clear();
        CropGrowControlBuilder.clear();
        CROP_GROW_MAP.clear();
        DefaultCropClimateType.clear();
        CropInfoManager.CROP_HUMIDITY_INFO.clear();
        CropInfoManager.CROP_SEASON_INFO.clear();
    }

    public enum RoomStatus {
        GREEN_HOUSE, NORMAL, UNKNOWN;
    }

    public static float getGrowChance(net.minecraftforge.eventbus.api.Event event, GrowParameter growParameter) {
        return event instanceof BonemealEvent ?
                growParameter.fertile_chance() : growParameter.grow_chance();
    }

    public static GreenHouseCoreProvider getGreenHouseProvider(
            Level level, BlockPos pos,
            Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> agentClimateTypeHolder) {
        List<Season> seasons = getLikeSeasonsInTemperate(controlMap, agentClimateTypeHolder);
        if (!seasons.isEmpty()) {
            SolarDataManager saveData = SolarHolders.getSaveData(level);
            if (saveData != null) {
                return saveData.findNearGreenHouseProvider(pos, seasons);
            }
        }
        return null;
    }

    public static @NotNull List<Season> getLikeSeasonsInTemperate(Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> agentClimateTypeHolder) {
        List<Season> seasons = new ArrayList<>();
        CropGrowControl growControl_Temp = getCropGrowControl(controlMap, agentClimateTypeHolder);
        for (Season collectValue : Season.collectValues()) {
            GrowParameter parameter = growControl_Temp.getGrowParameter(collectValue);
            if (parameter == null
                    || parameter.grow_chance() > 0.4f) {
                seasons.add(collectValue);
            }
        }
        return seasons;
    }

    public static GrowParameter getSeasonGrowParameter(CropGrowControl growControl, SolarTerm solarTerm, Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> agentClimateTypeHolder, Holder<AgroClimaticZone> climateTypeHolder) {
        GrowParameter growParameter = null;
        if (growControl != null) {
            growParameter = growControl.getGrowParameter(solarTerm);
        }

        if (growParameter == null
        ) {
            CropGrowControl deaultCropGrowControl = getCropGrowControl(controlMap, agentClimateTypeHolder);
            growParameter = climateTypeHolder.value().getGrowParameterFromMapping(deaultCropGrowControl, solarTerm);
        }
        return growParameter;
    }

    public static CropGrowControl getCropGrowControl(Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> climateTypeHolder) {
        return controlMap.getOrDefault(climateTypeHolder, null);
    }

    public static Holder<AgroClimaticZone> getDefaultAgroClimaticZoneHolder(LevelAccessor level) {
        boolean isServerSide = !level.isClientSide();
        return DefaultCropClimateType.getOrDefault(isServerSide, null);
    }

    public static Holder<AgroClimaticZone> getclimateTypeHolder(Holder<Biome> biomeHolder) {
        return cropClimateTypeMap.getOrDefault(biomeHolder.value(), null);
    }

    public static Holder<Biome> getCropBiome(LevelAccessor level, BlockPos pos) {
        int i = QuartPos.fromBlock(pos.getX());
        int j = QuartPos.fromBlock(pos.getY());
        int k = QuartPos.fromBlock(pos.getZ());
        return level.getNoiseBiome(i, j, k);
    }

    public static Map<Holder<AgroClimaticZone>, CropGrowControl> getControlMap(Block block) {
        return CROP_GROW_MAP.get(block);
    }

    public static void beforeCropGrowUp(net.minecraftforge.eventbus.api.Event event, LevelAccessor level, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap = getControlMap(block);
        if (controlMap == null) return;

        Holder<Biome> biomeHolder = getCropBiome(level, pos);
        Holder<AgroClimaticZone> climateTypeHolder = getclimateTypeHolder(biomeHolder);
        if (climateTypeHolder == null) return;

        Holder<AgroClimaticZone> agentClimateTypeHolder = getDefaultAgroClimaticZoneHolder(level);
        CropGrowControl growControl = getCropGrowControl(controlMap, climateTypeHolder);

        boolean notCancel = false;
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm((Level) level);
        Season season = solarTerm.getSeason();
        RoomStatus roomStatus = RoomStatus.UNKNOWN;

        // TODO:这些映射应该提前计算，不应该实时计算,但是由于湿润度是全部覆盖的，因此如果没有，则不计算
        if (growControl == null) return;

        GrowParameter growParameter = getSeasonGrowParameter(growControl, solarTerm, controlMap, agentClimateTypeHolder, climateTypeHolder);

        int randomKey = level.getRandom().nextInt(1000);
        if (growParameter != null && CommonConfig.Crop.enableCrop.get()) {
            // TODO：计算本地节气？
            notCancel |= getGrowChance(event, growParameter) * 1000 > randomKey;
            // notCancel |= CommonConfig.Crop.cropGrowChanceInWrongSeason.get() > 0
            //         && randomKey < CommonConfig.Crop.cropGrowChanceInWrongSeason.get() * 1000;
            if (!notCancel) {
                {
                    List<Season> seasons = getLikeSeasonsInTemperate(controlMap, agentClimateTypeHolder);
                    if (!seasons.isEmpty()) {
                        SolarDataManager saveData = SolarHolders.getSaveData((Level) level);
                        if (saveData != null) {
                            GreenHouseCoreProvider nearGreenHouseProvider = saveData.findNearGreenHouseProvider(pos, seasons);
                            if (nearGreenHouseProvider != null) {
                                roomStatus = isInRoom(level, pos, blockState, season) ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
                                if (roomStatus == RoomStatus.GREEN_HOUSE) {
                                    notCancel = true;
                                    nearGreenHouseProvider.costAvailCost((2 / seasons.size() + 1));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            notCancel = true;
        }
        if (!notCancel) {
            setResult(event, CANCEL);
            if (randomKey < growParameter.death_chance() * 1000) {
                ((Level) level).setBlockAndUpdate(pos,
                        growParameter.deadState().isPresent() ?
                                growParameter.deadState().get() :
                                Blocks.DEAD_BUSH.defaultBlockState());
            }
        } else if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            // not need to check it any more
            // if (blockState.getFluidState().isSource()) return;
            Humidity env = EclipticUtil.getHumidityAt(solarTerm,biomeHolder,pos,!level.isClientSide());

            // GrowParameter growParameter = growControl.base().humidMap().getOrDefault(env, null);
            checkHumidity(event, level, growControl, env, roomStatus, pos, blockState, season, false, randomKey);
        }
    }


    public static void checkHumidity(Event event, LevelAccessor world, CropGrowControl growControl, Humidity env, RoomStatus roomStatus, BlockPos pos, BlockState blockState, Season season, boolean hasUpdate, int randomKey) {
        if (blockState.getFluidState().isSource()) return;
        if (growControl != null) {
            if (!hasUpdate) {
                int modification = SolarHolders.getSaveData((Level) world).calculateHumidityModification(pos);
                if (modification != 0) {
                    roomStatus = isInRoom(world, pos, blockState, season) ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
                }
                if (modification != 0 && roomStatus == RoomStatus.GREEN_HOUSE) {
                    env = env.cycle(modification);
                    checkHumidity(event, world, growControl, env, roomStatus, pos, blockState, season, true, randomKey);
                    return;
                    //  TODO:下雨增加湿润度
                } else if (((Level) world).isRainingAt(pos)) {
                    env = env.cycle(1);
                    checkHumidity(event, world, growControl, env, roomStatus, pos, blockState, season, true, randomKey);
                    return;
                }
            }
            GrowParameter growParameter = growControl.getGrowParameter(env);
            if (growParameter != null) {
                float f = getGrowChance(event, growParameter);
                if (f == 0) {
                    setResult(event, CANCEL);
                } else if (f > 1.0F) {
                    setResult(event, GROW);
                } else if (f < 1.0F) {
                    if (randomKey < 1000 * f) {
                        setResult(event, PASS);
                    } else {
                        // 或者用特殊气体，BlockEntity辅助查询
                        boolean should = true;
                        if (should) {
                            setResult(event, CANCEL);
                        }
                    }
                }
            }
        }
    }


    public static final int CANCEL = 1;
    public static final int PASS = 2;
    public static final int GROW = 3;


    public static void setResult(net.minecraftforge.eventbus.api.Event event, int flag) {
        if (event.hasResult()) {
            if (flag == CANCEL) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(BlockEvent.CropGrowEvent.Pre.Result.DENY);
                } else if (event instanceof SaplingGrowTreeEvent blockGrowFeatureEvent) {
                    blockGrowFeatureEvent.setResult(Event.Result.DENY);
                } else if (event instanceof BonemealEvent bonemealEvent) {
                    // bonemealEvent.setCanceled(true);
                    bonemealEvent.setResult(Event.Result.ALLOW);
                }
            } else if (flag == PASS) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(BlockEvent.CropGrowEvent.Pre.Result.DEFAULT);
                }
            } else if (flag == GROW) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(BlockEvent.CropGrowEvent.Pre.Result.ALLOW);
                }
            }
        }

    }

    public static final Vec3[] CHECK_DIRECTIONS = {
            // 基本方向
            new Vec3(0, 1, 0), // 向上
            new Vec3(1, 0, 0), // 向前
            new Vec3(-1, 0, 0), // 向后
            new Vec3(0, 0, 1), // 向右
            new Vec3(0, 0, -1), // 向左

            // 组合方向
            new Vec3(1, 1, 0), // 向前上
            new Vec3(-1, 1, 0), // 向后上
            new Vec3(0, 1, 1), // 向上右
            new Vec3(0, 1, -1), // 向上左

            new Vec3(1, 0, 1), // 向前右
            new Vec3(1, 0, -1), // 向前左
            new Vec3(-1, 0, 1), // 向后右
            new Vec3(-1, 0, -1), // 向后左

            // 三维组合方向
            new Vec3(1, 1, 1), // 向前上右
            new Vec3(1, 1, -1), // 向前上左
            new Vec3(-1, 1, 1), // 向后上右
            new Vec3(-1, 1, -1), // 向后上左
    };

    public static final Vec3[] CHECK_DIRECTIONS_SIMPLE = {
            // 基本方向
            new Vec3(0, 1, 0), // 向上
            new Vec3(1, 0, 0), // 向前
            new Vec3(-1, 0, 0), // 向后
            new Vec3(0, 0, 1), // 向右
            new Vec3(0, 0, -1), // 向左

    };

    public static class SectionClipContext extends ClipContext {
        public final List<Pair<SectionPos, LevelChunkSection>> chunkAccessList = new ArrayList<>(1);

        public SectionClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, Entity pEntity) {
            super(from, to, block, fluid, pEntity);
        }

        public List<Pair<SectionPos, LevelChunkSection>> getChunkAccessList() {
            return chunkAccessList;
        }

        public BlockState getBlockState(LevelReader levelAccessor, BlockPos pos) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            int y = SectionPos.blockToSectionCoord(pos.getY());
            for (int i = 0, size = this.chunkAccessList.size(); i < size; i++) {
                Pair<SectionPos, LevelChunkSection> chunkAccess = this.chunkAccessList.get(i);
                if (chunkAccess.getFirst().x() == x
                        && chunkAccess.getFirst().z() == z
                        && chunkAccess.getFirst().y() == y) {
                    return chunkAccess.getSecond().getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
                }
            }
            ChunkAccess chunk1 = levelAccessor.getChunk(x, z);
            int sectionIndex = chunk1.getSectionIndex(pos.getY());
            LevelChunkSection[] sections = chunk1.getSections();
            if (sectionIndex < 0 || sectionIndex >= sections.length)
                return Blocks.AIR.defaultBlockState();
            LevelChunkSection chunk = sections[sectionIndex];
            this.chunkAccessList.add(Pair.of(SectionPos.of(x, y, z), chunk));
            return chunk.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        }

        public void release() {
            this.chunkAccessList.clear();
        }
    }

    public record BlockTester(
            LevelReader levelReader) implements BiFunction<SectionClipContext, BlockPos, BlockHitResult> {

        @Override
        public BlockHitResult apply(SectionClipContext clipContext, BlockPos pos) {
            BlockState blockstate = clipContext.getBlockState(levelReader, pos);
            if (!blockstate.isSolid()) return null;
            Vec3 vec3 = clipContext.getFrom();
            Vec3 vec31 = clipContext.getTo();
            VoxelShape voxelshape = clipContext.getBlockShape(blockstate, levelReader, pos);
            BlockHitResult blockHitResult = voxelshape.clip(vec3, vec31, pos);
            if (blockHitResult != null)
                clipContext.release();
            return blockHitResult;
        }
    }

    private static final FailHandler FAIL_HANDLER = new FailHandler();

    public static class FailHandler implements Function<SectionClipContext, BlockHitResult> {
        @Override
        public BlockHitResult apply(SectionClipContext clipContext) {
            clipContext.release();
            Vec3 vec3 = clipContext.getFrom().subtract(clipContext.getTo());
            return BlockHitResult.miss(clipContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(clipContext.getTo()));
        }
    }

    public static BlockHitResult clip(LevelReader levelAccessor, SectionClipContext context) {
        return BlockGetter.traverseBlocks(context.getFrom(),
                context.getTo(),
                context,
                new BlockTester(levelAccessor),
                FAIL_HANDLER);
    }

    public static boolean isInRoom(LevelAccessor level, BlockPos pos, BlockState state, Season season) {
        if (state.getFluidState().isSource()) return false;

        boolean isInLight = level.getBrightness(LightLayer.SKY, pos.above()) > 12;
        if (isInLight) {
            int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
            if (height < pos.getY()) return false;
        }
        if (season == Season.SUMMER) {
            if (isInLight && EclipticUtil.isNoon((Level) level))
                return false;
        }
        boolean isConnected = true;


        int maxDistance = CommonConfig.Crop.greenHouseMaxDiameter.get();
        int y_maxDistance = CommonConfig.Crop.greenHouseMaxHeight.get();
        Vec3 centerVec = pos.getCenter();
        Vec3[] vec3s = CommonConfig.Crop.complexGreenHouseCheck.get() ?
                CHECK_DIRECTIONS : CHECK_DIRECTIONS_SIMPLE;

        float xr = (float) level.getRandom().nextGaussian() / 3f;
        float yr = (float) level.getRandom().nextGaussian() / 3f;
        for (int i = 0, vec3sLength = vec3s.length; i < vec3sLength; i++) {
            Vec3 direction = vec3s[i];
            direction = direction.add(xr, 0, yr);
            // TODO: 最小起步点
            Vec3 startVec = centerVec;

            // direction是否要限制为圆形
            // direction=direction.normalize();

            Vec3 endVec = centerVec.add(direction.scale(direction.y == 0 ?
                    maxDistance : y_maxDistance));

            SectionClipContext context = new SectionClipContext(startVec, endVec,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, null);
            HitResult hitResult = clip(level, context);

            if (hitResult.getType() == HitResult.Type.MISS) {
                isConnected = false;
                break;
            }
        }

        // TODO:qucikly for windows green house
        if (isConnected && !isInLight) {
            isConnected = level.getRandom().nextInt(10000) > CommonConfig.Crop.darkGreenhouseFailChance.get();
        }
        return isConnected;
    }

    public static void unloadChunk(Level level, ChunkPos pos) {
        SolarHolders.getSaveData(level).unloadChunk(pos);
    }

    public static void handleRandomTick(Level serverLevel, LevelChunk chunk, BlockPos blockPos, BlockState blockState) {
        if (blockState.is(Blocks.BUBBLE_COLUMN)
                && chunk.getBlockState(blockPos.above()).isAir()
                && chunk.getBlockState(blockPos.below()).is(Blocks.MAGMA_BLOCK)) {
            SolarDataManager saveData = SolarHolders.getSaveData(serverLevel);
            // saveData.addMap(blockPos, blockState);
        }
    }

    public static void handleRandomTick2(Level level, LevelChunk chunk) {
        SolarDataManager saveData = SolarHolders.getSaveData(level);
        saveData.randomClearSome(chunk.getPos(), level.getRandom());
    }
}
