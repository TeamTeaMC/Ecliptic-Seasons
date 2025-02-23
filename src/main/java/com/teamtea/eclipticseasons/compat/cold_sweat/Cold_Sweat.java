package com.teamtea.eclipticseasons.compat.cold_sweat;

import com.momosoftworks.coldsweat.api.event.core.init.GatherDefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.ElevationTempModifier;
import com.momosoftworks.coldsweat.api.util.Placement;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Cold_Sweat {
    public static final Cold_Sweat INSTANCE = new Cold_Sweat();

    @SubscribeEvent
    public void registerTempModifiers(TempModifierRegisterEvent event) {
        event.register(EclipticSeasons.rl("season"), ESTempModifier::new);
    }

    @SubscribeEvent
    public void defineDefaultModifiers(GatherDefaultTempModifiersEvent event) {
        if (event.getTrait() == Temperature.Trait.WORLD) {
            event.addModifierById(EclipticSeasons.rl("season"), mod -> mod.tickRate(60),
                    Placement.Duplicates.BY_CLASS,
                    Placement.of(Placement.Mode.BEFORE,
                            Placement.Order.FIRST,
                            mod2 -> mod2 instanceof ElevationTempModifier));
        }
    }
}
