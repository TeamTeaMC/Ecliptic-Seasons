package com.teamtea.eclipticseasons.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

public class SnowyBakedModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> {

    private int bindBlockType = -1;

    public SnowyBakedModelWrapper(T originalModel) {
        super(originalModel);
    }


    public void updateBlockType(int bindBlockType) {
        this.bindBlockType = bindBlockType;
    }

    public int getBindBlockType() {
        return this.bindBlockType;
    }

    public static boolean isInvalid(SnowyBakedModelWrapper<?> snowyBakedModelWrapper) {
        return snowyBakedModelWrapper.getBindBlockType() <0;
    }
}
