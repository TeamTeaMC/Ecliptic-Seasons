package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.client.renderer.block.model.multipart.Condition;

public interface ConditionLike extends Condition {

    Codec<ConditionLike> CODEC= CodecUtil.lazyInitialized(()->ALlConditionLike.CODEC);

    String getTypeKey();
}
