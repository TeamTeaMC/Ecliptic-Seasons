package com.teamtea.eclipticseasons.compat.cold_sweat;

import com.momosoftworks.coldsweat.api.event.core.init.GatherDefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.UndergroundTempModifier;
import com.momosoftworks.coldsweat.api.util.Placement;
import com.teamtea.eclipticseasons.EclipticSeasons;

public class Cold_Sweat {
    public static final Cold_Sweat INSTANCE = new Cold_Sweat();

    public void registerTempModifiers(TempModifierRegisterEvent event) {
        event.register(EclipticSeasons.rl("season"), ESTempModifier::new);
    }

    public void defineDefaultModifiers(GatherDefaultTempModifiersEvent event) {
        event.addModifierById(EclipticSeasons.rl( "season"),
                mod -> mod.tickRate(60),
                Placement.Duplicates.BY_CLASS,
                Placement.of(Placement.Mode.BEFORE, Placement.Order.FIRST, mod2 -> mod2 instanceof UndergroundTempModifier));
    }
}
