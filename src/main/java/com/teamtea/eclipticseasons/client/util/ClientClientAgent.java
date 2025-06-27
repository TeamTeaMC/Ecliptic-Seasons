package com.teamtea.eclipticseasons.client.util;

import com.teamtea.eclipticseasons.common.misc.ClientAgent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

public class ClientClientAgent implements ClientAgent {

    @Override
    public Entity getCameraEntity() {
        return Minecraft.getInstance().getCameraEntity();
    }

    @Override
    public HitResult getHitResult() {
        return Minecraft.getInstance().hitResult;
    }
}
