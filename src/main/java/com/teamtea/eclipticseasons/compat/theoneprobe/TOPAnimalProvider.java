package com.teamtea.eclipticseasons.compat.theoneprobe;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import com.teamtea.eclipticseasons.config.ClientConfig;
import mcjty.theoneprobe.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TOPAnimalProvider implements IProbeInfoEntityProvider {
    @Override
    public String getID() {
        return EclipticSeasons.rl("animal").toLanguageKey();
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (entity instanceof LivingEntity livingEntity)
            AnimalHooks.getBreedInfo(livingEntity).forEach(iProbeInfo::mcText);
    }

}
