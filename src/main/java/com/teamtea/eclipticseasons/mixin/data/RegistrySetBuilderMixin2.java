// package com.teamtea.eclipticseasons.mixin.data;
//
// import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
// import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
// import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
//
// import java.util.List;
// import java.util.Map;
//
// @Mixin(targets = "net.minecraft.core.RegistrySetBuilder")
// public abstract class RegistrySetBuilderMixin2 {
//
//     // @ModifyExpressionValue(method = {"build"},
//     //         at = {@At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z")})
//     // private static <E> boolean eclipticseasons$buildPatch$fixError(boolean original) {
//     //     // if ("true".equals(System.getProperty("eclipticseasons.runs.runData"))
//     //     // )
//     //     {
//     //         return true;
//     //     }
//     //     // return original;
//     // }
//
// }