package com.teamtea.eclipticseasons.mixin.client.block;


import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class MixinClientBlockState  implements ISnowyBlockState {

    @Unique
    public BakedModel eclipticseasons$cacheSnowyBakedModel = null;

    @Unique
    public BakedModel eclipticseasons$cacheSnowyBakedModel2 = null;

    @Unique
    public int eclipticseasons$loadVersion = ModelManager.loadVersion;

    @Unique
    public int eclipticseasons$loadVersion2 = ModelManager.loadVersion;

    @Override
    public BakedModel getSnowyModel(int loadVersion) {
        if (loadVersion != eclipticseasons$loadVersion) {
            eclipticseasons$cacheSnowyBakedModel = null;
        }
        return eclipticseasons$cacheSnowyBakedModel;
    }

    @Override
    public void setSnowyModel(BakedModel bakedModel,int loadVersion) {
        this.eclipticseasons$cacheSnowyBakedModel = bakedModel;
        this.eclipticseasons$loadVersion=loadVersion;
    }

    @Override
    public BakedModel getSnowyModel2(int loadVersion) {
        if (loadVersion != eclipticseasons$loadVersion2) {
            eclipticseasons$cacheSnowyBakedModel2 = null;
        }
        return eclipticseasons$cacheSnowyBakedModel2;
    }

    @Override
    public void setSnowyModel2(BakedModel bakedModel,int loadVersion) {
        this.eclipticseasons$cacheSnowyBakedModel2 = bakedModel;
        this.eclipticseasons$loadVersion2=loadVersion;
    }

}
