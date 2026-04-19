package com.teamtea.eclipticseasons.mixin.common.level;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow
    public abstract Iterable<ServerLevel> getAllLevels();

    @Inject(at = {@At(value = "TAIL")}, method = {"setWeatherParameters"})
    private void eclipticseasons$precipitationAt_endBiomeCheck(int clearTime, int rainTime, boolean raining, boolean thundering, CallbackInfo ci) {
        Iterator<ServerLevel> iterator = getAllLevels().iterator();
        if (iterator.hasNext())
            WeatherManager.onSetWeatherParameters(iterator.next(), clearTime, rainTime, raining, thundering);
    }


}
