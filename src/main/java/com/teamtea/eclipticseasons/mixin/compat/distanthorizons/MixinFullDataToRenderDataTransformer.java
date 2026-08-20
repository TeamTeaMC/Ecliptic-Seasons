package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dataObjects.transformers.FullDataToRenderDataTransformer;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.common.mixin.expression.DirectExpression;
import com.teamtea.eclipticseasons.compat.distanthorizons.DHTool;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Pseudo
@Mixin({FullDataToRenderDataTransformer.class})
@ConditionalMixin(value = "distanthorizons", version = "3.0.0-b")
public abstract class MixinFullDataToRenderDataTransformer {


    @Shadow(remap = false)
    @Final
    private static IWrapperFactory WRAPPER_FACTORY;


    // @WrapOperation(
    //         remap = false,
    //         require = 0,
    //         method = "setRenderColumnView",
    //         at = @At(value = "INVOKE", target = "Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IClientLevelWrapper;getBlockColor(Lcom/seibel/distanthorizons/core/pos/blockPos/DhBlockPos;Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IBiomeWrapper;Lcom/seibel/distanthorizons/core/dataObjects/fullData/sources/FullDataSourceV2;Lcom/seibel/distanthorizons/core/wrapperInterfaces/block/IBlockStateWrapper;)I")
    // )
    // private static int eclipticseasons$setRenderColumnView_computeBaseColor(IClientLevelWrapper instance,
    //                                                                         DhBlockPos dhBlockPos,
    //                                                                         IBiomeWrapper iBiomeWrapper,
    //                                                                         FullDataSourceV2 fullDataSourceV2,
    //                                                                         IBlockStateWrapper iBlockStateWrapper,
    //                                                                         Operation<Integer> original,
    //                                                                         @Local FullDataPointIdMap fullDataMapping,
    //                                                                         @Local(argsOnly = true) LongArrayList fullColumnData,
    //                                                                         @Local(name = "skyLight") LocalIntRef localIntRef) {
    //     MapColor mapColor = DHTool.computeBaseColor(instance, dhBlockPos, iBiomeWrapper, iBlockStateWrapper, fullDataMapping, fullColumnData, WRAPPER_FACTORY, localIntRef.get());
    //     if (mapColor == MapColor.SNOW)
    //         // 不知道为什么，不能用这个值
    //         return Color.WHITE.getRGB();
    //     return original.call(instance, dhBlockPos, iBiomeWrapper, fullDataSourceV2, iBlockStateWrapper);
    // }

    @DirectExpression(
            remap = false,
            method = "setRenderColumnView",
            at = {@At(
                    ordinal = 1,
                    value = "INVOKE",
                    target = "Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IClientLevelWrapper;getBlockColor(Lcom/seibel/distanthorizons/core/pos/blockPos/DhBlockPos;Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IBiomeWrapper;Lcom/seibel/distanthorizons/core/dataObjects/fullData/sources/FullDataSourceV2;Lcom/seibel/distanthorizons/core/wrapperInterfaces/block/IBlockStateWrapper;)I"
            ), @At(
                    ordinal = 2,
                    value = "INVOKE",
                    target = "Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IClientLevelWrapper;getBlockColor(Lcom/seibel/distanthorizons/core/pos/blockPos/DhBlockPos;Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IBiomeWrapper;Lcom/seibel/distanthorizons/core/dataObjects/fullData/sources/FullDataSourceV2;Lcom/seibel/distanthorizons/core/wrapperInterfaces/block/IBlockStateWrapper;)I"
            )},
            handler = "Lcom/teamtea/eclipticseasons/compat/distanthorizons/DHTool;applySnowColor(Ljava/lang/Integer;)I",
            require = 0
    )
    private static Integer setRenderColumnView_computeBaseColor(
            IClientLevelWrapper instance,
            DhBlockPos dhBlockPos,
            IBiomeWrapper iBiomeWrapper,
            FullDataSourceV2 fullDataSourceV2,
            IBlockStateWrapper iBlockStateWrapper,
            // Operation<Integer> original,
            @Local FullDataPointIdMap fullDataMapping,
            @Local(argsOnly = true) LongArrayList fullColumnData,
            @Local(name = "skyLight") int skyLight,
            @Local(name = "fullDataIndex") int fullDataIndex,
            @Local(name = "isSnowLayer") boolean isSnowLayer
    ) {
        return isSnowLayer ? null : DHTool.computeBaseColor(instance, dhBlockPos, iBiomeWrapper, iBlockStateWrapper, fullDataMapping, fullColumnData, WRAPPER_FACTORY, skyLight, fullDataSourceV2, fullDataIndex);
    }

