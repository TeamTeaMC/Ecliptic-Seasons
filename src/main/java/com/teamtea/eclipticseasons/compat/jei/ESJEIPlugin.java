package com.teamtea.eclipticseasons.compat.jei;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@JeiPlugin
public class ESJEIPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_ID = EclipticSeasons.rl("jei_plugin");

    public static final ResourceLocation HUMIDITY_CONTROL = EclipticSeasons.rl("humidity_control");

    public static final mezz.jei.api.recipe.RecipeType<HumidityControl> HUMIDITY_CONTROL_RECIPE_TYPE = getType(HUMIDITY_CONTROL, HumidityControl.class);

    public static <T> mezz.jei.api.recipe.RecipeType<T> getType(ResourceLocation rs, Class<? extends T> recipeClass) {
        return mezz.jei.api.recipe.RecipeType.create(rs.getNamespace(), rs.getPath(), recipeClass);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new JEIHumidityControlCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        for (Block block : BlockRegistry.getAllChangedGrateBlocks()) {
            registration.addRecipeCatalyst(BlockRegistry.getOriginalCopperGrateBlock(block).asItem().getDefaultInstance(), HUMIDITY_CONTROL_RECIPE_TYPE);
        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        Optional<Registry<HumidityControl>> humidityControls = level.registryAccess().registry(
                ESRegistries.HUMIDITY_CONTROL
        );
        humidityControls.ifPresent(controls -> registration.addRecipes(HUMIDITY_CONTROL_RECIPE_TYPE, controls.stream().toList()));
    }
}
