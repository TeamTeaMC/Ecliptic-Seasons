package com.teamtea.eclipticseasons.compat.jei.fake;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.item.GreenhouseCoreBlockItem;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.compat.jei.ESJEIPlugin;
import com.teamtea.eclipticseasons.config.CommonConfig;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

import java.awt.*;


// @SuppressWarnings("removal")
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
   public @NonNull IRecipeType<GreenHouseCoreRecipe> getRecipeType() {
       return ESJEIPlugin.GREENHOUSE_CORE_TYPE;
   }

   @Override
   public @NonNull Component getTitle() {
       return Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual");
   }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 45;
    }

    public @NonNull IDrawable getBackground() {
       return blankDrawable;
   }

   @Override
   public IDrawable getIcon() {
       return icon;
   }

   @Override
   public void setRecipe(IRecipeLayoutBuilder builder, GreenHouseCoreRecipe recipe, IFocusGroup iFocusGroup) {
       builder.addSlot(RecipeIngredientRole.INPUT,4,16)
               .add(ItemRegistry.greenhouse_core_container_item.get());
       builder.addOutputSlot(96,16)
               .add(recipe.end());

       builder.addSlot(RecipeIngredientRole.INPUT,33+8,3)
               .add(recipe.input());

       if (recipe.end() instanceof GreenHouseCoreBlock greenHouseCoreBlock) {
           Item essenceItem = switch (greenHouseCoreBlock.getSeason()) {
               case SPRING -> ItemRegistry.spring_greenhouse_essence_item.get();
               case SUMMER -> ItemRegistry.summer_greenhouse_essence_item.get();
               case AUTUMN -> ItemRegistry.autumn_greenhouse_essence_item.get();
               case WINTER -> ItemRegistry.winter_greenhouse_essence_item.get();
               default -> null;
           };

           if (essenceItem != null) {
               builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                       .add(essenceItem);
           }
       }
   }


   @Override
   public void getTooltip(ITooltipBuilder tooltip, GreenHouseCoreRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
       IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);

       tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.not_in_room"));
       tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.crop_bonus"));
       tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.stage",3));

       double daysFor3Stages = 3
               * CommonConfig.Crop.seasonalPrayerRitualTimeCost.get();
       String display = String.format("%.1f", daysFor3Stages);
       tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.greenhouse_ritual.time_cost",display));
   }

   @Override
   public void draw(GreenHouseCoreRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
       int startX = (int) (guiGraphics.pose().m20());
       int startY = (int) (guiGraphics.pose().m21());

       IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);


       guiHelper.getSlotDrawable().draw(guiGraphics, 32+8, 2);

       guiHelper.getSlotDrawable().draw(guiGraphics, 3, 15);
       guiHelper.getSlotDrawable().draw(guiGraphics, 95, 15);

       guiGraphics.pose().pushMatrix();
       guiGraphics.pose().scale(3f, 0.5f);
       guiHelper.getRecipeArrow().draw(guiGraphics, 8,42);
       if (guiHelper.getRecipeArrowFilled() instanceof IDrawableStatic ds) {
           ds.draw(guiGraphics, 8,42, 0, 0, 0, 21 - (int) (System.currentTimeMillis() % (2100*30) / 3000));
       }
       guiGraphics.pose().popMatrix();

       FormattedCharSequence visualOrderText = SimpleUtil.addSolarIconBefore(SolarTerm.collectValidValues()[recipe.season().ordinal()*6+3], recipe.season().getTranslation()).getVisualOrderText();
       guiGraphics.text(Minecraft.getInstance().font, visualOrderText,
               34,32, Color.WHITE.getRGB());
   }
}
