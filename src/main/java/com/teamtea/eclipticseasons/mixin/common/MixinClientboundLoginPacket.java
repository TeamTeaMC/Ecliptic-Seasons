package com.teamtea.eclipticseasons.mixin.common;


import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundLoginPacket.class)
public class MixinClientboundLoginPacket {


    @Shadow @Final private RegistryAccess.Frozen registryHolder;

    // @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeWithCodec(Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V")}, method = {"write"})
    // private <T> void eclipticseasons$fetch(FriendlyByteBuf instance, DynamicOps<Tag> pOps, Codec<T> pCodec, T pValue, Operation<Void> original) {
    //     RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(NbtOps.INSTANCE, WeatherManager.fetchLevelIfNull(null).registryAccess());
    //     instance.writeWithCodec(BUILTIN_CONTEXT_OPS, RegistrySynchronization.NETWORK_CODEC, registryHolder);
    // }


}
