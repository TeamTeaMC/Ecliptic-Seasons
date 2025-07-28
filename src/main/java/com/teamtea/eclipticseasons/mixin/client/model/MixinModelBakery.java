package com.teamtea.eclipticseasons.mixin.client.model;


import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Shadow
    protected abstract void registerModelAndLoadDependencies(ModelResourceLocation modelLocation, UnbakedModel model);


    @Inject(at = {@At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")}, method = {"<init>"})
    private void eclipticseasons$register_snowy_models(BlockColors blockColors,
                                                       ProfilerFiller profilerFiller,
                                                       Map<ResourceLocation, BlockModel> modelResources,
                                                       Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources,
                                                       CallbackInfo ci) {
        ExtraModelManager.registerExtraSnowyModels(this::registerModelAndLoadDependencies);
    }

    @Inject(at = {@At(value = "RETURN")}, method = {"loadBlockModel"}, cancellable = true)
    private void eclipticseasons$loadBlockModel_remapping(ResourceLocation location,
                                                CallbackInfoReturnable<BlockModel> cir) {
        BlockModel blockModel = ExtraModelManager.remappingSeasonTextures(location, cir.getReturnValue());
        if (blockModel != null) {
            cir.setReturnValue(blockModel);
        }
    }

}
