package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class EffectRegistry {
    public static class Effects {
        public static final ResourceKey<MobEffect> HEAT_STROKE = ResourceKey.create(Registries.MOB_EFFECT, EclipticSeasons.rl("heat_stroke"));
    }

    public static final MobEffect HEAT_STROKE = new HeatStrokeEffect(MobEffectCategory.NEUTRAL, 0xf9d27d);


    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registries.MOB_EFFECT, helper -> {
            helper.register(Effects.HEAT_STROKE, HEAT_STROKE);
        });
    }

}
