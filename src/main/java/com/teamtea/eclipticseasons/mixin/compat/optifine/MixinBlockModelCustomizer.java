package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Pseudo
@Mixin(targets = "net.optifine.model.BlockModelCustomizer", remap = false)
public abstract class MixinBlockModelCustomizer {


    @Unique
    private static Random eclipticseasons$randomSource = new Random();

    // 这里不知道要不要ordinal=1
    // 但是opt这里要处理的是那个jar文件得移动移动一下，不能直接用
    // opt 似乎无法使用
    // IdentityHashMap似乎不适合Opt
    @Inject(at = {@At(value = "RETURN"),}, remap = false, method = {"getRenderQuads(Ljava/util/List;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/RenderType;JLnet/optifine/render/RenderEnv;)Ljava/util/List;"}, cancellable = true)
    private static void eclipticseasons$getRenderQuads(List<BakedQuad> quads, BlockAndTintGetter worldIn, BlockState stateIn, BlockPos posIn, Direction enumfacing, RenderType layer, long rand, net.optifine.render.RenderEnv renderEnv, CallbackInfoReturnable<List<BakedQuad>> cir) {
        List<BakedQuad> bakedQuadList = cir.getReturnValue();
        if (!bakedQuadList.isEmpty() && Minecraft.getInstance().level != null) {
            eclipticseasons$randomSource.setSeed(rand);
            cir.setReturnValue(ModelManager.appendOverlay(worldIn, stateIn, posIn, enumfacing, eclipticseasons$randomSource, rand, bakedQuadList));
        }
    }


}
