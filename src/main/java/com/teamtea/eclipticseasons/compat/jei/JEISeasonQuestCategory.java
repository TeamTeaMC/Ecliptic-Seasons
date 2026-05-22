package com.teamtea.eclipticseasons.compat.jei;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.client.ClientTooltipFlag;
import org.jspecify.annotations.NonNull;

import java.util.List;


@SuppressWarnings("removal")
public class JEISeasonQuestCategory implements IRecipeCategory<SeasonQuest> {

   private final IDrawable blankDrawable;
   private final IDrawable icon;
   private final IGuiHelper guiHelper;

   public JEISeasonQuestCategory(IGuiHelper guiHelper) {
       this.guiHelper = guiHelper;
       icon = guiHelper.createDrawableItemStack(ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance());
       blankDrawable = guiHelper.createBlankDrawable(110, 28);
   }

   @Override
   public @NonNull IRecipeType<SeasonQuest> getRecipeType() {
       // return ESJEIPlugin.SEASON_QUEST_TYPE;
       return null;
   }

   @Override
   public @NonNull Component getTitle() {
       return Component.translatable("info.eclipticseasons.recipe_category.season_quest");
   }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 28;
    }

    public @NonNull IDrawable getBackground() {
       return blankDrawable;
   }

   @Override
   public IDrawable getIcon() {
       return icon;
   }

   @Override
   public void setRecipe(IRecipeLayoutBuilder builder, SeasonQuest recipe, IFocusGroup iFocusGroup) {

       List<ItemPredicate> need = recipe.need();
       for (ItemPredicate predicate : need) {
           predicate.items().ifPresent(
                   holders -> {
                       builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                               .addIngredients(Ingredient.of(holders.stream().map(Holder::value)));
                   }
           );
       }

       int offset = need.size() == 1 ? 20 : need.size() == 2 ? 10 : 0;

       var award = recipe.award();
       for (int i = 0, awardSize = award.size(); i < awardSize; i++) {
           ItemStack stack = award.get(i).create();
           builder.addSlot(RecipeIngredientRole.OUTPUT, 90 + i * 20 + (need.size() - 3) * 20
                           + offset, 4)
                   .add(stack);
       }
   }


   @Override
   public void getTooltip(ITooltipBuilder tooltip, SeasonQuest recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
       IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
       boolean hit = false;
       List<ItemPredicate> need = recipe.need();

       int offset = need.size() == 1 ? 20 : need.size() == 2 ? 10 : 0;

       for (int i = 0, needSize = need.size(); i < needSize; i++) {
           ItemPredicate predicate = need.get(i);
           var holderSet = predicate.items().orElseGet(HolderSet::empty).stream().toList();
           if (!holderSet.isEmpty()) {
               int currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 1200;
               int index = currentTimeMillis % holderSet.size();
               ItemStack defaultInstance = holderSet.get(index).value().getDefaultInstance();
               int x = 5 + i * 20 - 1 + offset, y = 3;
               if (mouseX > x && mouseX < x + 18 && mouseY > y && mouseY < y + 18) {
                   List<Component> tooltipLines = defaultInstance.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level),
                           Minecraft.getInstance().player,
                           ClientTooltipFlag.of(Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
                   tooltip.addAll(tooltipLines);
                   hit = true;
                   break;
               }
           }
       }

       if (!hit) {
           tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.season_quest.time"));
           tooltip.add(Component.translatable("info.eclipticseasons.recipe_category.season_quest.time.period", recipe.start().orElse(SolarTerm.NONE).getTranslation(), recipe.end().orElse(SolarTerm.NONE).getTranslation()));
       }
   }
    @Override
   public void draw(SeasonQuest recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
       IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

       List<ItemPredicate> need = recipe.need();
       int offset = need.size() == 1 ? 20 : need.size() == 2 ? 10 : 0;

       for (int i = 0, needSize = need.size(); i < needSize; i++) {
           ItemPredicate predicate = need.get(i);
           int x = 5 + i * 20 - 1 + offset, y = 3;
           guiHelper.getSlotDrawable().draw(guiGraphics, x, y);

           var holderSet = predicate.items().orElseGet(HolderSet::empty).stream().toList();
           if (!holderSet.isEmpty()) {
               int currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 1200;
               guiGraphics.pose().translate(0, 0);
               int index = currentTimeMillis % holderSet.size();
               guiGraphics.item(holderSet.get(index).value().getDefaultInstance(), x + 1, y + 1);


               if (mouseX > x && mouseX < x + 18 && mouseY > y && mouseY < y + 18) {
                   guiGraphics.fill(RenderPipelines.GUI,
                           x + 1, 4, x + 1 + 16, 4 + 16,
                           0x80FFFFFF
                   );
               }
           }
       }


       var award = recipe.award();
       for (int i = 0, awardSize = award.size(); i < awardSize; i++) {
           guiHelper.getSlotDrawable().draw(guiGraphics, 90 + i * 20 - 1 + (need.size() - 3) * 20 + offset, 3);
       }

       guiHelper.getRecipeArrowFilled().draw(guiGraphics, 64 + (need.size() - 3) * 20 + offset, 4);

   }
}
