package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.teamtea.eclipticseasons.client.util.ClientRef;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ClientRef.class})
public abstract class MixinTintWithoutLevelOverrider {

//    @Inject(
//            remap = false,
//            method = "onClientPlayerExit",
//            at = @At(value = "TAIL")
//    )
//    private static void eclipticseasons$onClientPlayerExit(CallbackInfo ci) {
//        // 也不知道为什么DH不重置
//        try {
//            Field field = BiomeWrapper.class.getDeclaredField("WRAPPER_BY_RESOURCE_LOCATION");
//            field.setAccessible(true);
//            Object map = field.get(null);
//            if (map instanceof Map<?, ?>) {
////                ((Map<?, ?>) map).clear();
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            EclipticSeasons.logger(e);
//        }
//    }

    // @WrapOperation(
    //         remap = false,
    //         method = "<init>",
    //         at = @At(value = "INVOKE", ordinal = 0, target = "LloaderCommon/forge/com/seibel/distanthorizons/common/wrappers/block/TintWithoutLevelOverrider;unwrap(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/level/biome/Biome;")
    // )
    // private Biome eclipticseasons$init_unwrap(Holder<Biome> biome,
    //                                    Operation<Biome> original,
    //                                    @Local(argsOnly = true) BiomeWrapper biomeWrapper,
    //                                    @Local(argsOnly = true) IClientLevelWrapper iClientLevelWrapper) {
    //     // 也许我们都不喜欢它，但是这必须要修复，否则将会传入DH的缓存Biome，导致我们无法正确读取当前的温度
    //     // 难道DH不知道它会丢失吗一旦重启关卡，我认为也许他们不在乎
    //     if (BiomeClimateManager.BIOME_TAG_KEY_MAP.getOrDefault(biomeWrapper.biome.get(), null) == null) {
    //         var biomeObject = DHTool.recoverBiomeObject(biomeWrapper, iClientLevelWrapper);
    //         if (biomeObject != null)
    //             return biomeObject;
    //     }
    //     return original.call(biome);
    // }


}
