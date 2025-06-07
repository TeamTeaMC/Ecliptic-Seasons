package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.block.model.multipart.Condition;

public interface ConditionLike extends Condition {

    Codec<ConditionLike> CODEC= Codec.lazyInitialized(()->ALlConditionLike.CODEC);

    String getTypeKey();
}
