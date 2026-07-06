package com.teamtea.eclipticseasons.common.core.map.stub;

import net.minecraft.core.HolderOwner;
import org.jspecify.annotations.NonNull;

public class DummyHolderOwner<T> implements HolderOwner<T> {
    @Override
    public boolean canSerializeIn(@NonNull HolderOwner<T> context) {
        return false;
    }
}
