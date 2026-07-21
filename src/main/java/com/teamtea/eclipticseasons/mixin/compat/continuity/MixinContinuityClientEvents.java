package com.teamtea.eclipticseasons.mixin.compat.continuity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import me.pepperbell.continuity.client.ContinuityClientEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ContinuityClientEvents.class)
@Pseudo
public abstract class MixinContinuityClientEvents {

    @Unique
    private static final Set<Identifier> eclipticseasons$SKIPPED_CTM = ConcurrentHashMap.newKeySet();

    @WrapOperation(remap = false, require = 0, method = "onModifyBakingResult",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static <K, V> V eclipticseasons$skipSnowyModel(Map<K, V> instance, K k, V v, Operation<V> original) {
        if (k instanceof BlockState state) {
            ResourceKey<Block> key = state.typeHolder().getKey();
            if (key != null) {
                Identifier identifier = key.identifier();
                if (EclipticSeasonsApi.MODID.equals(identifier.getNamespace())
                        && identifier.getPath().startsWith("snowy_")) {
                    eclipticseasons$SKIPPED_CTM.add(identifier);
                    return v;
                }
            }
        }
        return original.call(instance, k, v);
    }


    @Inject(
            remap = false,
            require = 0,
            method = "onModifyBakingResult",
            at = @At("TAIL")
    )
    private static void eclipticseasons$logSkippedModels(
            ModelEvent.ModifyBakingResult event,
            CallbackInfo ci
    ) {
        if (!eclipticseasons$SKIPPED_CTM.isEmpty()) {
            EclipticSeasons.LOGGER.error(
                    "NeoContinuity compatibility: skipped CTM wrapping for following snowy models:"
            );
            eclipticseasons$SKIPPED_CTM.forEach(id ->
                    EclipticSeasons.LOGGER.error(" - {}", id)
            );
            EclipticSeasons.LOGGER.error(
                    "NeoContinuity wraps BlockStateModel instances into CtmBlockStateModel, "
                            + "including models without CTM behavior."
            );

            EclipticSeasons.LOGGER.error(
                    "This changes the vanilla collectParts() composition pipeline and prevents "
                            + "Ecliptic Seasons snowy parts from being collected at the same block position."
            );
            eclipticseasons$SKIPPED_CTM.clear();
        }
    }
}