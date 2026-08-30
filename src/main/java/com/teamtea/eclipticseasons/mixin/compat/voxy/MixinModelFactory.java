package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxyModelFactory;
import com.teamtea.eclipticseasons.compat.voxy.client.VoxyClientTool;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyModelController;
import me.cortex.voxy.client.core.gl.GlBuffer;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.cortex.voxy.client.core.model.ModelStore;
import me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery;
import me.cortex.voxy.client.core.rendering.util.UploadStream;
import me.cortex.voxy.common.util.MemoryBuffer;
import me.cortex.voxy.common.util.Pair;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin({ModelFactory.class})
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinModelFactory implements IVoxyModelFactory {

    @Shadow
    @Final
    public SoftwareModelTextureBakery bakery2;

    @Shadow(remap = false)
    @Final
    private Mapper mapper;

    @WrapOperation(
            remap = false,
            method = "addEntry",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateFromBlockId(I)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState eclipticseasons$addEntry_setBS(Mapper instance, int blockId, Operation<BlockState> original) {
        return original.call(instance, VoxyTool.fixId(instance, blockId
                //         , (i) -> {
                //     // if (bakery2 instanceof IVoxyModelController modelController) {
                //     //     modelController.setSnowyBlock(true);
                //     // }
                // }
        ));
    }

    @Inject(
            remap = false,
            method = "addEntry",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$addEntry_clean(
            int blockId, CallbackInfoReturnable<Boolean> cir) {
        // if (bakery2 instanceof IVoxyModelController modelController) {
        //     modelController.setSnowyBlock(false);
        // }
    }

    @ModifyReturnValue(
            remap = false,
            method = "isBiomeDependentColour",
            at = @At("RETURN")
    )
    private static boolean eclipticseasons$markSeasonalFoliageDynamic(boolean original, List<BlockTintSource> tintSources, BlockState state) {
        return original | VoxyClientTool.isExtendBiomeDependentColour(state, tintSources);
    }

    @Inject(
            remap = false,
            method = "processModelResult",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$processModelResult_return(CallbackInfoReturnable<Boolean> cir) {
        if (bakery2 instanceof IVoxyModelController modelController) {
            modelController.setSnowyBlock(false);
            modelController.setSeasonalModel(null);
        }
    }

    @ModifyExpressionValue(
            remap = false,
            method = "processModelResult",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentLinkedDeque;poll()Ljava/lang/Object;")
    )
    private <E> E eclipticseasons$processModelResult_setBS(E original, @Share("isSnowyBlock") LocalBooleanRef ref) {
        if (original != null) {
            try {
                Method m = original.getClass().getDeclaredMethod("blockId");
                m.setAccessible(true);
                int blockId = (int) m.invoke(original);
                VoxyTool.fixId(mapper, blockId, (i) -> {
                    if (bakery2 instanceof IVoxyModelController modelController) {
                        modelController.setSnowyBlock(true);
                    }
                }, (seasonal) -> {
                    if (bakery2 instanceof IVoxyModelController modelController) {
                        modelController.setSeasonalModel(seasonal);
                        modelController.setSnowyBlock(seasonal.snowy());
                    }
                });
            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }
        }

        return original;
    }


    @Shadow
    @Final
    private List<Biome> biomes;

    @Shadow
    @Final
    private List<Pair<Integer, BlockState>> modelsRequiringBiomeColours;

    @Shadow
    @Final
    private ModelStore storage;

    @Unique
    private volatile boolean eclipticseasons$refreshTintRequested;

    @Unique
    private final AtomicReference<MemoryBuffer> eclipticseasons$pendingTintUpload =
            new AtomicReference<>();

    @Override
    public void eclipticseasons$requestTintRefresh() {
        eclipticseasons$refreshTintRequested = true;
    }

    /*
     * processAllThings() 在 ModelFactory 的后台处理线程执行。
     *
     * 在这里读取 biomes 和 modelsRequiringBiomeColours，
     * 避免从渲染线程并发遍历这两个 ArrayList。
     */

    @ModifyExpressionValue(
            remap = false,
            method = "processAllThings",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/cortex/voxy/client/core/model/ModelFactory;processModelResult()Z"
            )
    )
    private boolean eclipticseasons$rebuildTintTable(boolean hasMoreModels) {
        if (!hasMoreModels) {
            eclipticseasons$rebuildTintTable$Impl();
        }
        return hasMoreModels;
    }

    @Unique
    private void eclipticseasons$rebuildTintTable$Impl() {
        if (!eclipticseasons$refreshTintRequested) return;
        eclipticseasons$refreshTintRequested = false;

        int biomeCount = biomes.size();
        int modelCount = modelsRequiringBiomeColours.size();

        if (biomeCount == 0 || modelCount == 0) return;

        MemoryBuffer upload = new MemoryBuffer((long) biomeCount * modelCount * Integer.BYTES);
        long pointer = upload.address;

        for (Pair<Integer, BlockState> entry : modelsRequiringBiomeColours) {
            BlockState state = entry.right();
            List<BlockTintSource> tintSources =
                    ModelFactoryInvoker.eclipticseasons$getTintSources(state);

            for (Biome biome : biomes) {
                int colour = 0xFFFFFFFF;

                if (biome != null && tintSources != null) {
                    colour = ModelFactoryInvoker.eclipticseasons$captureColourConstant(
                            tintSources, state, biome
                    ) | 0xFF000000;
                }

                MemoryUtil.memPutInt(pointer, colour);
                pointer += Integer.BYTES;
            }
        }

        MemoryBuffer previous = eclipticseasons$pendingTintUpload.getAndSet(upload);
        if (previous != null && !previous.isFreed()) {
            previous.free();
        }
    }

    /*
     * processUploads() 由渲染线程调用，因此 OpenGL 上传放在这里。
     */
    @Inject(method = "processUploads", at = @At("HEAD"))
    private void eclipticseasons$uploadTintTable(CallbackInfo ci) {
        MemoryBuffer upload = eclipticseasons$pendingTintUpload.getAndSet(null);
        if (upload == null) return;

        ModelStoreAccessor accessor = (ModelStoreAccessor) storage;
        GlBuffer colourBuffer = accessor.eclipticseasons$getModelColourBuffer();

        upload.cpyTo(UploadStream.INSTANCE.upload(colourBuffer, 0, upload.size));
        UploadStream.INSTANCE.commit();
        upload.free();
    }

    @Inject(method = "free", at = @At("HEAD"))
    private void eclipticseasons$freePendingTintUpload(CallbackInfo ci) {
        MemoryBuffer upload = eclipticseasons$pendingTintUpload.getAndSet(null);

        if (upload != null && !upload.isFreed()) {
            upload.free();
        }
    }
}
