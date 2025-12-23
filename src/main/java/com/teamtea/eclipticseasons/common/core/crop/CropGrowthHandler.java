package com.teamtea.eclipticseasons.common.core.crop;


import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.crop.CropGrow;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.CropRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(CropGrowEvent.Pre event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        if (world instanceof Level level) {
            beforeCropGrowUp(event, level, pos, block);
        }
    }

    public static void beforeCropGrowUp(CanPlantGrowEvent event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        if (world instanceof Level level) {
            beforeCropGrowUp(event, level, pos, block);
        }
    }

    // note 确实会变成BlockGrowFeatureEvent再触发一次，很麻烦，那只能阻止一下了，a计划是弄一个缓存map
    public static void beforeCropGrowUp(BonemealEvent event) {
        // if(!event.isValidBonemealTarget())return;
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        if (world instanceof Level level) {
            beforeCropGrowUp(event, level, pos, block);
        }
    }


    public static void beforeCropGrowUp(BlockGrowFeatureEvent event) {
        var world = event.getLevel();
        BlockPos pos = event.getPos();

        if (world instanceof Level level) {
            if (SolarHolders.getSaveData(level) instanceof SolarDataManager data) {
                if (data.shouldSkipNextCheck(pos)) return;
            }
            beforeCropGrowUp(event, level, pos, world.getBlockState(pos));
        }
    }

    // private final static List<WetterStructure> wetterStructures = new ArrayList<>();
    private final static Map<Block, List<WetterStructure>> wetterStructures = new IdentityHashMap<>();
    private final static Map<Biome, Holder<AgroClimaticZone>> cropClimateTypeMap = new IdentityHashMap<>();
    private final static Map<ResourceLocation, CropGrowControlBuilder> CropGrowControlBuilder = new HashMap<>();
    private final static Map<Block, Map<Holder<AgroClimaticZone>, CropGrowControl>> CROP_GROW_MAP = new IdentityHashMap<>();

    private final static IdentityHashMap<Boolean, Holder<AgroClimaticZone>> DefaultCropClimateType = new IdentityHashMap<>();


    public static void resetUpdate(RegistryAccess registryAccess, boolean isServer) {
        if (isServer) {

            Optional<Registry<WetterStructure>> structures = registryAccess.registry(ESRegistries.WETTER);
            if (structures.isPresent()) {
                wetterStructures.clear();
                for (WetterStructure structure : ESSortInfo.sorted2(structures.get())) {
                    HolderSet<Block> holders = structure.core().isPresent()
                            && structure.core().get().blocks().isPresent() ?
                            structure.core().get().blocks().get() : HolderSet.direct(Blocks.AIR.builtInRegistryHolder());
                    for (Holder<Block> holder : holders) {
                        wetterStructures.compute(holder.value(),
                                (block, wetterStructures1) -> {
                                    wetterStructures1 = wetterStructures1 == null ? new ArrayList<>() : wetterStructures1;
                                    wetterStructures1.add(structure);
                                    return wetterStructures1;
                                });
                    }
                }
            }
        }

        Optional<Registry<AgroClimaticZone>> agroClimaticZones = registryAccess.registry(ESRegistries.AGRO_CLIMATE);
        Optional<Registry<CropGrowControlBuilder>> cropGrowControlBuilders = registryAccess.registry(ESRegistries.CROP);
        if (agroClimaticZones.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.AGRO_CLIMATE);
            return;
        } else if (cropGrowControlBuilders.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.CROP);
            return;
        }

        long startTime = System.currentTimeMillis();

        if (isServer) {
            cropClimateTypeMap.clear();
            CropGrowControlBuilder.clear();
            CROP_GROW_MAP.clear();
            DefaultCropClimateType.clear();
        }


        Registry<AgroClimaticZone> cropClimateTypeRegistry = agroClimaticZones.get();
        for (Holder.Reference<AgroClimaticZone> agroClimaticZoneReference : ESSortInfo.sorted(cropClimateTypeRegistry.holders().toList())) {
            if (!agroClimaticZoneReference.isBound()) continue;
            HolderSet<Biome> biomes = agroClimaticZoneReference.value().biomes();
            for (int i = 0; i < biomes.size(); i++) {
                cropClimateTypeMap.put(biomes.get(i).value(), agroClimaticZoneReference);
            }
        }

        DefaultCropClimateType.put(isServer, cropClimateTypeRegistry.getHolder(AgroClimateRegistry.TEMPERATE).orElse(null));

        Registry<Item> itemRegistry = registryAccess.registryOrThrow(Registries.ITEM);
        Registry<Block> blockRegistry = registryAccess.registryOrThrow(Registries.BLOCK);
        for (Map.Entry<ResourceKey<CropGrowControlBuilder>, CropGrowControlBuilder> entry : ESSortInfo.sorted(cropGrowControlBuilders.get().entrySet())) {
            CropGrowControlBuilder builder = entry.getValue();
            CropGrowControlBuilder.put(entry.getKey().location(), builder);
            Optional<HolderSet<Block>> blocks = builder.applyTarget().blocks();
            Optional<StatePropertiesPredicate> properties = builder.applyTarget().properties();
            if (blocks.isEmpty()) continue;

            Enum2ObjectMap<SolarTerm, GrowParameter> solarTermGrowParameterEnumMap = new Enum2ObjectMap<>(builder.solarTermList());
            Enum2ObjectMap<Season, GrowParameter> seasonGrowParameterEnumMap = new Enum2ObjectMap<>(builder.seasonList());
            Enum2ObjectMap<Humidity, GrowParameter> humidityGrowParameterEnumMap = new Enum2ObjectMap<>(builder.humidList());
            Optional<GrowParameter> solarTermGrowParameter = builder.defaultSolarTermGrowParameter();
            Optional<GrowParameter> humidityGrowParameter = builder.defaultHumidityGrowParameter();
            Optional<BlockPredicate> notGreenHouse = builder.notGreenHouse();

            if (builder.parent().size() > 0) {
                List<HolderSet<CropGrowControlBuilder>> holderSets = new ArrayList<>();
                holderSets.add(builder.parent());
                while (!holderSets.isEmpty()) {
                    HolderSet<CropGrowControlBuilder> currentParentSet = holderSets.removeFirst();
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
                        if (notGreenHouse.isEmpty() && parentBuilder.notGreenHouse().isPresent()) {
                            notGreenHouse = parentBuilder.notGreenHouse();
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
                    Optional<List<BlockState>> statesCheck = Optional.empty();
                    if (properties.isPresent()) {
                        StatePropertiesPredicate statePropertiesPredicate = properties.get();
                        statesCheck = Optional.of(
                                block.getStateDefinition().getPossibleStates()
                                        .stream()
                                        .filter(statePropertiesPredicate::matches)
                                        .toList()
                        );
                    }
                    Map<Holder<AgroClimaticZone>, CropGrowControl> c =
                            CROP_GROW_MAP.computeIfAbsent(block, (block1) -> new HashMap<>());

                    for (int j = 0; j < builder.cropClimateType().size(); j++) {
                        CropGrow cropGrow = new CropGrow(
                                solarTermGrowParameter,
                                humidityGrowParameter,
                                new Enum2ObjectMap<>(solarTermGrowParameterEnumMap),
                                new Enum2ObjectMap<>(seasonGrowParameterEnumMap),
                                new Enum2ObjectMap<>(humidityGrowParameterEnumMap));
                        // 一个Block，对应一个cropGrow，绑定到一个CropGrowControl上
                        // 由于有些Block有自己的湿润度，因此容易出问题
                        // 而且不同群系湿润度系统不一样

                        CropGrowControl newControlCache = new CropGrowControl(
                                statesCheck.isEmpty() ?
                                        cropGrow : CropGrow.EMPTY,
                                statesCheck.map(
                                        blockStates ->
                                                blockStates.stream()
                                                        .collect(Collectors.toMap(
                                                                Function.identity(),
                                                                bs -> cropGrow,
                                                                (a, b) -> b,
                                                                IdentityHashMap::new
                                                        ))
                                ),
                                Optional.empty(), notGreenHouse
                        );

                        Holder<AgroClimaticZone> cropClimateTypeHolder = builder.cropClimateType().get(j);
                        if (cropClimateTypeHolder.getKey() != null) {
                            c.compute(cropClimateTypeHolder, (resourceLocation, oldControl) -> {
                                if (oldControl == null) return newControlCache;
                                return oldControl.merge(newControlCache);
                            });
                        }
                    }
                }
            }
        }

        CropInfoManager.CROP_SEASON_INFO.forEach((block, cropSeasonInfo) -> {
            // if (CROP_GROW_MAP.containsKey(block)) return;
            CropSeasonType name = CropInfoManager.getCropSeasonTypeFrom(cropSeasonInfo);
            if (name != null) {
                ResourceLocation location = CropRegistry.createKey(name).location();
                generateInfoForTag(block, location);
            }
        });
        CropInfoManager.CROP_HUMIDITY_INFO.forEach((block, cropHumidityInfo) -> {
            // if (CROP_GROW_MAP.containsKey(block)) return;
            CropHumidityType name = CropInfoManager.getCropHumidityTypeFrom(cropHumidityInfo);
            if (name != null) {
                ResourceLocation location = CropRegistry.createKey(name).location();
                generateInfoForTag(block, location);
            }
        });

        EclipticSeasons.logger("Reload crop data cost %s ms in %s side.".formatted(System.currentTimeMillis() - startTime, isServer ? "server" : "client")
        );
    }

    private static void generateInfoForTag(Block block, ResourceLocation location) {
        Map<Holder<AgroClimaticZone>, CropGrowControl> blockClimateMap;
        CropGrowControlBuilder builder = CropGrowControlBuilder.getOrDefault(location, null);
        if (builder != null) {
            CropGrow cropGrow = new CropGrow(builder.defaultSolarTermGrowParameter(),
                    builder.defaultHumidityGrowParameter(),
                    new Enum2ObjectMap<>(builder.solarTermList()),
                    new Enum2ObjectMap<>(builder.seasonList()),
                    new Enum2ObjectMap<>(builder.humidList()));

            CropGrowControl newControlCache = new CropGrowControl(
                    cropGrow, Optional.empty(), Optional.empty(), Optional.empty()
            );

            blockClimateMap = CROP_GROW_MAP.computeIfAbsent(block, (b1) -> new HashMap<>());

            for (int j = 0; j < builder.cropClimateType().size(); j++) {
                Holder<AgroClimaticZone> cropClimateTypeHolder = builder.cropClimateType().get(j);
                if (cropClimateTypeHolder.getKey() != null) {
                    blockClimateMap.compute(cropClimateTypeHolder, (resourceLocation, oldControl) -> {
                        if (oldControl == null) return newControlCache;
                        return oldControl.merge(newControlCache);
                    });
                }
            }
        }
    }

    public static void clearOnClientExitOrServerClose() {
        wetterStructures.clear();
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

    public static float getGrowChance(Event event, GrowParameter growParameter) {
        return event instanceof BonemealEvent ?
                growParameter.fertile_chance() : growParameter.grow_chance();
    }


    @Deprecated(forRemoval = true, since = "0.12")
    public static @Nullable GreenHouseCoreProvider getGreenHouseProvider(
            Level level, BlockPos pos,
            Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> agentClimateTypeHolder) {
        return getGreenHouseProvider(level, pos, null, controlMap, agentClimateTypeHolder);
    }

    @Deprecated(forRemoval = true, since = "0.12")
    public static @NotNull List<Season> getLikeSeasonsInTemperate(Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap,
                                                                  Holder<AgroClimaticZone> agentClimateTypeHolder) {
        return getLikeSeasonsInTemperate(null, controlMap, agentClimateTypeHolder);
    }

    @Deprecated(forRemoval = true, since = "0.12")
    public static @Nullable GrowParameter getSeasonGrowParameter(
            CropGrowControl growControl,
            SolarTerm solarTerm,
            Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap,
            Holder<AgroClimaticZone> agentClimateTypeHolder,
            Holder<AgroClimaticZone> climateTypeHolder) {
        return getSeasonGrowParameter(null, growControl,
                getCropGrowControl(controlMap, agentClimateTypeHolder),
                solarTerm, climateTypeHolder);
    }

    public static @Nullable GreenHouseCoreProvider getGreenHouseProvider(
            Level level, BlockPos pos, BlockState state,
            Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> agentClimateTypeHolder) {
        List<Season> seasons = getLikeSeasonsInTemperate(state, controlMap, agentClimateTypeHolder);
        if (!seasons.isEmpty()) {
            SolarDataManager saveData = SolarHolders.getSaveData(level);
            if (saveData != null) {
                return saveData.findNearGreenHouseProvider(pos, seasons);
            }
        }
        return null;
    }


    public static @NotNull List<Season> getLikeSeasonsInTemperate(BlockState state,
                                                                  Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap,
                                                                  Holder<AgroClimaticZone> agentClimateTypeHolder) {
        List<Season> seasons = new ArrayList<>();
        CropGrowControl growControl_Temp = getCropGrowControl(controlMap, agentClimateTypeHolder);
        if (growControl_Temp != null) {
            for (Season collectValue : Season.collectValues()) {
                GrowParameter parameter = growControl_Temp.getGrowParameter(collectValue, state);
                if (parameter != null
                        && parameter.grow_chance() > 0.4f) {
                    seasons.add(collectValue);
                }
            }
        }
        return seasons;
    }

    public static @NotNull List<Humidity> getLikeHumidityInTemperate(BlockState state,
                                                                     Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap,
                                                                     Holder<AgroClimaticZone> agentClimateTypeHolder) {
        List<Humidity> humidities = new ArrayList<>();
        CropGrowControl growControl_Temp = getCropGrowControl(controlMap, agentClimateTypeHolder);
        if (growControl_Temp != null) {
            for (Humidity collectValue : Humidity.collectValues()) {
                GrowParameter parameter = growControl_Temp.getGrowParameter(collectValue, state);
                if (parameter != null
                        && parameter.grow_chance() > 0.7f) {
                    humidities.add(collectValue);
                }
            }
        }
        return humidities;
    }

    public static @Nullable GrowParameter getSeasonGrowParameter(
            BlockState state,
            CropGrowControl growControl,
            CropGrowControl deaultCropGrowControl,
            SolarTerm solarTerm,
            Holder<AgroClimaticZone> climateTypeHolder) {
        GrowParameter growParameter = null;
        if (growControl != null) {
            growParameter = growControl.getGrowParameter(solarTerm, state);
        }
        if (growParameter == null
        ) {
            growParameter = climateTypeHolder.value().getGrowParameterFromMapping(state, deaultCropGrowControl, solarTerm);
        }
        return growParameter;
    }

    public static @Nullable CropGrowControl getCropGrowControl(Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap, Holder<AgroClimaticZone> climateTypeHolder) {
        return controlMap.getOrDefault(climateTypeHolder, null);
    }

    public static @Nullable Holder<AgroClimaticZone> getDefaultAgroClimaticZoneHolder(LevelAccessor level) {
        boolean isServerSide = level != null && !level.isClientSide();
        return DefaultCropClimateType.getOrDefault(isServerSide, null);
    }

    public static @Nullable Holder<AgroClimaticZone> getclimateTypeHolder(Holder<Biome> biomeHolder) {
        return cropClimateTypeMap.getOrDefault(biomeHolder.value(), null);
    }

    public static Holder<Biome> getCropBiome(LevelAccessor level, BlockPos pos) {
        int i = QuartPos.fromBlock(pos.getX());
        int j = QuartPos.fromBlock(pos.getY());
        int k = QuartPos.fromBlock(pos.getZ());
        return level.getNoiseBiome(i, j, k);
    }

    public static @Nullable Map<Holder<AgroClimaticZone>, CropGrowControl> getControlMap(Block block) {
        return CROP_GROW_MAP.get(block);
    }


    // 由于前面的事情的缘故，需要记录
    public static void beforeCropGrowUp(Event event, Level level, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap = getControlMap(block);
        if (controlMap == null) return;

        Holder<Biome> biomeHolder = getCropBiome(level, pos);
        Holder<AgroClimaticZone> climateTypeHolder = getclimateTypeHolder(biomeHolder);
        if (climateTypeHolder == null) return;

        Holder<AgroClimaticZone> agentClimateTypeHolder = getDefaultAgroClimaticZoneHolder(level);
        CropGrowControl growControl = getCropGrowControl(controlMap, climateTypeHolder);
        CropGrowControl agentGrowControl = getCropGrowControl(controlMap, agentClimateTypeHolder);
        if (growControl == null && agentGrowControl == null) {
            return;
        }
        boolean notCancel = false;
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        Season season = solarTerm.getSeason();
        RoomStatus roomStatus = RoomStatus.UNKNOWN;

        // TODO:这些映射应该提前计算，不应该实时计算,但是由于湿润度是全部覆盖的，因此如果没有，则不计算

        GrowParameter growParameter = getSeasonGrowParameter(blockState, growControl, agentGrowControl, solarTerm, climateTypeHolder);
        Optional<BlockPredicate> notGreenHouse =
                growControl != null ? growControl.notGreenHouse() : agentGrowControl.notGreenHouse();

        int randomKey = level.getRandom().nextInt(1000);
        float baseGrowthChance = 1f;
        if (growParameter != null && CommonConfig.Crop.enableCrop.get()) {
            float growChance = getGrowChance(event, growParameter);
            notCancel |= growChance * 1000 >= randomKey;
            baseGrowthChance = growChance;
            // notCancel |= CommonConfig.Crop.cropGrowChanceInWrongSeason.get() > 0
            //         && randomKey < CommonConfig.Crop.cropGrowChanceInWrongSeason.get() * 1000;
            if (!notCancel) {
                if (CommonConfig.Crop.simpleGreenHouse.get()) {
                    if (isInRoom(level, pos, blockState, notGreenHouse)) {
                        notCancel = true;
                        roomStatus = RoomStatus.GREEN_HOUSE;
                    } else {
                        roomStatus = RoomStatus.NORMAL;
                    }
                } else {
                    List<Season> seasons = getLikeSeasonsInTemperate(blockState, controlMap, agentClimateTypeHolder);
                    if (!seasons.isEmpty()) {
                        SolarDataManager saveData = SolarHolders.getSaveData(level);
                        if (saveData != null) {
                            GreenHouseCoreProvider nearGreenHouseProvider = saveData.findNearGreenHouseProvider(pos, seasons);
                            if (nearGreenHouseProvider != null) {
                                roomStatus = isInRoom(level, pos, blockState, notGreenHouse) ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
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
        baseGrowthChance = notCancel ? baseGrowthChance : 0;
        if (!notCancel) {
            setResult(event, CANCEL, growParameter);
            if (CropInfoManager.mayKilledByClimate(blockState)
                    && randomKey < growParameter.death_chance() * 1000) {
                level.setBlockAndUpdate(pos,
                        growParameter.deadState().isPresent() ?
                                growParameter.deadState().get() :
                                Blocks.DEAD_BUSH.defaultBlockState());
            }
        } else if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            // not need to check it any more
            // if (blockState.getFluidState().isSource()) return;
            float env = EclipticUtil.getHumidityLevelAt(level, solarTerm, biomeHolder, pos, !level.isClientSide());

            // GrowParameter growParameter = growControl.base().humidMap().getOrDefault(env, null);
            checkHumidity(event, level, growControl != null ? growControl : agentGrowControl, env, roomStatus, pos, blockState, season, false, randomKey, baseGrowthChance);
        } else {
            setResult(event, ((baseGrowthChance * 1000) - 1000 > randomKey) ? GROW : PASS, growParameter);
        }
    }


    public static void checkHumidity(Event event, Level level, CropGrowControl growControl, float env, RoomStatus roomStatus, BlockPos pos, BlockState blockState, Season season, boolean hasUpdate, int randomKey, float baseGrowthChance) {
        if (blockState.getFluidState().isSource()) return;
        env = Mth.clamp(env, 0, Humidity.collectValues().length - 1);
        if (growControl != null) {
            if (!hasUpdate) {
                if (CommonConfig.Crop.simpleGreenHouse.get()) {
                    roomStatus = roomStatus != RoomStatus.UNKNOWN ? roomStatus :
                            isInRoom(level, pos, blockState, growControl.notGreenHouse()) ?
                                    RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
                    if (roomStatus == RoomStatus.GREEN_HOUSE) {
                        setResult(event, PASS, null);
                        return;
                    }
                } else {
                    float modification = SolarHolders.getSaveData(level) instanceof SolarDataManager sd ?
                            sd.calculateHumidityModification(pos) : 0;
                    if (modification != 0) {
                        roomStatus = isInRoom(level, pos, blockState, growControl.notGreenHouse()) ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
                    }
                    if (modification != 0 && roomStatus == RoomStatus.GREEN_HOUSE) {
                        env += (modification);
                        checkHumidity(event, level, growControl, env, roomStatus, pos, blockState, season, true, randomKey, baseGrowthChance);
                        return;
                    } else if (level.isRainingAt(pos)) {
                        env += (1);
                        checkHumidity(event, level, growControl, env, roomStatus, pos, blockState, season, true, randomKey, baseGrowthChance);
                        return;
                    }
                }
            }
            GrowParameter growParameter = growControl.getGrowParameter(env, blockState);
            if (growParameter != null) {
                float f = getGrowChance(event, growParameter);
                int flag = PASS;
                if (f == 0) {
                    flag = CANCEL;
                } else if (f > 1.0F) {
                    flag = ((f * baseGrowthChance * 1000) - 1000 > randomKey) ? GROW : PASS;
                } else {
                    if (f == 1.0F || randomKey < 1000 * f) {
                    } else {
                        flag = CANCEL;
                    }
                }
                setResult(event, flag, growParameter);
                if (flag == CANCEL && CropInfoManager.mayKilledByClimate(blockState)
                        && randomKey < growParameter.death_chance() * 1000) {
                    level.setBlockAndUpdate(pos,
                            growParameter.deadState().isPresent() ?
                                    growParameter.deadState().get() :
                                    Blocks.DEAD_BUSH.defaultBlockState());
                }
            } else {
                setResult(event, PASS, null);
            }
        }
    }

    public static final int CANCEL = 1;
    public static final int PASS = 2;
    public static final int GROW = 3;

    @Deprecated
    public static void setResult(Event event, int flag) {
        setResult(event, flag, null);
    }

    public static void setResult(Event event, int flag, @Nullable GrowParameter growParameter) {
        if (flag == CANCEL) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            } else if (event instanceof CanPlantGrowEvent cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            } else if (event instanceof BlockGrowFeatureEvent blockGrowFeatureEvent) {
                blockGrowFeatureEvent.setCanceled(true);
            } else if (event instanceof BonemealEvent bonemealEvent) {
                bonemealEvent.setSuccessful(CommonConfig.Crop.boneMealConsumeOnFailure.get());
                bonemealEvent.setCanceled(true);
            }
        } else if (flag == PASS) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DEFAULT);
            } else if (event instanceof CanPlantGrowEvent cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DEFAULT);
            }
        } else if (flag == GROW) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.GROW);
            } else if (event instanceof CanPlantGrowEvent cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.GROW);
            }
        }

        postResult(event, flag, growParameter);
    }

    static void postResult(Event event, int flag, @Nullable GrowParameter growParameter) {
        if (flag != CANCEL) {
            if (event instanceof BonemealEvent bonemealEvent
                    && bonemealEvent.getLevel() instanceof ServerLevel serverLevel
                    && SolarHolders.getSaveData(serverLevel) instanceof SolarDataManager solarDataManager) {
                solarDataManager.addSkipNextCheck(bonemealEvent.getPos(), bonemealEvent.getState());
            }
        } else {
            if (event instanceof BonemealEvent bonemealEvent) {
                if (bonemealEvent.getPlayer() instanceof ServerPlayer player
                        && !(player instanceof FakePlayer)
                        && CommonConfig.Crop.boneMealFailureMessage.get()) {
                    if (growParameter != null && growParameter.fertile_chance() == 0) {
                        player.sendSystemMessage(
                                Component.translatable("info.eclipticseasons.bone_meal.failure"),
                                true
                        );
                    }
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

    public static class SClipContext extends ClipContext {
        public List<ChunkAccess> chunkAccessList = new ArrayList<>(1);

        public SClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, CollisionContext collisionContext) {
            super(from, to, block, fluid, collisionContext);
        }

        public SClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, Entity entity) {
            super(from, to, block, fluid, entity);
        }

        public BlockState getBlockState(LevelReader levelAccessor, BlockPos pos) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            int y = SectionPos.blockToSectionCoord(pos.getY());
            for (int i = 0, size = this.chunkAccessList.size(); i < size; i++) {
                ChunkAccess chunkAccess = this.chunkAccessList.get(i);
                if (chunkAccess.getPos().x == x && chunkAccess.getPos().z == z) {
                    return chunkAccess.getBlockState(pos);
                }
            }
            ChunkAccess chunk = levelAccessor.getChunk(x, z);
            this.chunkAccessList.add(chunk);
            return chunk.getBlockState(pos);
        }

        public void release() {
            this.chunkAccessList.clear();
        }
    }

    public static class SectionClipContext extends ClipContext {
        public final List<Pair<SectionPos, LevelChunkSection>> chunkAccessList = new ArrayList<>(1);

        public SectionClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, CollisionContext collisionContext) {
            super(from, to, block, fluid, collisionContext);
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
            LevelReader levelReader,
            Optional<BlockPredicate> notCheck) implements BiFunction<SectionClipContext, BlockPos, BlockHitResult> {

        @Override
        public BlockHitResult apply(SectionClipContext clipContext, BlockPos pos) {
            // avoid check in start
            if (BlockPos.containing(clipContext.getFrom()).distSqr(pos) == 0)
                return null;

            BlockState blockstate = clipContext.getBlockState(levelReader, pos);
            // todo add a tag to skip some blocks like leaves
            if (!blockstate.isSolid()) {
                // Fluid fluid = blockstate.getFluidState().getType();
                // if (fluid == Fluids.EMPTY
                //         || !(fluid == Fluids.WATER
                //         || fluid == Fluids.FLOWING_WATER))
                {
                    return null;
                }
            }
            Vec3 vec3 = clipContext.getFrom();
            Vec3 vec31 = clipContext.getTo();

            VoxelShape voxelshape = clipContext.getBlockShape(blockstate, levelReader, pos);
            BlockHitResult blockHitResult = voxelshape.clip(vec3, vec31, pos);
            if (blockHitResult != null) {
                clipContext.release();
                if (notCheck.isPresent()
                        && notCheck.get().blocks().isPresent()) {
                    if (notCheck.get().blocks().get().contains(blockstate.getBlockHolder())) {
                        blockHitResult = BlockHitResult.miss(blockHitResult.getLocation(), blockHitResult.getDirection(), pos);
                    }
                }
            }
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

    public static BlockHitResult clip(LevelReader levelAccessor, SectionClipContext context, Optional<BlockPredicate> notCheck) {
        return BlockGetter.traverseBlocks(context.getFrom(),
                context.getTo(),
                context,
                new BlockTester(levelAccessor, notCheck),
                FAIL_HANDLER);
    }

    public static boolean isInRoom(LevelAccessor level, BlockPos pos, BlockState state, Optional<BlockPredicate> notCheck) {
        // if (state.getFluidState().isSource()) return false;

        boolean isInLight = level.getBrightness(LightLayer.SKY, pos.above()) > 12;
        if (isInLight) {
            int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
            if (height < pos.getY()) return false;
        }
        // if (season == Season.SUMMER) {
        //     if (isInLight && EclipticUtil.isNoon((Level) level))
        //         return false;
        // }
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
            direction = direction.x != 0 || direction.z != 0 ?
                    direction.add(xr, 0, yr) : direction;
            Vec3 startVec = centerVec;

            // direction是否要限制为圆形
            // direction=direction.normalize();

            Vec3 endVec =
                    CommonConfig.Crop.useBoxDistance.get() ?
                            getClampedEndPoint(centerVec, direction, maxDistance, y_maxDistance) :
                            centerVec.add(direction.scale(direction.y == 0 ? maxDistance : y_maxDistance));

            SectionClipContext context = new SectionClipContext(startVec, endVec,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, CollisionContext.empty());
            BlockHitResult hitResult = clip(level, context, notCheck);

            if (hitResult.getType() == HitResult.Type.MISS) {
                isConnected = false;
                break;
            }

        }

        if (isConnected && !isInLight) {
            if (level.getRandom().nextInt(10000) <= CommonConfig.Crop.darkGreenhouseFailChance.get()) {
                isConnected = state.is(EclipticBlockTags.NATURAL_PLANTS);
            }
        }
        return isConnected;
    }


    public static void unloadChunk(Level level, ChunkPos chunkPos) {
        SolarDataManager saveData = SolarHolders.getSaveData(level);
        if (saveData != null)
            saveData.unloadChunk(chunkPos);
    }

    public static void handleChunkTick(Level level, LevelChunk chunk) {
        SolarDataManager saveData = SolarHolders.getSaveData(level);
        if (saveData != null)
            saveData.tickChunk(chunk);
    }
    //
    // public static boolean shouldTick(Level level, LevelChunk chunk) {
    //     SolarDataManager saveData = SolarHolders.getSaveData(level);
    //     if (saveData != null)
    //         return saveData.shouldTickChunk(chunk.getPos());
    //     else return false;
    // }

    public static void handleRandomTick(ServerLevel level, BlockPos pos, BlockState state, List<WetterStructure> wetterStructureList) {
        SolarDataManager saveData = SolarHolders.getSaveData(level);
        if (saveData == null) return;
        // HumidityControlProvider humidityControlProvider = saveData.queryHumidityControlProvider(pos);
        // if (humidityControlProvider != null && humidityControlProvider.getRemainTime() > 0) return;
        boolean hasFound = false;
        WetterStructure needAdd = null;
        for (int j = 0, wetterStructuresSize = wetterStructureList.size(); j < wetterStructuresSize; j++) {
            WetterStructure structure = wetterStructureList.get(j);
            boolean needSkip = false;
            //         structure.core().isEmpty()
            //         || (structure.core().get().blocks().isEmpty())
            //         || (!structure.core().get().blocks().get().contains(state.getBlockHolder()));
            // if (!needSkip) {
            //     // HumidityControlProvider humidityControlProvider = saveData.queryHumidityControlProvider(pos);
            //     // if (humidityControlProvider != null) needSkip = true;
            // }
            if (!needSkip) {
                if (structure.enableAirCheck()) {
                    needSkip = !level.getBlockState(pos.above()).isEmpty();
                }
            }
            if (!needSkip) {
                List<PosAndBlockStateCheck> blockStatePredicate = structure.blockStatePredicate();
                for (int i = 0, blockStatePredicateSize = blockStatePredicate.size(); i < blockStatePredicateSize; i++) {
                    PosAndBlockStateCheck check = blockStatePredicate.get(i);
                    // BlockState stateTested;
                    // if (check.offset().equals(Vec3i.ZERO)) {
                    //     stateTested = state;
                    // } else {
                    //     stateTested = chunk.getBlockState(pos.offset(check.offset()));
                    // }
                    if (!check.block().matches(level, pos.offset(check.offset()))) {
                        needSkip = true;
                        break;
                    }
                }
            }
            if (!needSkip) {
                hasFound = true;
                needAdd = structure;
                break;
            }
        }
        if (hasFound) {
            // if(humidityControlProvider!=null) {
            //     saveData.removeHumidityControlProvider(pos);
            // }
            saveData.addHumidityControlProvider(pos, new HumidityControlProvider(
                    needAdd.level(), needAdd.range() * needAdd.range(), needAdd.lastingTime(), true
            ));
            // level.scheduleTick(pos, state.getBlock(), needAdd.lastingTime());
        }
    }

    public static List<WetterStructure> validTick(BlockState state) {
        List<WetterStructure> wetterStructureList = wetterStructures.getOrDefault(state.getBlock(), List.of());
        List<WetterStructure> wetterStructureListFilter = null;
        // boolean air = state.getBlock() == Blocks.AIR;
        for (WetterStructure wetterStructure : wetterStructureList) {
            boolean use = false;
            if (wetterStructure.core().isPresent() && wetterStructure.core().get().blocks().isPresent()
                // || air
            ) {
                BlockPredicate blockPredicate = wetterStructure.core().get();
                HolderSet<Block> holders = blockPredicate.blocks().get();
                if (holders.contains(state.getBlockHolder())
                        && (blockPredicate.properties().isEmpty() || blockPredicate.properties().get().matches(state))) {
                    use = true;
                }
            } else {
                use = true;
            }
            if (use) {
                if (wetterStructureListFilter == null) wetterStructureListFilter = new ArrayList<>();
                wetterStructureListFilter.add(wetterStructure);
            }
        }
        return wetterStructureListFilter == null ? List.of() :
                wetterStructureList.size() == wetterStructureListFilter.size() ? wetterStructureList : wetterStructureListFilter;
    }

    public static Vec3 getClampedEndPoint(
            Vec3 centerVec,
            Vec3 direction,
            double maxXZDistance,
            double maxYDistance
    ) {
        if (direction.lengthSqr() == 0) return centerVec;

        double dx = direction.x;
        double dy = direction.y;
        double dz = direction.z;

        double scaleX = (dx == 0) ? Double.POSITIVE_INFINITY : maxXZDistance / Math.abs(dx);
        double scaleZ = (dz == 0) ? Double.POSITIVE_INFINITY : maxXZDistance / Math.abs(dz);
        double scaleY = (dy == 0) ? Double.POSITIVE_INFINITY : maxYDistance / Math.abs(dy);

        double scale = Math.min(scaleX, Math.min(scaleZ, scaleY));

        Vec3 scaled = direction.scale(scale);
        return centerVec.add(scaled);
    }


    public static boolean isWithinDistanceForGreenHouseWorker(Vec3 from, Vec3 to, float limit) {
        if (CommonConfig.Crop.useBoxDistance.get()) {
            return Math.abs(from.x - to.x) < limit + 0.1
                    && Math.abs(from.z - to.z) < limit + 0.1
                    && Math.abs(from.y - to.y) < limit + 0.1;
        } else {
            return from.distanceToSqr(to) < (limit * limit + 0.1);
        }
    }

    public static List<Component> appendInfo(Level level, BlockState state) {
        List<Component> toolTip = new ArrayList<>();

        if (!CommonConfig.Crop.enableCropHumidityControl.get() && !CommonConfig.Crop.enableCrop.get()) return toolTip;
        Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap = CropGrowthHandler.getControlMap(state.getBlock());
        if (controlMap == null) return toolTip;
        Holder<AgroClimaticZone> defaultAgroClimaticZoneHolder = CropGrowthHandler.getDefaultAgroClimaticZoneHolder(level);
        if (defaultAgroClimaticZoneHolder == null) return toolTip;

        if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            List<Humidity> humidityList = CropGrowthHandler.getLikeHumidityInTemperate(
                    state, controlMap, defaultAgroClimaticZoneHolder
            );
            if (!humidityList.isEmpty())
                toolTip.addAll(CropHumidityInfo.getTooltip(humidityList.getFirst(), humidityList.getLast()));
        }
        if (CommonConfig.Crop.enableCrop.get()) {
            List<Season> seasons =
                    CropGrowthHandler.getLikeSeasonsInTemperate(state, controlMap, defaultAgroClimaticZoneHolder
                    );
            if (!seasons.isEmpty())
                toolTip.addAll(CropSeasonInfo.getTooltip(CropSeasonInfo.getSeason(seasons)));
        }
        return toolTip;
    }


}
