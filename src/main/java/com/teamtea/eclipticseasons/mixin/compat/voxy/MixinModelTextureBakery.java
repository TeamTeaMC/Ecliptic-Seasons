package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.data.client.model.ModelTester;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.lod.SeasonalModelEntry;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.client.VoxyClientTool;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyModelController;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({SoftwareModelTextureBakery.class})
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinModelTextureBakery implements IVoxyModelController {

    @Shadow
    @Final
    private ReuseVertexConsumer translucentVC;

    @Shadow
    @Final
    private ReuseVertexConsumer opaqueVC;

    @WrapOperation(
            method = "bakeBlockModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;collectParts(Lnet/minecraft/util/RandomSource;Ljava/util/List;)V")
    )
    private void eclipticseasons$collectSeasonalParts(
            BlockStateModel originalModel,
            RandomSource random,
            List<BlockStateModelPart> parts,
            Operation<Void> original,
            @Local(argsOnly = true) BlockState state
    ) {
        SeasonalModelEntry entry = getSeasonalModel();

        if (entry == null) {
            original.call(originalModel, random, parts);
            return;
        }

        ModelTester modelTester = ExtraModelManager.getSeasonalModel(state, entry.modelIdentifier());

        BlockStateModel seasonalModel = modelTester == null ? null
                : ExtraModelManager.getExtraModel(modelTester.modelIdentifier());

        // if not found model
        if (seasonalModel == null) {
            original.call(originalModel, random, parts);
            return;
        }

        if (!modelTester.replace()) {
            original.call(originalModel, random, parts);
        }

        seasonalModel.collectParts(random, parts);
    }

    @Inject(
            remap = false,
            method = "bakeBlockModel",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$bakeBlockModel_pre(BlockState state, CallbackInfo ci) {
        if (isSnowyBlock())
            VoxyClientTool.renderToStream(state, translucentVC, opaqueVC);
    }


    @Unique
    boolean eclipticseasons$snowyBlock = false;

    @Override
    public void setSnowyBlock(boolean snowyBlock) {
        this.eclipticseasons$snowyBlock = snowyBlock;
    }

    @Override
    public boolean isSnowyBlock() {
        return eclipticseasons$snowyBlock;
    }

    @Unique
    @Nullable
    private SeasonalModelEntry eclipticseasons$seasonalModel;

    @Override
    public void setSeasonalModel(@Nullable SeasonalModelEntry seasonalModel) {
        this.eclipticseasons$seasonalModel = seasonalModel;
    }

    @Override
    public @Nullable SeasonalModelEntry getSeasonalModel() {
        return eclipticseasons$seasonalModel;
    }
}
