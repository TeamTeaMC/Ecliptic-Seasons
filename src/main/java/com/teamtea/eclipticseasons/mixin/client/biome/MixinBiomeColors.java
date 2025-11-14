package com.teamtea.eclipticseasons.mixin.client.biome;


import com.teamtea.eclipticseasons.api.constant.solar.color.base.TemperateSolarTermColors;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

@Mixin({BiomeColors.class})
public abstract class MixinBiomeColors {

    @Inject(at = {@At("RETURN")}, method = {"getAverageGrassColor"}, cancellable = true)
    private static void eclipticseasons$getAverageGrassColor(BlockAndTintGetter pLevel,
                                                             BlockPos pBlockPos,
                                                             CallbackInfoReturnable<Integer> cir) {
        //if (pLevel != null && pBlockPos != null) {
        //    BlockPos.MutableBlockPos mutable = pBlockPos.mutable();
        //    mutable.setY(mutable.getY() + 1);
        //    //if (pLevel.getBrightness(LightLayer.SKY, mutable) < 15)
        //    //    cir.setReturnValue(TemperateSolarTermColors.AUTUMNAL_EQUINOX.getGrassColor());
        //    if (pLevel.getBrightness(LightLayer.SKY, mutable) > 0
        //            && pLevel instanceof IMapSlice mapSlice) {
        //        int solidBlockHeight = mapSlice.getSolidBlockHeight(pBlockPos);
        //        while (solidBlockHeight >= mutable.getY()) {
        //            try {
        //                BlockState blockState = ClientCon.getUseLevel().getBlockState(mutable);
        //                if (blockState.is(BlockTags.LEAVES) || blockState.is(BlockTags.LOGS)) {
        //                    cir.setReturnValue(TemperateSolarTermColors.AUTUMNAL_EQUINOX.getGrassColor());
        //                    break;
        //                }
        //            } catch (CancellationException e) {
        //                return;
        //            }
        //            mutable.set(mutable.getY() + 1);
        //        }
        //    }
        //}
    }


}