    @DirectExpression(
            remap = false,
            method = "setRenderColumnView",
            at = {@At(
                    ordinal = 3,
                    value = "INVOKE",
                    target = "Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IClientLevelWrapper;getBlockColor(Lcom/seibel/distanthorizons/core/pos/blockPos/DhBlockPos;Lcom/seibel/distanthorizons/core/wrapperInterfaces/world/IBiomeWrapper;Lcom/seibel/distanthorizons/core/dataObjects/fullData/sources/FullDataSourceV2;Lcom/seibel/distanthorizons/core/wrapperInterfaces/block/IBlockStateWrapper;)I"
            )},
            handler = "Lcom/teamtea/eclipticseasons/compat/distanthorizons/DHTool;applySnowColor(Ljava/lang/Integer;)I",
            require = 0
    )
    private static Integer setRenderColumnView_computeBaseColor_3(
            IClientLevelWrapper instance,
            DhBlockPos dhBlockPos,
            IBiomeWrapper iBiomeWrapper,
            FullDataSourceV2 fullDataSourceV2,
            IBlockStateWrapper iBlockStateWrapper,
            // Operation<Integer> original,
            @Local FullDataPointIdMap fullDataMapping,
            @Local(argsOnly = true) LongArrayList fullColumnData,
            @Local(name = "skyLight") int skyLight,
            @Local(name = "fullDataIndex") int fullDataIndex
    ) {
        return DHTool.computeBaseColor(instance, dhBlockPos, iBiomeWrapper, iBlockStateWrapper, fullDataMapping, fullColumnData, WRAPPER_FACTORY, skyLight, fullDataSourceV2, fullDataIndex);
    }

    // @Unique


    @ModifyExpressionValue(
            remap = false,
            require = 0,
            method = "setRenderColumnView",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/seibel/distanthorizons/core/dataObjects/fullData/FullDataPointIdMap;getBlockStateWrapper(I)Lcom/seibel/distanthorizons/core/wrapperInterfaces/block/IBlockStateWrapper;"
            )
    )
    private static IBlockStateWrapper eclipticseasons$setRenderColumnView_fixIce(
            IBlockStateWrapper original,
            @Local(argsOnly = true) IClientLevelWrapper clientLevel,
            @Local FullDataPointIdMap fullDataMapping,
            @Local(argsOnly = true) LongArrayList fullColumnData,
            @Local DhBlockPosMutable dhBlockPosMutable,
            @Local IBiomeWrapper biomeWrapper,
            @Local(name = "fullDataIndex") int fullDataIndex
    ) {
        if (original.isLiquid()
                && original.getWrappedMcObject() instanceof BlockState blockState
                && clientLevel instanceof ClientLevelWrapper clientLevelWrapper) {
            IBlockStateWrapper frozen = DHTool.shouldFrozen(
                    clientLevelWrapper,
                    biomeWrapper,
                    dhBlockPosMutable,
                    blockState,
                    fullDataMapping,
                    fullColumnData,
                    fullDataIndex
            );

            if (frozen != null) {
                return frozen;
            }
        }

        return original;
    }

}
