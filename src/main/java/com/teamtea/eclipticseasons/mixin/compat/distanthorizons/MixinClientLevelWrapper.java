package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;

import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper_forge;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_forge;
import com.seibel.distanthorizons.core.dataObjects.BlockBiomeWrapperPair;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Map;

@Mixin(value = ClientLevelWrapper_forge.class, remap = false)
@ConditionalMixin(value = "distanthorizons", version = "3.0.0-b")
public abstract class MixinClientLevelWrapper {

    // or mixin into SharedApi.setDhWorld(null)
    @Inject(
            method = "onUnload",
            at = @At("TAIL"),
            remap = false
    )
    private void eclipticseasons$clearBiomeCaches(CallbackInfo ci) {
        eclipticseasons$clearBlockBiomePairCache();

        BiomeWrapper_forge.WRAPPER_BY_BIOME.clear();
        BiomeWrapper_forge.WRAPPER_BY_RESOURCE_LOCATION.clear();

    }

    @Unique
    private static void eclipticseasons$clearBlockBiomePairCache() {
        try {
            Field field = BlockBiomeWrapperPair.class.getDeclaredField(
                    "CACHED_PAIR_BY_BIOME_BY_BLOCK"
            );

            field.setAccessible(true);

            Object value = field.get(null);

            if (value instanceof Map<?, ?> cache) {
                cache.clear();
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }
}