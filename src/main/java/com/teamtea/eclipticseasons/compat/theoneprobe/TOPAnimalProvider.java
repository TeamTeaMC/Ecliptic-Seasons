package com.teamtea.eclipticseasons.compat.theoneprobe;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import mcjty.theoneprobe.api.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
