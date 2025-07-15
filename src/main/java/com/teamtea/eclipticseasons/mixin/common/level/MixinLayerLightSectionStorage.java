package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerLightSectionStorage.class})
public abstract class MixinLayerLightSectionStorage {

    @Shadow
    @Final
    protected LightChunkGetter chunkSource;

    @Shadow
    @Final
    private LightLayer layer;

    @Inject(
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/DataLayer;set(IIII)V"),
            method = "setStoredLevel"
    )
    public void eclipticseasons$setStoredLevel_trackLightDecrease(long pLevelPos, int pLightLevel, CallbackInfo ci, @Local DataLayer datalayer) {
        if (layer == LightLayer.BLOCK && chunkSource.getLevel() instanceof ServerLevel serverLevel) {
            if (pLightLevel == 0) {
                int old = datalayer.get(SectionPos.sectionRelative(BlockPos.getX(pLevelPos)), SectionPos.sectionRelative(BlockPos.getY(pLevelPos)), SectionPos.sectionRelative(BlockPos.getZ(pLevelPos)));
                ServerMapFixer.addLightPlanner(serverLevel, pLevelPos, old);
            }
        }
    }
}
