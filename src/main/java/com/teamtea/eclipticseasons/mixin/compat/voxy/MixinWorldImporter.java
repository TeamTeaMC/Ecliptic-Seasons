package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.compat.voxy.VoxyImporterTool;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import com.teamtea.eclipticseasons.compat.voxy.helper.VoxyPairCompoundTag;
import me.cortex.voxy.common.voxelization.ILightingSupplier;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.world.other.Mapper;
import me.cortex.voxy.commonImpl.importers.WorldImporter;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldImporter.class})
public class MixinWorldImporter {

    //@Unique
    //private static CompoundTag eclipticseasons$above = null;

    @Shadow(remap = false)
    @Final
    private Codec<PalettedContainer<BlockState>> blockStateCodec;

    @Shadow(remap = false)
    @Final
    private PalettedContainerRO<Holder<Biome>> defaultBiomeProvider;

    @Shadow(remap = false)
    @Final
    private Codec<PalettedContainerRO<Holder<Biome>>> biomeCodec;


    @WrapOperation(
            remap = false,
            method = "importChunkNBT",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/commonImpl/importers/WorldImporter;importSectionNBT(IIILnet/minecraft/nbt/CompoundTag;)V")
    )
    private void eclipticseasons$importChunkNBT(
            WorldImporter instance,
            int x, int y, int z,
            CompoundTag compoundTag, Operation<Void> original,
            @Local(argsOnly = true) CompoundTag chunk) {
        original.call(instance, x, y, z, VoxyImporterTool.getVoxyPairCompoundTag(y, compoundTag, chunk));
    }

    @Inject(
            remap = false,
            method = "importSectionNBT",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$importSectionNBT_init(
            int x, int y, int z, CompoundTag section, CallbackInfo ci,
            @Local(argsOnly = true) LocalRef<CompoundTag> sectionRef,
            @Share("above") LocalRef<CompoundTag> aboveRef) {
        if (VoxyTool.isVoxyTest() && section instanceof VoxyPairCompoundTag voxyPairCompoundTag) {
            sectionRef.set(voxyPairCompoundTag.getOriginal());
            aboveRef.set(voxyPairCompoundTag.getAbove());
        }
    }


    @WrapOperation(
            remap = false,
            method = "importSectionNBT",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/voxelization/WorldConversionFactory;convert(Lme/cortex/voxy/common/voxelization/VoxelizedSection;Lme/cortex/voxy/common/world/other/Mapper;Lnet/minecraft/world/level/chunk/PalettedContainer;Lnet/minecraft/world/level/chunk/PalettedContainerRO;Lme/cortex/voxy/common/voxelization/ILightingSupplier;)Lme/cortex/voxy/common/voxelization/VoxelizedSection;")
    )
    private VoxelizedSection eclipticseasons$importSectionNBT_set(
            VoxelizedSection section,
            Mapper stateMapper,
            PalettedContainer<BlockState> blockContainer,
            PalettedContainerRO<Holder<Biome>> biomeContainer,
            ILightingSupplier lightSupplier,
            Operation<VoxelizedSection> original,
            @Share("above") LocalRef<CompoundTag> aboveRef) {
        lightSupplier = VoxyImporterTool.getILightingSupplier(
                blockStateCodec, biomeCodec, defaultBiomeProvider,
                blockContainer, biomeContainer, lightSupplier, aboveRef.get());
        return original.call(section, stateMapper, blockContainer, biomeContainer, lightSupplier);
    }

}
