package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.transformers.FullDataToRenderDataTransformer;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.teamtea.eclipticseasons.compat.distanthorizons.DHTool;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Mixin({FullDataToRenderDataTransformer.class})
public abstract class MixinFullDataToRenderDataTransformer {


    @Shadow(remap = false)
    @Final
    private static IWrapperFactory WRAPPER_FACTORY;

    @WrapOperation(
            remap = false,
            method = "setRenderColumnView",
            at = @At(value = "INVOKE", target = "Lcom/seibel/distanthorizons/core/level/IDhClientLevel;computeBaseColor(Lcom/seibel/distanthorizons/core/pos/blockPos/DhBlockPos;Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IBiomeWrapper;Lcom/seibel/distanthorizons/core/wrapperInterfaces/block/IBlockStateWrapper;)I")
    )
    private static int ecliptic$setRenderColumnView_computeBaseColor(IDhClientLevel instance,
                                                                     DhBlockPos dhBlockPos,
                                                                     IBiomeWrapper iBiomeWrapper,
                                                                     IBlockStateWrapper iBlockStateWrapper,
                                                                     Operation<Integer> original,
                                                                     @Local(argsOnly = true) FullDataPointIdMap fullDataMapping,
                                                                     @Local(argsOnly = true) LongArrayList fullColumnData) {
        MapColor mapColor = DHTool.computeBaseColor(instance, dhBlockPos, iBiomeWrapper, iBlockStateWrapper, fullDataMapping, fullColumnData, WRAPPER_FACTORY);
        if (mapColor ==MapColor.SNOW)
            // 不知道为什么，不能用这个值
            return Color.WHITE.getRGB();
        return original.call(instance, dhBlockPos, iBiomeWrapper, iBlockStateWrapper);
    }


}
