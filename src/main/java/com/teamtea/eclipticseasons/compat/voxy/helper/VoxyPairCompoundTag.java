package com.teamtea.eclipticseasons.compat.voxy.helper;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

@Accessors(chain = true)
public class VoxyPairCompoundTag {
    @Setter
    @Getter
    public CompoundTag original;

    @Setter
    @Getter
    public CompoundTag above;


    public CompoundTag pack() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("es_voxy_original", original);
        compoundTag.put("es_voxy_above", above);
        return compoundTag;
    }

    public static VoxyPairCompoundTag unpack(CompoundTag compoundTag) {
        if (compoundTag.contains("es_voxy_above")) {
            return new VoxyPairCompoundTag()
                    .setAbove(compoundTag.getCompoundOrEmpty("es_voxy_above"))
                    .setOriginal(compoundTag.getCompoundOrEmpty("es_voxy_original"));
        }
        return null;
    }
}
