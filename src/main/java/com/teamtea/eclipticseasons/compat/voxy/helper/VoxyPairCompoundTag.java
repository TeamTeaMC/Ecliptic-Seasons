package com.teamtea.eclipticseasons.compat.voxy.helper;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;

@Accessors(chain = true)
public class VoxyPairCompoundTag extends CompoundTag {
    @Setter
    @Getter
    public CompoundTag original;

    @Setter
    @Getter
    public CompoundTag above;
}
