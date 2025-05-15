package com.teamtea.eclipticseasons.client.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

public class SnowyBakedModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> implements ISnowyReplaceModel{

    private int bindBlockType = -1;

    @Setter
    @Getter
    private boolean replace=false;

    @Setter
    @Getter
    private boolean lowLayer =false;

    public SnowyBakedModelWrapper(T originalModel) {
        super(originalModel);
    }


    @Override
    public void updateBlockType(int bindBlockType) {
        this.bindBlockType = bindBlockType;
    }

    @Override
    public int getBindBlockType() {
        return this.bindBlockType;
    }

}
