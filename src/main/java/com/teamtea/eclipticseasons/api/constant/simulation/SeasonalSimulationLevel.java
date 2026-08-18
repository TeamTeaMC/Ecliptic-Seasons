package com.teamtea.eclipticseasons.api.constant.simulation;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/**
 * Defines the depth of seasonal simulation applied to the world.
 *
 * <p>Higher levels include all effects from lower levels and introduce
 * additional seasonal mechanics.</p>
 *
 * <pre>
 * ENVIRONMENT
 *     Basic seasonal changes affecting the world and atmosphere.
 *
 * ECOLOGY
 *     Seasonal changes affecting natural systems and living environments.
 *
 * AGRICULTURE
 *     Seasonal changes affecting farming and cultivation systems.
 *
 * SURVIVAL
 *     Seasonal changes affecting player gameplay and difficulty.
 * </pre>
 */
public enum SeasonalSimulationLevel implements ITranslatable {

    /**
     * Only applies seasonal changes to the environment.
     *
     * <p>Includes basic world simulation such as seasonal atmosphere,
     * weather-related effects, and environmental changes.
     * No gameplay restrictions are introduced.</p>
     */
    ENVIRONMENT(ChatFormatting.AQUA),

    /**
     * Adds seasonal effects to natural systems.
     *
     * <p>Includes changes to natural blocks, vegetation states,
     * wildlife-related systems, and other ecological behaviors.</p>
     */
    ECOLOGY(ChatFormatting.DARK_GREEN),

    /**
     * Adds seasonal effects to agricultural systems.
     *
     * <p>Includes crop growth rules, farming restrictions,
     * and other cultivation-related mechanics.</p>
     */
    AGRICULTURE(ChatFormatting.GREEN),

    /**
     * Adds seasonal gameplay mechanics affecting players.
     *
     * <p>Includes survival-oriented features such as temperature
     * effects, heat-related mechanics, and other difficulty changes.</p>
     */
    SURVIVAL(ChatFormatting.DARK_AQUA),

    /**
     * Custom mode allows users to configure each seasonal feature individually.
     */
    CUSTOM(ChatFormatting.RED);


    private final ChatFormatting color;

    SeasonalSimulationLevel(ChatFormatting color) {
        this.color = color;
    }

    public static final Codec<SeasonalSimulationLevel> CODEC =
            StringRepresentable.fromEnum(SeasonalSimulationLevel::values);

    public static void onSeasonalSimulationLevelChange(SeasonalSimulationLevel current) {
        if (current == CUSTOM || CommonConfig.getSeasonalSimulationLevel() == current) return;

        CommonConfig.Season.seasonalSimulationLevel.clearCache();
        CommonConfig.Season.seasonalSimulationLevel.set(current);

        CommonConfig.setSeasonalSimulationLevel(current);

        CommonConfig.Crop.enableCrop.set(
                current.enable(AGRICULTURE));

        CommonConfig.Temperature.heatStroke.set(
                current.enable(SURVIVAL));
        CommonConfig.Animal.enableBee.set(
                current.enable(SURVIVAL));
        CommonConfig.Animal.enableBreed.set(
                current.enable(SURVIVAL));
        CommonConfig.Animal.enableTimeBreed.set(
                current.enable(SURVIVAL));
        CommonConfig.Animal.enableFishing.set(
                current.enable(SURVIVAL));
    }

    @Override
    public Component getTranslation() {
        return Component.translatable(
                "info.eclipticseasons.seasonal_simulation_level." + getName()
        ).withStyle(color);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(
                "info.eclipticseasons.seasonal_simulation_level." + getName() + ".tooltip"
        );
    }

    public boolean enable(SeasonalSimulationLevel taget) {
        return this.ordinal() >= taget.ordinal();
    }
}