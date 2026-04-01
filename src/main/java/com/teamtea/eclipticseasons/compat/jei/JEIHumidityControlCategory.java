package com.teamtea.eclipticseasons.compat.jei;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.client.util.BlockGuiRenderUtil;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.library.gui.ingredients.TagContentTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JEIHumidityControlCategory implements IRecipeCategory<HumidityControl> {
   private final IDrawable icon;
   private final IGuiHelper guiHelper;
   private final IDrawableStatic clock;
   private final IDrawableStatic shadow;
   private final IDrawableStatic range;
   private final IDrawableStatic right_down_arrow;
   private final IDrawableStatic blue_arrow_up;
   private final IDrawableStatic red_arrow;
   private final IDrawableStatic blankDrawable;

   public JEIHumidityControlCategory(IGuiHelper guiHelper) {
       this.guiHelper = guiHelper;
       icon = guiHelper.createDrawableItemStack(ItemRegistry.block_in_wooden_grate_block_item.get().getDefaultInstance());
       blankDrawable = guiHelper.createBlankDrawable(128, 60);
       clock = createIDrawableStatic("clock.png");
       shadow = createIDrawableStatic("shadow.png");
       range = createIDrawableStatic("range.png");
       right_down_arrow = createIDrawableStatic("right_down_arrow.png");
       blue_arrow_up = createIDrawableStatic("blue_arrow_up.png");
       red_arrow = createIDrawableStatic("red_arrow.png");
   }


   public IDrawableStatic createIDrawableStatic(String name) {
       return guiHelper.drawableBuilder(EclipticSeasons.rl("textures/gui/icon/" + name), 0, 0, 16, 16)
               .setTextureSize(16, 16).build();
   }


   @Override
   public @NotNull IRecipeType<HumidityControl> getRecipeType() {
       return ESJEIPlugin.HUMIDITY_CONTROL_RECIPE_TYPE;
   }

   @Override
   public @NotNull Component getTitle() {
       return Component.translatable("info.eclipticseasons.humidity_control");
   }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 60;
    }

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
               .add(recipe.ingredient().ingredient());

       for (Block allChangedGrateBlock : BlockRegistry.getAllChangedGrateBlocks()) {
           builder.addInvisibleIngredients(RecipeIngredientRole.RENDER_ONLY)
                   .add(allChangedGrateBlock);
       }
       // builder.addInvisibleIngredients(RecipeIngredientRole.RENDER_ONLY)
       //         .add((BlockRegistry.getAllChangedGrateBlocks().stream().map(Block::asItem).map(Item::builtInRegistryHolder).toList()));

       for (PosAndBlockStateCheck check : recipe.checks()) {
           if (check.block().blocks().isPresent() && check.block().blocks().get().size() > 0) {
               for (Holder<Block> blockHolder : check.block().blocks().get()) {
                   builder.addInvisibleIngredients(RecipeIngredientRole.RENDER_ONLY)
                           .add((blockHolder.value()));
               }
           }
       }

       builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 40)
               .add(recipe.result().create());

   }

   @Override
   public void getTooltip(ITooltipBuilder tooltip, HumidityControl recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
       IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
       if (mouseX > 32 && mouseX < 64
               && mouseY > 4 && mouseY < 60) {
           int currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 2000;
           for (PosAndBlockStateCheck check : recipe.checks()) {
               if (check.block().blocks().isPresent() && check.block().blocks().get().size() > 0) {
                   int index = currentTimeMillis % check.block().blocks().get().size();
                   Holder<Block> blockHolder = check.block().blocks().get().get(index);

                   if (check.offset().getY() == -1 && check.offset().getX() == 0 && check.offset().getZ() == 0) {
                       tooltip.add(Component.translatable("info.eclipticseasons.humidity_control.below_need", blockHolder.value().getName().withStyle(ChatFormatting.GRAY)));
                   } else {
                       tooltip.add(Component.translatable("info.eclipticseasons.humidity_control.common_need", blockHolder.value().getName().withStyle(ChatFormatting.GRAY)));
                   }

                   tooltip.add(Component.translatable("info.eclipticseasons.humidity_control.extra_hint"));
                   IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
                   IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
                   IIngredientRenderer<ItemStack> renderer = ingredientManager.getIngredientRenderer(blockHolder.value().asItem().getDefaultInstance());

                   List<ItemStack> stacks = new ArrayList<>();
                   for (Holder<Block> holder : check.block().blocks().get()) {
                       stacks.add(holder.value().asItem().getDefaultInstance());
                   }
                   tooltip.add(new TagContentTooltipComponent<>(renderer, stacks));
               }
           }

       }
   }

   @Override
   public void draw(HumidityControl recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor
           guiGraphics, double mouseX, double mouseY) {
       int startX = (int) (guiGraphics.pose().m20());
       int startY = (int) (guiGraphics.pose().m21());
       int startX0= (int) (guiGraphics.mouseX-mouseX);
       int startY0= (int) (guiGraphics.mouseY-mouseY);


       guiHelper.getSlotDrawable().draw(guiGraphics, 15, 7);
       guiHelper.getSlotDrawable().draw(guiGraphics, 99, 39);

       guiGraphics.pose().pushMatrix();
       int shadow_y = 4;
       for (PosAndBlockStateCheck check : recipe.checks()) {
           if (check.offset().getY() != 0) {
               shadow_y += 20;
               break;
           }
       }
       guiGraphics.pose().translate(25, shadow_y);
       guiGraphics.pose().scale(4f, 3f);
       shadow.draw(guiGraphics, 0, 0);
       guiGraphics.pose().popMatrix();

       // guiGraphics.pose().pushMatrix();
       // guiGraphics.pose().translate(32, 20, 0);
       // guiGraphics.pose().mulPose(XYZ.deg_to_rad(0, 0, 90));
       // Internal.getTextures().getRecipeArrow().draw(guiGraphics, 0, 0);
       // guiGraphics.pose().popMatrix();

       guiGraphics.pose().pushMatrix();
       guiGraphics.pose().translate(40 + 1, 3);
       guiGraphics.pose().scale(2f, 2f);


       // guiGraphics.pose().pushMatrix();
       // guiGraphics.pose().scale(.75f, .75f, .75f);
       // guiGraphics.pose().translate(3, 3, 0);
       // guiGraphics.renderItem(recipe.ingredient().getItems()[0], 0, 0);
       // guiGraphics.pose().popMatrix();
       int currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 2000;
       for (PosAndBlockStateCheck check : recipe.checks()) {
           if (check.block().blocks().isPresent() && check.block().blocks().get().size() > 0) {
               int index = currentTimeMillis % check.block().blocks().get().size();
               Holder<Block> blockHolder = check.block().blocks().get().get(index);
               guiGraphics.pose().pushMatrix();
               guiGraphics.pose().translate(16 * check.offset().getX(), -16 * check.offset().getY());
               float x = 7f;
               float y = -3f + 2;
               int guiOffset = 0;
               if (blockHolder.isBound()) {
                   BlockState blockState = blockHolder.value().defaultBlockState();
                   // BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                   // BakedModel bakedmodel = blockRenderer.getBlockModel(blockState);
                   guiGraphics.pose().pushMatrix();
                   guiGraphics.pose().translate((float) (x + 8), (float) (y + 8));

                   try {
                       guiGraphics.pose().scale(16.0F, -16.0F);

                       // guiGraphics.pose().mulPose(XYZ.deg_to_rad(30, 225, 0));
                       guiGraphics.pose().scale(0.625f, 0.625f);

                       // Lighting.setupLevel();
                       // BlockGuiRenderUtil.renderBlockInGui(guiGraphics, blockState);
                       BlockGuiRenderUtil.renderBlockInGui(guiGraphics,blockState,
                               (int)startX+40,  (int)startY+28);
                       // guiGraphics.flush();

                   } catch (Throwable throwable) {
                       EclipticSeasons.logger(throwable);
                   }

                   guiGraphics.pose().popMatrix();
               }
               guiGraphics.pose().popMatrix();
           }
       }

       currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 1200;
       guiGraphics.pose().translate(0, 0);
       List<Block> allChangedGrateBlocks = BlockRegistry.getAllChangedGrateBlocks();
       int index = currentTimeMillis % allChangedGrateBlocks.size();
       guiGraphics.item(BlockRegistry.getOriginalCopperGrateBlock(allChangedGrateBlocks.get(index)).asItem().getDefaultInstance(), 0, 0);

       guiGraphics.pose().popMatrix();

       // guiGraphics.renderTooltip(
       //         Minecraft.getInstance().font,
       //         List.of(Component.translatable("" + recipe.lasting_time()),
       //                 Component.translatable("" + recipe.level())),
       //         Optional.empty(),
       //         (int) mouseX, (int) mouseY
       // );

       Font font = Minecraft.getInstance().font;
       {
           guiGraphics.pose().pushMatrix();
           guiGraphics.pose().translate(96, 0);
           // guiGraphics.pose().scale(2f, 2f, 2f);
           clock.draw(guiGraphics, 0, 0);

           {
               guiGraphics.pose().pushMatrix();
               guiGraphics.pose().scale(.75f, .75f);
               String timeString = recipe.noCost() ? "∞" : ((int) (recipe.lasting_time() / Minecraft.getInstance().level.tickRateManager().tickrate())) + "s";
               guiGraphics.text(font, timeString, 20,
                       (int) (font.lineHeight / 1.5f), Color.GRAY.getRGB(), false);
               guiGraphics.pose().popMatrix();
           }


           currentTimeMillis = (int) ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 1800;
           {
               guiGraphics.pose().pushMatrix();
               guiGraphics.pose().translate(0.5f, 10);
               String aString = "";
               IDrawableStatic iDrawableStatic;
               if (currentTimeMillis % 2 == 0) {
                   iDrawableStatic = recipe.level() > 0 ? blue_arrow_up : red_arrow;
                   aString = "" + Math.abs(recipe.level());
               } else {
                   iDrawableStatic = range;
                   aString = "" + recipe.range();
               }
               iDrawableStatic.draw(guiGraphics, 0, 0);
               {
                   guiGraphics.pose().pushMatrix();
                   guiGraphics.pose().scale(.75f, .75f);
                   guiGraphics.text(font, aString, 20,
                           (int) (font.lineHeight / 1.5f + 1), Color.GRAY.getRGB(), false);
                   guiGraphics.pose().popMatrix();
               }
               guiGraphics.pose().popMatrix();
           }

           guiGraphics.pose().popMatrix();
       }
       right_down_arrow.draw(guiGraphics, 80, 24);


   }
}
