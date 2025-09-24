package com.teamtea.eclipticseasons.compat.jei;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
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
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new JEIHumidityControlCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ItemRegistry.block_in_wooden_grate_block_item.get().getDefaultInstance(), HUMIDITY_CONTROL_RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        Optional<Registry<HumidityControl>> humidityControls = level.registryAccess().registry(
                ESRegistries.HUMIDITY_CONTROL
        );
        humidityControls.ifPresent(controls -> registration.addRecipes(HUMIDITY_CONTROL_RECIPE_TYPE, ESSortInfo.sorted2(controls)));
    }
}
