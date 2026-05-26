package com.teamtea.eclipticseasons.compat.jei;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.compat.jei.fake.CauldronRecipe;
import com.teamtea.eclipticseasons.compat.jei.fake.CauldronRecipeCategory;
import com.teamtea.eclipticseasons.compat.jei.fake.GreenHouseCoreRecipe;
import com.teamtea.eclipticseasons.compat.jei.fake.GreenHouseCoreRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

@JeiPlugin
public class ESJEIPlugin implements IModPlugin {
   public static final Identifier PLUGIN_ID = EclipticSeasons.rl("jei_plugin");

   public static final Identifier HUMIDITY_CONTROL = EclipticSeasons.rl("humidity_control");
   public static final Identifier CAULDRON = EclipticSeasons.rl("cauldron");
   public static final Identifier GREENHOUSE_CORE = EclipticSeasons.rl("greenhouse_core");
   public static final Identifier WETTER = EclipticSeasons.rl("wetter");
   // public static final Identifier SEASON_QUEST = EclipticSeasons.rl("season_quest");

   public static final IRecipeType<HumidityControl> HUMIDITY_CONTROL_RECIPE_TYPE = getType(HUMIDITY_CONTROL, HumidityControl.class);
   public static final IRecipeType<CauldronRecipe> CAULDRON_RECIPE_TYPE = getType(CAULDRON, CauldronRecipe.class);
   public static final IRecipeType<GreenHouseCoreRecipe> GREENHOUSE_CORE_TYPE = getType(GREENHOUSE_CORE, GreenHouseCoreRecipe.class);
   public static final IRecipeType<WetterStructure> WETTER_TYPE = getType(WETTER, WetterStructure.class);
   // public static final IRecipeType<SeasonQuest> SEASON_QUEST_TYPE = getType(SEASON_QUEST, SeasonQuest.class);

   public static <T> IRecipeType<T> getType(Identifier rs, Class<? extends T> recipeClass) {
       return IRecipeType.create(rs.getNamespace(), rs.getPath(), recipeClass);
   }

   @Override
   public @NonNull Identifier getPluginUid() {
       return PLUGIN_ID;
   }

   @Override
   public void registerCategories(IRecipeCategoryRegistration registry) {
       registry.addRecipeCategories(new JEIHumidityControlCategory(registry.getJeiHelpers().getGuiHelper()));
       registry.addRecipeCategories(new CauldronRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
       registry.addRecipeCategories(new GreenHouseCoreRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
       registry.addRecipeCategories(new JEIWetterCategory(registry.getJeiHelpers().getGuiHelper()));
       // registry.addRecipeCategories(new JEISeasonQuestCategory(registry.getJeiHelpers().getGuiHelper()));
   }

   @Override
   public void registerRecipeCatalysts(@NonNull IRecipeCatalystRegistration registration) {
       registration.addCraftingStation(HUMIDITY_CONTROL_RECIPE_TYPE, ItemRegistry.block_in_wooden_grate_block_item.get());
       registration.addCraftingStation(CAULDRON_RECIPE_TYPE, Items.CAULDRON);
       registration.addCraftingStation(GREENHOUSE_CORE_TYPE, ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance(),
               ItemRegistry.greenhouse_core_container_item.get().getDefaultInstance());
       // registration.addCraftingStation(SEASON_QUEST_TYPE, ItemRegistry.seasonal_prayer_scroll_item.get());
   }

   @Override
   public void registerRecipes(@NonNull IRecipeRegistration registration) {
       Level level = Minecraft.getInstance().level;
       if (level == null) return;
       level.registryAccess()
               .lookup(ESRegistries.HUMIDITY_CONTROL)
               .ifPresent(controls -> registration.addRecipes(HUMIDITY_CONTROL_RECIPE_TYPE, ESSortInfo.sorted2(controls)));
       level.registryAccess().lookup(ESRegistries.WETTER)
               .ifPresent(controls -> registration.addRecipes(WETTER_TYPE, ESSortInfo.sorted2(controls)));
       // level.registryAccess().lookup(ESRegistries.SEASON_QUEST)
       //         .ifPresent(controls -> registration.addRecipes(SEASON_QUEST_TYPE,
       //                 ESSortInfo.sorted2(controls)
       //         ));

       registration.addRecipes(CAULDRON_RECIPE_TYPE, CauldronRecipe.caldronRecipeList.get());
       registration.addRecipes(GREENHOUSE_CORE_TYPE, GreenHouseCoreRecipe.Recipes.get());
   }
}
