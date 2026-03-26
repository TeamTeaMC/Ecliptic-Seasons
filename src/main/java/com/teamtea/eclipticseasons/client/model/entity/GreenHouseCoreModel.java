package com.teamtea.eclipticseasons.client.model.entity;

import com.teamtea.eclipticseasons.client.render.ber.state.GreenHouseCoreState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class GreenHouseCoreModel extends Model<GreenHouseCoreState> {
    public GreenHouseCoreModel(ModelPart root, Identifier id) {
        super(root, (RenderTypes::itemTranslucent));
    }

    @Override
    public void setupAnim(GreenHouseCoreState state) {
        super.setupAnim(state);
    }
}
