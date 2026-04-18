package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.crop.CropGrow;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.GreenHouseCoreProvider;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GrowthDetectorItem extends Item {
    public GrowthDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            Level level = context.getLevel();
            BlockPos clickedPos = context.getClickedPos();
            BlockState state = level.getBlockState(clickedPos);
            if (!state.isAir() && CropGrowthHandler.getControlMap(state.getBlock()) != null) {
                if (level instanceof ServerLevel serverLevel) {
                    MutableComponent component = Component.translatable("item.eclipticseasons.growth_detector.hint.title");

                    Holder<Biome> biomeHolder = CropGrowthHandler.getCropBiome(level, clickedPos);
                    Holder<AgroClimaticZone> climateTypeHolder = CropGrowthHandler.getclimateTypeHolder(biomeHolder);
                    if (climateTypeHolder != null) {
                        component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.agro_climatic_zone", Component.translatable(AgroClimaticZone.getDescriptionId(EclipticSeasons.parse(climateTypeHolder.getRegisteredName())))));
                    }

                    Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap = CropGrowthHandler.getControlMap(state.getBlock());
                    if (controlMap == null) return super.useOn(context);

                    CropGrowControl growControl = CropGrowthHandler.getCropGrowControl(controlMap, climateTypeHolder);
                    Holder<AgroClimaticZone> agent = CropGrowthHandler.getDefaultAgroClimaticZoneHolder(level);
                    if (growControl == null) {
                        growControl = CropGrowthHandler.getCropGrowControl(controlMap, agent);
                    }
                    if (growControl != null) {
                        if (growControl.base().equals(CropGrow.EMPTY)) {
                            if (growControl.blocks().isEmpty() ||
                                    !growControl.blocks().get().containsKey(state))
                                return super.useOn(context);
                        }
                    }

                    float chance = 0;
                    int chose = 0;
                    if (growControl != null) {
                        for (int i = 0; i < 100; i++) {
                            chance += CropGrowthHandler.isInRoom(level, clickedPos, state, growControl.notGreenHouse()) ? 1 : 0;
                        }
                        chose = chance > 50 ? 1 : chance > 10 ? 2 : 3;
                        component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.greenroom_" + chose, state.getBlock().getName()));
                    }

                    chance = 0;
                    for (int i = 0; i < 100; i++) {
                        chance += getGrowChance(level, clickedPos, state);
                    }
                    chose = chance > 80f ? 1 : chance > 60f ? 2 : chance > 40f ? 3 : chance > 20f ? 4 : chance > 0f ? 5 : 6;

                    if (chance <= 40) {
                        List<Season> seasons = CropGrowthHandler.getLikeSeasonsInTemperate(state, controlMap, agent);
                        if (!seasons.isEmpty()) {
                            SolarDataManager saveData = SolarHolders.getSaveData(level);
                            if (saveData != null && saveData.findNearGreenHouseProvider(clickedPos, seasons) == null) {
                                component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.season_core"));
                            }
                        }
                        List<Humidity> humidityList = CropGrowthHandler.getLikeHumidityInTemperate(state, controlMap, agent);
                        if (!humidityList.isEmpty()) {
                            if (!humidityList.contains(EclipticSeasonsApi.getInstance().getAdjustedHumidity(serverLevel, clickedPos))) {
                                component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.humidity"));
                            }
                        }
                    }
                    component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.grow_chance_" + chose, chance));
                    // component.append(","+chance);
                    if (player instanceof ServerPlayer serverPlayer)
                        serverPlayer.sendSystemMessage(component);
                }
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.useOn(context);
    }


    public static float getGrowChance(Level level, BlockPos pos, BlockState blockState) {
        float result = 1f;
        Block block = blockState.getBlock();
        Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap = CropGrowthHandler.getControlMap(block);
        if (controlMap == null) return result;

        Holder<Biome> biomeHolder = CropGrowthHandler.getCropBiome(level, pos);
        Holder<AgroClimaticZone> climateTypeHolder = CropGrowthHandler.getclimateTypeHolder(biomeHolder);
        if (climateTypeHolder == null) return result;

        Holder<AgroClimaticZone> agentClimateTypeHolder = CropGrowthHandler.getDefaultAgroClimaticZoneHolder(level);
        CropGrowControl growControl = CropGrowthHandler.getCropGrowControl(controlMap, climateTypeHolder);
        CropGrowControl agentGrowControl = CropGrowthHandler.getCropGrowControl(controlMap, agentClimateTypeHolder);
        if (growControl == null && agentGrowControl == null) {
            return result;
        }

        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        Season season = solarTerm.getSeason();

        GrowParameter growParameter = CropGrowthHandler.getSeasonGrowParameter(blockState, growControl, agentGrowControl, solarTerm, climateTypeHolder);
        Optional<BlockPredicate> notGreenHouse =
                growControl != null ? growControl.notGreenHouse() : agentGrowControl.notGreenHouse();
        CropGrowthHandler.RoomStatus roomStatus = CropGrowthHandler.isInRoom(level, pos, blockState, notGreenHouse) ? CropGrowthHandler.RoomStatus.GREEN_HOUSE : CropGrowthHandler.RoomStatus.NORMAL;

        if (growParameter != null && CommonConfig.Crop.enableCrop.get()) {
            result *= growParameter.grow_chance();
            if (result < 1) {
                if (CommonConfig.Crop.simpleGreenHouse.get() ||
                        roomStatus == CropGrowthHandler.RoomStatus.GREEN_HOUSE) {
                    if (CropGrowthHandler.getGreenHouseProvider(level, pos, blockState, controlMap, agentClimateTypeHolder) != null) {
                        result = 1;
                    }
                }
            }
        }

        if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            float env = EclipticUtil.getHumidityLevelAt(level, solarTerm, biomeHolder, pos, !level.isClientSide());
            result *= getHumidityGrowChance(level, growControl != null ? growControl : agentGrowControl, env, roomStatus, pos, blockState, season, false);
        }

        return result;
    }

    public static float getHumidityGrowChance(Level level, CropGrowControl growControl, float env, CropGrowthHandler.RoomStatus roomStatus, BlockPos pos, BlockState blockState, Season season, boolean hasUpdate) {
        float result = 1;
        env = Mth.clamp(env, 0, Humidity.collectValues().length - 1);
        if (growControl != null) {
            GrowParameter growParameter = growControl.getGrowParameter(env, blockState);
            if (growParameter != null) {
                float f = growParameter.grow_chance();
                if (f > 1.0F) {
                    result *= f;
                } else if (f <= 1.0F) {
                    if (hasUpdate) {
                        result = f;
                    } else {
                        if (CommonConfig.Crop.simpleGreenHouse.get()
                                && roomStatus == CropGrowthHandler.RoomStatus.GREEN_HOUSE) {
                            result = 1f;
                            return result;
                        }
                        float modification =
                                CommonConfig.Crop.simpleGreenHouse.get() ? 0 :
                                        SolarHolders.getSaveData(level) instanceof SolarDataManager sd ? sd.calculateHumidityModification(pos) : 0;
                        if (modification != 0 && roomStatus == CropGrowthHandler.RoomStatus.GREEN_HOUSE) {
                            env += (modification);
                            result = getHumidityGrowChance(level, growControl, env, roomStatus, pos, blockState, season, true);
                        } else if (level.isRainingAt(pos)) {
                            env += (1);
                            result = getHumidityGrowChance(level, growControl, env, roomStatus, pos, blockState, season, true);
                        } else {
                            result *= f;
                        }
                    }
                }
            }
        }
        return result;
    }

}
