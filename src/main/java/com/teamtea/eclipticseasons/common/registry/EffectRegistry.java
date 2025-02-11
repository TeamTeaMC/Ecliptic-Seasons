package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EffectRegistry {
    public static final Effect HEAT_STROKE = new HeatStrokeEffect(EffectType.NEUTRAL, 0xf9d27d);

    @SubscribeEvent
    public static void blockRegister(RegistryEvent.Register<Effect> event) {
        // event.register(Registry.MOB_EFFECT.key(), soundEventRegisterHelper -> {
        //     soundEventRegisterHelper.register(rl("heat_stroke"), HEAT_STROKE);
        // });
        HEAT_STROKE.setRegistryName(EclipticSeasons.rl("heat_stroke"));
        event.getRegistry().register(HEAT_STROKE);
    }


}
