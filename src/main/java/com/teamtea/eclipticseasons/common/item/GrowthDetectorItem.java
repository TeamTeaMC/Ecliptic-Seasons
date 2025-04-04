package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class GrowthDetectorItem extends Item {
    public GrowthDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            Level level = context.getLevel();
            BlockPos clickedPos = context.getClickedPos();
            BlockState blockState = level.getBlockState(clickedPos);
            if (!blockState.isAir() && CropGrowthHandler.getControlMap(blockState.getBlock()) != null) {
                if (!level.isClientSide()) {
                    MutableComponent component = Component.translatable("item.eclipticseasons.growth_detector.hint.title");
                    SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);

                    Holder<Biome> biomeHolder = CropGrowthHandler.getCropBiome(level, clickedPos);
                    Holder<AgroClimaticZone> climateTypeHolder = CropGrowthHandler.getclimateTypeHolder(biomeHolder);
                    if (climateTypeHolder != null) {
                        component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.agro_climatic_zone", Component.translatable(AgroClimaticZone.getDescriptionId((climateTypeHolder.unwrapKey().get().location())))));
                    }

                    Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap = CropGrowthHandler.getControlMap(blockState.getBlock());
                    CropGrowControl growControl = CropGrowthHandler.getCropGrowControl(controlMap, climateTypeHolder);
                    float chance = 0;
                    for (int i = 0; i < 100; i++) {
                        chance += CropGrowthHandler.isInRoom(level, clickedPos, blockState,  growControl.notGreenHouse()) ? 1 : 0;
                    }
                    int chose = chance > 50 ? 1 : chance > 10 ? 2 : 3;

                    component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.greenroom_" + chose, blockState.getBlock().getName()));


                    chance = 0;
                    for (int i = 0; i < 100; i++) {
                        chance += getGrowChance(level, clickedPos, blockState);
                    }
                    chose = chance > 80f ? 1 : chance > 60f ? 2 : chance > 40f ? 3 : chance > 20f ? 4 : chance > 0f ? 5 : 6;
                    component.append(Component.translatable("item.eclipticseasons.growth_detector.hint.grow_chance_" + chose, chance));

                    player.sendSystemMessage(component);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
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

        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        Season season = solarTerm.getSeason();

        if (growControl == null) return result;

        GrowParameter growParameter = CropGrowthHandler.getSeasonGrowParameter(growControl, solarTerm, controlMap, agentClimateTypeHolder, climateTypeHolder);
        CropGrowthHandler.RoomStatus roomStatus = CropGrowthHandler.isInRoom(level, pos, blockState,growControl.notGreenHouse()) ? CropGrowthHandler.RoomStatus.GREEN_HOUSE : CropGrowthHandler.RoomStatus.NORMAL;

        if (growParameter != null && CommonConfig.Crop.enableCrop.get()) {
            result *= growParameter.grow_chance();
            if (result < 1) {
                if (roomStatus == CropGrowthHandler.RoomStatus.GREEN_HOUSE) {
                    if (CropGrowthHandler.getGreenHouseProvider(level, pos, controlMap, agentClimateTypeHolder) != null) {
                        result = 1;
                    }
                }
            }
        }

        if (CommonConfig.Crop.enableCropHumidityControl.get()) {
            Humidity env = EclipticUtil.getHumidityAt(level, solarTerm, biomeHolder, pos, !level.isClientSide());
            result *= getHumidityGrowChance(level, growControl, env, roomStatus, pos, blockState, season, false);
        }

        return result;
    }

    public static float getHumidityGrowChance(LevelAccessor world, CropGrowControl growControl, Humidity env, CropGrowthHandler.RoomStatus roomStatus, BlockPos pos, BlockState blockState, Season season, boolean hasUpdate) {
        float result = 1;
        if (growControl != null) {
            GrowParameter growParameter = growControl.getGrowParameter(env);
            if (growParameter != null) {
                float f = growParameter.grow_chance();
                if (f == 0) {
                    result = 0;
                } else if (f > 1.0F) {
                    result = 1;
                } else if (f <= 1.0F) {
                    if (hasUpdate) {
                        result = f;
                    } else {
                        int modification = SolarHolders.getSaveData((Level) world).calculateHumidityModification(pos);
                        if (modification != 0 && roomStatus == CropGrowthHandler.RoomStatus.GREEN_HOUSE) {
                            env = env.cycle(modification);
                            result = getHumidityGrowChance(world, growControl, env, roomStatus, pos, blockState, season, true);
                        } else if (((Level) world).isRainingAt(pos)) {
                            env = env.cycle(1);
                            result = getHumidityGrowChance(world, growControl, env, roomStatus, pos, blockState, season, true);
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
