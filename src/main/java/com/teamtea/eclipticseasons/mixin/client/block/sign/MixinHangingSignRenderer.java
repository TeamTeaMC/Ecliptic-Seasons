package com.teamtea.eclipticseasons.mixin.client.block.sign;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HangingSignRenderer.class)
public class MixinHangingSignRenderer {
    // @ModifyExpressionValue(
    //         method = "render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;getWoodType(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/state/properties/WoodType;")
    // )
    // private WoodType eclipticseasons$get_wrap_wood_type(WoodType original, @Local(argsOnly = true) SignBlockEntity blockEntity) {
    //     if (blockEntity instanceof QuestHangingSignBlockEntity questHangingSignBlockEntity)
    //         return questHangingSignBlockEntity.getSignType().type();
    //     return original;
    // }
}
