package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EffectRegistry {
    public static final MobEffect HEAT_STROKE = new HeatStrokeEffect(MobEffectCategory.NEUTRAL, 0xf9d27d);

    @SubscribeEvent
    public static void blockRegister(RegistryEvent.Register<MobEffect> event) {
        // event.register(Registry.MOB_EFFECT.key(), soundEventRegisterHelper -> {
        //     soundEventRegisterHelper.register(rl("heat_stroke"), HEAT_STROKE);
        // });
        HEAT_STROKE.setRegistryName(EclipticSeasons.rl("heat_stroke"));
        event.getRegistry().register(HEAT_STROKE);
    }


}
