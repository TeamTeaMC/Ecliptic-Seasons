package com.teamtea.eclipticseasons.compat.theoneprobe;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.InterModComms;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class TheOneProbeProvide implements Function<ITheOneProbe, Void> {
    @Nullable
    @Override
    public Void apply(@Nullable ITheOneProbe probe) {
        if (probe != null) {
            probe.registerProvider(new TOPCropProvider());
            probe.registerProvider(new TOPCauldronProvider());
            probe.registerProvider(new TOPGreenHouseCoreProvider());
            probe.registerEntityProvider(new TOPAnimalProvider());
        }
        return null;
    }

    public static void init() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeProvide::new);
    }
}
