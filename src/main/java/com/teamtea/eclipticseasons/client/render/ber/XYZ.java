package com.teamtea.eclipticseasons.client.render.ber;

import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class XYZ {
    public static Quaternionf deg_to_rad(float angleX, float angleY, float angleZ){
        return new Quaternionf().rotateXYZ(angleX* Mth.DEG_TO_RAD , angleY * Mth.DEG_TO_RAD , angleZ* Mth.DEG_TO_RAD);
    }

    public static Quaternionf deg_to_rad(int angleX, int angleY, int angleZ){
        return new Quaternionf().rotateXYZ(angleX* Mth.DEG_TO_RAD, angleY* Mth.DEG_TO_RAD, angleZ* Mth.DEG_TO_RAD);
    }
}
