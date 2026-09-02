package com.teamtea.eclipticseasons.compat.jei.fake;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.render.ber.XYZ;
import com.teamtea.eclipticseasons.client.util.BlockGuiRenderUtil;
import com.teamtea.eclipticseasons.compat.jei.ESJEIPlugin;
import com.teamtea.eclipticseasons.compat.jei.elements.ScaleDrawableResource;
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
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


@SuppressWarnings("removal")
public class CauldronRecipeCategory implements IRecipeCategory<CauldronRecipe> {

    private final IDrawable blankDrawable;
    private final IDrawable icon;
    private final IGuiHelper guiHelper;

    public CauldronRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        //icon = guiHelper.drawableBuilder(EclipticSeasons.rl("textures/font/seasons_icons.png"), 60, 90, 30, 30)
        //        //.trim(30,90,60,120)
        //        .setTextureSize(180, 120)
        //        .build();
        icon = new ScaleDrawableResource(SolarTerm.getFontIcon().withPrefix("textures/").withSuffix(".png"), 60, 90, 30, 30,
                180, 120, 16f / 30);

        blankDrawable = guiHelper.createBlankDrawable(128, 40);

    }

    @Override
    public @NotNull RecipeType<CauldronRecipe> getRecipeType() {
        return ESJEIPlugin.CAULDRON_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("info.eclipticseasons.recipe_category.cauldron_snow_transform");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipe recipe, IFocusGroup iFocusGroup) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addIngredients(Ingredient.of(Items.CAULDRON));
        builder.addSlot(RecipeIngredientRole.CATALYST, 86, 3)
                .addIngredients(Ingredient.of(recipe.tool()));
        builder.addOutputSlot(109, 16)
                .addIngredients(Ingredient.of(recipe.endItem()));
    }


    @Override
    public void getTooltip(ITooltipBuilder tooltip, CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.cauldron_snow_transform.condition"));
    }

    @Override
    public void draw(CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);


        guiHelper.getSlotDrawable().draw(guiGraphics, 85, 2);
        guiHelper.getSlotDrawable().draw(guiGraphics, 108, 15);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(1.2f, 0.5f, 1f);
        guiHelper.getRecipeArrow().draw(guiGraphics, 25, 12 * 3);
        guiHelper.getRecipeArrowFilled().draw(guiGraphics, 25, 12 * 3, 0, 0, 0, 21 - (int) (System.currentTimeMillis() % 6600 / 300));
        guiGraphics.pose().popPose();

        FormattedCharSequence visualOrderText = SimpleUtil.addSolarIconBefore(SolarTerm.BEGINNING_OF_WINTER, Component.empty()).getVisualOrderText();
        guiGraphics.drawString(Minecraft.getInstance().font, visualOrderText,
                34, 8, Color.WHITE.getRGB());


        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(32, 28, 0);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(16.0F, -16.0F, 16.0F);
        guiGraphics.pose().mulPose(XYZ.deg_to_rad(30, 225, 0));
        guiGraphics.pose().translate(0.5f, 0.2f, 0);
        BlockGuiRenderUtil.renderBlockInGui(guiGraphics, recipe.start());
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(56, 0f, 0);
        guiGraphics.pose().scale(16.0F, -16.0F, 16.0F);
        guiGraphics.pose().mulPose(XYZ.deg_to_rad(30, 225, 0));
        guiGraphics.pose().translate(0.5f, 0.2f, 0);
        BlockGuiRenderUtil.renderBlockInGui(guiGraphics, recipe.end());
        guiGraphics.pose().popPose();

        guiGraphics.pose().popPose();

    }

}
