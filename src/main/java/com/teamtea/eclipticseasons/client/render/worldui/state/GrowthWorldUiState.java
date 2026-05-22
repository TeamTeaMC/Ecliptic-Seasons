package com.teamtea.eclipticseasons.client.render.worldui.state;

import com.teamtea.eclipticseasons.common.item.info.GrowthInfo;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public record GrowthWorldUiState(
        BlockPos pos,
        Component title,
        List<Component> lines,
        float yOffset,
        int backgroundColor,
        int borderColor,
        int textColor,
        int titleColor,
        float scale
) {

    public static GrowthWorldUiState from(GrowthInfo info) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.translatable(
                "ui.eclipticseasons.growth_chance",
                String.format("%.0f%%", info.growChance())
        ));

        lines.add(Component.translatable(
                "ui.eclipticseasons.greenhouse_" + info.greenhouseLevel()
        ));

        if (!CommonConfig.Crop.simpleGreenHouse.get()) {
            if (info.needsSeasonCore()) {
                lines.add(Component.translatable("ui.eclipticseasons.issue.needs_season_core"));
            } else if (info.humidityMismatch()) {
                lines.add(Component.translatable("ui.eclipticseasons.issue.humidity_mismatch"));
            } else {
                lines.add(Component.translatable("ui.eclipticseasons.issue.all_conditions_met"));
            }
        }

        return new GrowthWorldUiState(
                info.pos(),
                info.cropName(),
                lines,
                1.35F,
                0xAA101010,
                0xCCB8A060,
                0xFFFFFFFF,
                0xFFFFE0A0,
                0.015F
        );
    }

}