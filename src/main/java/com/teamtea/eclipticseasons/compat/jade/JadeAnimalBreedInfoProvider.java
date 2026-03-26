package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class JadeAnimalBreedInfoProvider implements IEntityComponentProvider {
   public static JadeAnimalBreedInfoProvider INSTANCE = new JadeAnimalBreedInfoProvider();

   @Override
   public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
       if (entityAccessor.getEntity() instanceof LivingEntity livingEntity)
           iTooltip.addAll(AnimalHooks.getBreedInfo(livingEntity));
   }

   @Override
   public Identifier getUid() {
       return EclipticSeasons.rl("animal");
   }

   @Override
   public int getDefaultPriority() {
       return 5000;
   }
}
