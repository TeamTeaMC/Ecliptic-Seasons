package com.teamtea.eclipticseasons.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class ESClientEntityTickEvent extends EntityEvent {
    public ESClientEntityTickEvent(Entity entity) {
        super(entity);
    }
}
