package com.teamtea.eclipticseasons.compat.iui_forge;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.render.ber.XYZ;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.anningui.iui_forge.render.Element;

import java.util.List;

public class SnowyBlockView extends Element {
    @Override
    public void onRender(GuiGraphics guiGraphics, int mx, int my, float delta) {
        super.onRender(guiGraphics, mx, my, delta);
        BlockState blockState = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        BakedModel bakedmodel = blockRenderer.getBlockModel(blockState);
        guiGraphics.pose().pushPose();
        try {
            guiGraphics.pose().translate(
                    getPosX() + width / 2d,
                    getPosY() + height / 1.5d,
                    0d);
            guiGraphics.pose().scale(16.0F, -16.0F, 16.0F);
            guiGraphics.pose().mulPose(XYZ.deg_to_rad(30, 225, 0));

            int i = mc.getBlockColors().getColor(blockState, null, null, 0);
            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;
            for (BakedModel bakedModel : List.of(bakedmodel, ExtraModelManager.models.get(ExtraModelManager.snowOverlayBlock))) {
                for (net.minecraft.client.renderer.RenderType rt : bakedModel.getRenderTypes(blockState, RandomSource.create(42), ModelData.EMPTY))
                    blockRenderer.getModelRenderer()
                            .renderModel(
                                    guiGraphics.pose().last(),
                                    guiGraphics.bufferSource().getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)),
                                    blockState, bakedModel, f, f1, f2, LightTexture.FULL_SKY, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, rt);

            }
            //blockRenderer.renderSingleBlock(blockState,
            //        guiGraphics.pose(),
            //        guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            //mc.getItemRenderer()
            //        .render(Items.APPLE.getDefaultInstance(),
            //                ItemDisplayContext.GUI,false,guiGraphics.pose(),
            //                guiGraphics.bufferSource(),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,bakedmodel);
        } catch (Throwable throwable) {
            EclipticSeasons.logger(throwable);
        }
    }


}
