package com.teamtea.eclipticseasons.compat.theoneprobe;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.Platform;

import java.lang.reflect.Method;

public class TOPReflector {
    public static void init() {
        if (Platform.isModLoaded("theoneprobe")){
            try {
                Class<?> manualWorkClass = Class.forName("com.teamtea.eclipticseasons.compat.theoneprobe.TheOneProbeProvide");
                Object manualWorkInstance = manualWorkClass.getDeclaredConstructor().newInstance();
                Method loadMethod = manualWorkClass.getMethod("init");
                loadMethod.invoke(manualWorkInstance);
            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }
        }
    }
}
