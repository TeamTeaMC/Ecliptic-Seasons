package com.teamtea.eclipticseasons.compat.jei;


import com.mojang.blaze3d.platform.Lighting;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.client.render.ber.XYZ;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@SuppressWarnings("removal")
public class JEIHumidityControlCategory implements IRecipeCategory<HumidityControl> {
    private final IDrawable icon;
    private final IGuiHelper guiHelper;
    private IDrawableStatic blankDrawable;

    public JEIHumidityControlCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        icon = guiHelper.createDrawableItemStack(ItemRegistry.block_in_wooden_grate_block_item.get().getDefaultInstance());
        blankDrawable = guiHelper.createBlankDrawable(128, 70);
    }


    @Override
    public @NotNull RecipeType<HumidityControl> getRecipeType() {
        return ESJEIPlugin.HUMIDITY_CONTROL_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("info.eclipticseasons.humidity_control");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return blankDrawable;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HumidityControl recipe, @NotNull IFocusGroup ingredients) {

        builder.addSlot(RecipeIngredientRole.INPUT, 16, 8)
                .addIngredients(Ingredient.of(recipe.ingredient().getItems()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 40)
                .addIngredients(Ingredient.of(recipe.result()));

    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, HumidityControl recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        if (mouseX > 32 && mouseX < 64
                && mouseY > 4 && mouseY < 60)
            tooltip.addAll(List.of(Component.translatable("持续时间：" + recipe.lasting_time()/20),
                    Component.translatable("湿度变化：" + recipe.level())));
    }

    @Override
    public void draw(HumidityControl recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiHelper.getSlotDrawable().draw(guiGraphics, 15, 7);
        guiHelper.getSlotDrawable().draw(guiGraphics, 99, 39);


        // guiGraphics.pose().pushPose();
        // guiGraphics.pose().translate(32, 20, 0);
        // guiGraphics.pose().mulPose(XYZ.deg_to_rad(0, 0, 90));
        // Internal.getTextures().getRecipeArrow().draw(guiGraphics, 0, 0);
        // guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(40 + 1, 3, 0);
        guiGraphics.pose().scale(2f, 2f, 2f);


        // guiGraphics.pose().pushPose();
        // guiGraphics.pose().scale(.75f, .75f, .75f);
        // guiGraphics.pose().translate(3, 3, 0);
        // guiGraphics.renderItem(recipe.ingredient().getItems()[0], 0, 0);
        // guiGraphics.pose().popPose();
        int currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 2000;
        for (PosAndBlockStateCheck check : recipe.checks()) {
            if (check.block().size() > 0) {
                int index = currentTimeMillis % check.block().size();
                Holder<Block> blockHolder = check.block().get(index);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(16 * check.offset().getX(), -16 * check.offset().getY(), 16 * check.offset().getZ());
                float x = 7f;
                float y = -3f + 2;
                int guiOffset = 0;
                if (blockHolder.isBound()) {
                    BlockState blockState = blockHolder.value().defaultBlockState();
                    BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = blockRenderer.getBlockModel(blockState);
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate((float) (x + 8), (float) (y + 8), (float) (150 + (bakedmodel.isGui3d() ? guiOffset : 0)));

                    try {
                        guiGraphics.pose().scale(16.0F, -16.0F, 16.0F);

                        guiGraphics.pose().mulPose(XYZ.deg_to_rad(30, 225, 0));
                        guiGraphics.pose().scale(0.625f, 0.625f, 0.625f);

                        // Lighting.setupLevel(guiGraphics.pose().last().pose());
                        blockRenderer.renderSingleBlock(blockState,
                                guiGraphics.pose(),
                                guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                        guiGraphics.flush();

                    } catch (Throwable throwable) {
                        EclipticSeasons.logger(throwable);
                    }

                    guiGraphics.pose().popPose();
                }
                guiGraphics.pose().popPose();
            }
        }

        currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 1200;
        guiGraphics.pose().translate(0, 0, 20);
        guiGraphics.renderItem(ItemRegistry.block_in_wooden_grate_block_item.get().getDefaultInstance(), 0, 0);

        guiGraphics.pose().popPose();

        // guiGraphics.renderTooltip(
        //         Minecraft.getInstance().font,
        //         List.of(Component.translatable("" + recipe.lasting_time()),
        //                 Component.translatable("" + recipe.level())),
        //         Optional.empty(),
        //         (int) mouseX, (int) mouseY
        // );
    }
}
