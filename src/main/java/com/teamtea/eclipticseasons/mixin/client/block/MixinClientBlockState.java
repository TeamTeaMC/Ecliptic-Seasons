package com.teamtea.eclipticseasons.mixin.client.block;


import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class MixinClientBlockState  implements ISnowyBlockState {

    @Unique
    public BakedModel eclipticSeasons$cacheSnowyBakedModel = null;

    @Unique
    public int eclipticSeasons$loadVersion = ModelManager.loadVersion;

    @Override
    public BakedModel getSnowyModel(int loadVersion) {
        if (loadVersion != eclipticSeasons$loadVersion) {
            eclipticSeasons$cacheSnowyBakedModel = null;
        }
        return eclipticSeasons$cacheSnowyBakedModel;
    }

    @Override
    public void setSnowyModel(BakedModel bakedModel,int loadVersion) {
        this.eclipticSeasons$cacheSnowyBakedModel = bakedModel;
        this.eclipticSeasons$loadVersion=loadVersion;
    }
}
