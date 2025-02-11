package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EffectRegistry {
    public static final MobEffect HEAT_STROKE = new HeatStrokeEffect(MobEffectCategory.NEUTRAL, 0xf9d27d);

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registry.MOB_EFFECT.key(), soundEventRegisterHelper -> {
            soundEventRegisterHelper.register(EclipticSeasons.rl("heat_stroke"), HEAT_STROKE);
        });


    }


}
