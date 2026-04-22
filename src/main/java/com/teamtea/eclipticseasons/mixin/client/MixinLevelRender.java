package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRender {

    @Shadow
    @Nullable
    public ClientLevel level;


    @Shadow
    @Final
    private static ResourceLocation RAIN_LOCATION;

    @WrapOperation(
            method = {"tickRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickRain_getBiome(LevelReader instance, BlockPos pPos, Operation<Holder<Biome>> original) {
        return instance instanceof Level clevel ?
                MapChecker.getSurfaceBiome(clevel, pPos) :
                original.call(instance, pPos);
    }

    @WrapOperation(
            method = {"renderSnowAndRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickRain_getBiome(Level instance, BlockPos pPos, Operation<Holder<Biome>> original) {
        return MapChecker.getSurfaceBiome(instance, pPos);
    }

    // ModifyExpressionValue may cost much time than it
    @WrapOperation(
            method = {"tickRain", "renderSnowAndRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$renderSnowAndRain_tickRain_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        if (level == null)
            return original.call(biome, pos);
        return EclipticUtil.getRainOrSnow(level, biome, pos);
    }


    @Inject(
            method = "renderSnowAndRain",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V")
    )
    private void eclipticseasons$renderSnowAndRain_ModifySnowAmount(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo ci, @Local(ordinal = 3) LocalIntRef integerLocalRef) {
        integerLocalRef.set(ClientWeatherChecker.ModifySnowAmount(integerLocalRef.get(), pPartialTick, level));
    }

    @WrapOperation(
            method = "tickRain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V")
    )
    private void eclipticseasons$tickRain_modifySound(ClientLevel instance, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float pVolume, float pPitch, boolean pDistanceDelay, Operation<Void> original) {
        original.call(instance, blockPos, soundEvent, soundSource, ClientWeatherChecker.modifyVolume(soundEvent, pVolume, level), ClientWeatherChecker.modifyPitch(soundEvent, pPitch, level), pDistanceDelay);
    }

    @ModifyVariable(
            method = {"tickRain"},
            at = @At("STORE"),
            ordinal = 0
    )
    private int eclipticseasons$tickRain_modifyAmount(int originalNum) {
        return (int) ClientWeatherChecker.modifyRainAmount(originalNum, level);
    }

    @WrapOperation(
            method = {"renderSnowAndRain"},
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
    )
    private void eclipticseasons$renderSnowAndRain_rebindingTexture(
            int shaderTexture, ResourceLocation textureId, Operation<Void> original) {
        original.call(shaderTexture, ClientWeatherChecker.modifyRainAmount3(textureId, textureId == RAIN_LOCATION));
    }

    // @Inject(
    //         method = "renderHitOutline",
    //         at = @At(value = "HEAD"),
    //         cancellable = true)
    // private void eclipticseasons$renderHitOutline(PoseStack pPoseStack, VertexConsumer pConsumer, Entity pEntity, double pCamX, double pCamY, double pCamZ, BlockPos pPos, BlockState pState, CallbackInfo ci) {
    //     renderShape(
    //             pPoseStack,
    //             pConsumer,
    //             pState.getShape(this.level, pPos, CollisionContext.of(pEntity)),
    //             (double)pPos.getX() - pCamX,
    //             (double)pPos.getY() - pCamY,
    //             (double)pPos.getZ() - pCamZ,
    //             0.0F,
    //             0.0F,
    //             0.0F,
    //             0.4F
    //     );
    //
    //     ci.cancel();
    // }

}
