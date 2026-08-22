package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxyModelBakerySubsystem;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxyModelFactory;
import com.teamtea.eclipticseasons.compat.voxy.client.VoxyTintManager;
import me.cortex.voxy.client.core.model.ModelBakerySubsystem;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.cortex.voxy.common.world.other.Mapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.LockSupport;

@Mixin({ModelBakerySubsystem.class})
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinModelBakerySubsystem implements IVoxyModelBakerySubsystem {


    @Shadow(remap = false)
    @Final
    private Mapper mapper;

    @Definition(id = "mapper", field = "Lme/cortex/voxy/client/core/model/ModelBakerySubsystem;mapper:Lme/cortex/voxy/common/world/other/Mapper;")
    @Definition(id = "getBlockStateCount", method = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateCount()I")
    @Expression("this.mapper.getBlockStateCount()<=?")
    @WrapOperation(
            remap = false,
            method = "requestBlockBake",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean eclipticseasons$requestBlockBake(int left, int right,
                                                     Operation<Boolean> original, @Local(argsOnly = true) int blockId) {
        if (VoxyTool.isVirtualIceId(blockId)) return false;
        return original.call(left,VoxyTool.fixId(mapper,right));
    }

    @Shadow
    @Final
    public ModelFactory factory;

    @Shadow
    @Final
    private Thread processingThread;

    @Override
    public void eclipticseasons$refreshTint() {
        ((IVoxyModelFactory) factory).eclipticseasons$requestTintRefresh();
        LockSupport.unpark(processingThread);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void eclipticseasons$register(Mapper mapper, CallbackInfo ci) {
        VoxyTintManager.register(this);
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void eclipticseasons$unregister(CallbackInfo ci) {
        VoxyTintManager.unregister(this);
    }
}
