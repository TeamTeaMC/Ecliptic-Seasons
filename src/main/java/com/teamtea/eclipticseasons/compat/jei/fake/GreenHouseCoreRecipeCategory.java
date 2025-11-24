package com.teamtea.eclipticseasons.compat.jei.fake;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.compat.jei.ESJEIPlugin;
import com.teamtea.eclipticseasons.config.CommonConfig;
import icyllis.arc3d.core.Color;
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
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("removal")
public class GreenHouseCoreRecipeCategory implements IRecipeCategory<GreenHouseCoreRecipe> {

    private final IDrawable blankDrawable;
    private final IDrawable icon;
    private final IGuiHelper guiHelper;

    public GreenHouseCoreRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        icon = guiHelper.createDrawableItemStack(ItemRegistry.greenhouse_core_container_item.get().getDefaultInstance());
        blankDrawable = guiHelper.createBlankDrawable(116, 45);
    }

    @Override
    public @NotNull RecipeType<GreenHouseCoreRecipe> getRecipeType() {
        return ESJEIPlugin.GREENHOUSE_CORE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual");
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
    public void setRecipe(IRecipeLayoutBuilder builder, GreenHouseCoreRecipe recipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT,4,16)
                .addIngredients(Ingredient.of(ItemRegistry.greenhouse_core_container_item.get()));
        builder.addOutputSlot(96,16)
                .addIngredients(Ingredient.of(recipe.end()));

        builder.addSlot(RecipeIngredientRole.INPUT,33+8,3)
                .addIngredients(Ingredient.of(recipe.input()));
    }


    @Override
    public void getTooltip(ITooltipBuilder tooltip, GreenHouseCoreRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);

        tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.not_in_room"));
        tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.crop_bonus"));
        tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.stage",3));

        double daysFor3Stages = 3 * EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm(Minecraft.getInstance().level)
                * CommonConfig.Crop.seasonalPrayerRitualTimeCost.get();
        String display = String.format("%.1f", daysFor3Stages);
        tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.time_cost",display));
    }

    @Override
    public void draw(GreenHouseCoreRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);


        guiHelper.getSlotDrawable().draw(guiGraphics, 32+8, 2);

        guiHelper.getSlotDrawable().draw(guiGraphics, 3, 15);
        guiHelper.getSlotDrawable().draw(guiGraphics, 95, 15);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(3f, 0.5f, 1f);
        guiHelper.getRecipeArrow().draw(guiGraphics, 8,42);
        guiHelper.getRecipeArrowFilled().draw(guiGraphics, 8,42, 0, 0, 0, 21 - (int) (System.currentTimeMillis() % (2100*30) / 3000));

        guiGraphics.pose().popPose();

        FormattedCharSequence visualOrderText = SimpleUtil.addSolarIconBefore(SolarTerm.collectValidValues()[recipe.season().ordinal()*6+3], recipe.season().getTranslation()).getVisualOrderText();
        guiGraphics.drawString(Minecraft.getInstance().font, visualOrderText,
                34,32, Color.WHITE);
    }
}
