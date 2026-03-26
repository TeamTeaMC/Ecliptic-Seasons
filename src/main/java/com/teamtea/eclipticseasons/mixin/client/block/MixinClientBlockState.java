package com.teamtea.eclipticseasons.mixin.client.block;


import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(BlockState.class)
public abstract class MixinClientBlockState implements ISnowyBlockState {

    @Unique
    public BlockStateModel eclipticseasons$cacheSnowyBakedModel = null;

    @Unique
    public BlockStateModel eclipticseasons$cacheSnowyBakedModel2 = null;

    @Unique
    public int eclipticseasons$loadVersion = ExtraModelManager.loadVersion;

    @Unique
    public int eclipticseasons$loadVersion2 = ExtraModelManager.loadVersion;

    @Override
    public BlockStateModel getSnowyModel(int loadVersion) {
        if (loadVersion != eclipticseasons$loadVersion) {
            eclipticseasons$cacheSnowyBakedModel = null;
        }
        return eclipticseasons$cacheSnowyBakedModel;
    }

    @Override
    public void setSnowyModel(BlockStateModel bakedModel, int loadVersion) {
        this.eclipticseasons$cacheSnowyBakedModel = bakedModel;
        this.eclipticseasons$loadVersion = loadVersion;
    }

    @Override
    public BlockStateModel getSnowyModel2(int loadVersion) {
        if (loadVersion != eclipticseasons$loadVersion2) {
            eclipticseasons$cacheSnowyBakedModel2 = null;
        }
        return eclipticseasons$cacheSnowyBakedModel2;
    }

    @Override
    public void setSnowyModel2(BlockStateModel bakedModel, int loadVersion) {
        this.eclipticseasons$cacheSnowyBakedModel2 = bakedModel;
        this.eclipticseasons$loadVersion2 = loadVersion;
    }

}
