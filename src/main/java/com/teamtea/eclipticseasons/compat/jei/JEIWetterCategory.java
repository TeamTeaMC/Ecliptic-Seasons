package com.teamtea.eclipticseasons.compat.jei;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.client.util.BlockGuiRenderUtil;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class JEIWetterCategory implements IRecipeCategory<WetterStructure> {
    private final IDrawable icon;
    private final IGuiHelper guiHelper;
    private final IDrawableStatic clock;
    private final IDrawableStatic shadow;
    private final IDrawableStatic range;
    private final IDrawableStatic right_down_arrow;
    private final IDrawableStatic blue_arrow_up;
    private final IDrawableStatic red_arrow;
    private final IDrawableStatic blankDrawable;

    public JEIWetterCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        icon = guiHelper.drawableBuilder(EclipticSeasons.rl("textures/font/humidity_icons.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        blankDrawable = guiHelper.createBlankDrawable(96, 60);
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
    public @NotNull IRecipeType<WetterStructure> getRecipeType() {
        return ESJEIPlugin.WETTER_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("info.eclipticseasons.recipe_category.wetter");
    }

    @Override
    public int getWidth() {
        return 96;
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
    public void setRecipe(IRecipeLayoutBuilder builder, WetterStructure recipe, @NotNull IFocusGroup ingredients) {
        for (PosAndBlockStateCheck check : recipe.checks()) {
            if (check.block().blocks().isPresent() && check.block().blocks().get().size() > 0) {
                for (Holder<Block> blockHolder : check.block().blocks().get()) {
                    if (blockHolder.value().asItem() != Items.AIR)
                        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                                .add(Ingredient.of(blockHolder.value()));
                    if (blockHolder.value().defaultBlockState().getFluidState() instanceof FluidState state
                            && !state.isEmpty()) {
                        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                                .add(state.getType());

                    }
                }
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, WetterStructure recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
    public void draw(WetterStructure recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor
            guiGraphics, double mouseX, double mouseY) {

        int startX = (int) (guiGraphics.pose().m20());
        int startY = (int) (guiGraphics.pose().m21());

        guiGraphics.pose().pushMatrix();

        // patch offset
        guiGraphics.pose().translate(-25, 0);

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

        // patch offset
        guiGraphics.pose().translate(-25, 0);

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
                    guiGraphics.pose().translate(x + 8, y + 8);

                    try {
                        guiGraphics.pose().scale(16.0F, -16.0F);

                        // guiGraphics.pose().mulPose(XYZ.deg_to_rad(30, 225, 0));
                        guiGraphics.pose().scale(0.625f, 0.625f);

                        // Lighting.setupLevel();
                        // BlockGuiRenderUtil.renderBlockInGui(guiGraphics, blockState);
                        BlockGuiRenderUtil.renderBlockInGui(guiGraphics, blockState,
                                (int) startX + 16, (int) startY + 24);
                        if (!blockState.getFluidState().isEmpty())
                            BlockGuiRenderUtil.renderFluidInGui(guiGraphics, blockState.getFluidState(), startX + 16, startY + 0, -0.4f, 1f);
                        // guiGraphics.flush();

                    } catch (Throwable throwable) {
                        EclipticSeasons.logger(throwable);
                    }

                    guiGraphics.pose().popMatrix();
                }
                guiGraphics.pose().popMatrix();
            }
        }


        guiGraphics.pose().popMatrix();
        // patch offset
        guiGraphics.pose().translate(-34, 0);
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
                String timeString = ((int) (recipe.lasting_time() / Minecraft.getInstance().level.tickRateManager().tickrate())) + "s";
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
    }
}
