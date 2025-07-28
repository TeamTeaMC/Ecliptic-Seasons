package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerLightSectionStorage.class})
public abstract class MixinClientLayerLightSectionStorage {

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
        if (!FMLEnvironment.production && layer == LightLayer.BLOCK && chunkSource.getLevel() instanceof ClientLevel clientLevel) {
            if (pLightLevel == 0) {
                int old = datalayer.get(SectionPos.sectionRelative(BlockPos.getX(pLevelPos)), SectionPos.sectionRelative(BlockPos.getY(pLevelPos)), SectionPos.sectionRelative(BlockPos.getZ(pLevelPos)));
                ClientMapFixer.addLightPlanner(clientLevel, pLevelPos, old);
            }
        }
    }
}
