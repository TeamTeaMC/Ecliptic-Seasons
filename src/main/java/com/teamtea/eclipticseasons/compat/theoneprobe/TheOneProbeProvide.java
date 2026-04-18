package com.teamtea.eclipticseasons.compat.theoneprobe;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.neoforged.fml.InterModComms;

import org.jspecify.annotations.Nullable;
import java.util.function.Function;

public final class TheOneProbeProvide implements Function<ITheOneProbe, Void> {
    @Nullable
    @Override
    public Void apply(@Nullable ITheOneProbe probe) {
        if (probe != null) {
            //probe.registerProvider(new TOPCropProvider());
            //probe.registerProvider(new TOPCauldronProvider());
            //probe.registerProvider(new TOPGreenHouseCoreProvider());
            probe.registerEntityProvider(new TOPAnimalProvider());
        }
        return null;
    }

    public static void init() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeProvide::new);
    }
}
