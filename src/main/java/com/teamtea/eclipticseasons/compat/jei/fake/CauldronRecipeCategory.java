package com.teamtea.eclipticseasons.compat.jei.fake;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
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
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class CauldronRecipeCategory implements IRecipeCategory<CauldronRecipe> {

    private final IDrawable blankDrawable;
    private final IDrawable icon;
    private final IGuiHelper guiHelper;

    public CauldronRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        // icon = guiHelper.drawableBuilder(EclipticSeasons.rl("textures/font/seasons_icons.png"), 60, 90, 30, 30)
        //        //.trim(30,90,60,120)
        //        .setTextureSize(180, 120)
        //        .build();
        icon = new ScaleDrawableResource(SolarTerm.getFontIcon().withPrefix("textures/").withSuffix(".png"), 60, 90, 30, 30,
                180, 120, 16f / 30);

        blankDrawable = guiHelper.createBlankDrawable(128, 40);

    }

    @Override
    public @NotNull IRecipeType<CauldronRecipe> getRecipeType() {
        return ESJEIPlugin.CAULDRON_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("info.eclipticseasons.recipe_category.cauldron_snow_transform");
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 40;
    }

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
                .add(Items.CAULDRON);
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 86, 3)
                .add(new SlotDisplay.TagSlotDisplay(recipe.tool()));
        builder.addOutputSlot(109, 16)
                .add(recipe.endItem());
    }


    @Override
    public void getTooltip(ITooltipBuilder tooltip, CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.cauldron_snow_transform.condition"));
    }

    @Override
    public void draw(CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        int startX = (int) (guiGraphics.pose().m20());
        int startY = (int) (guiGraphics.pose().m21());
        
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);


        guiHelper.getSlotDrawable().draw(guiGraphics, 85, 2);
        guiHelper.getSlotDrawable().draw(guiGraphics, 108, 15);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(1.2f, 0.5f);
        guiHelper.getRecipeArrow().draw(guiGraphics, 25, 12 * 3);
        if (guiHelper.getRecipeArrowFilled() instanceof IDrawableStatic ds) {
            ds.draw(guiGraphics, 25, 12 * 3, 0, 0, 0, 21 - (int) (System.currentTimeMillis() % 6600 / 300));
        }
        guiGraphics.pose().popMatrix();

        FormattedCharSequence visualOrderText = SimpleUtil.addSolarIconBefore(SolarTerm.BEGINNING_OF_WINTER, Component.empty()).getVisualOrderText();
        guiGraphics.text(Minecraft.getInstance().font, visualOrderText,
                34, 8, Color.WHITE.getRGB());



        BlockGuiRenderUtil.renderBlockInGui(guiGraphics, recipe.start(),
                (int)startX-2,  (int)startY+6);
        BlockGuiRenderUtil.renderBlockInGui(guiGraphics, recipe.end(),
                (int)startX+54,  (int)startY+6);

    }

}
