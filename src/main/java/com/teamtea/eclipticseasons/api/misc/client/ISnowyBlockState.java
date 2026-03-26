package com.teamtea.eclipticseasons.api.misc.client;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;

import java.util.List;

public interface ISnowyBlockState {

    @Deprecated
    BlockStateModel getSnowyModel(int loadVersion);

    @Deprecated
    void setSnowyModel(BlockStateModel bakedModel, int loadVersion);

    @Deprecated
    BlockStateModel getSnowyModel2(int loadVersion2);

    @Deprecated
    void setSnowyModel2(BlockStateModel bakedModel, int loadVersion2);

}
