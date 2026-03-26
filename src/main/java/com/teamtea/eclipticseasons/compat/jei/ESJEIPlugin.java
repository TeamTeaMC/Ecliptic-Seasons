package com.teamtea.eclipticseasons.compat.jei;//package com.teamtea.eclipticseasons.compat.jei;
//
//import com.teamtea.eclipticseasons.EclipticSeasons;
//import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
//import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
//import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
//import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
//import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
//import com.teamtea.eclipticseasons.common.registry.ESRegistries;
//import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
//import com.teamtea.eclipticseasons.compat.jei.fake.CauldronRecipe;
//import com.teamtea.eclipticseasons.compat.jei.fake.CauldronRecipeCategory;
//import com.teamtea.eclipticseasons.compat.jei.fake.GreenHouseCoreRecipe;
//import com.teamtea.eclipticseasons.compat.jei.fake.GreenHouseCoreRecipeCategory;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.registration.IRecipeCatalystRegistration;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import net.minecraft.client.Minecraft;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import org.jetbrains.annotations.NotNull;
//
//@JeiPlugin
//public class ESJEIPlugin implements IModPlugin {
//    public static final Identifier PLUGIN_ID = EclipticSeasons.rl("jei_plugin");
//
//    public static final Identifier HUMIDITY_CONTROL = EclipticSeasons.rl("humidity_control");
//    public static final Identifier CAULDRON = EclipticSeasons.rl("cauldron");
//    public static final Identifier GREENHOUSE_CORE = EclipticSeasons.rl("greenhouse_core");
//    public static final Identifier WETTER = EclipticSeasons.rl("wetter");
//    public static final Identifier SEASON_QUEST = EclipticSeasons.rl("season_quest");
//
//    public static final RecipeType<HumidityControl> HUMIDITY_CONTROL_RECIPE_TYPE = getType(HUMIDITY_CONTROL, HumidityControl.class);
//    public static final RecipeType<CauldronRecipe> CAULDRON_RECIPE_TYPE = getType(CAULDRON, CauldronRecipe.class);
//    public static final RecipeType<GreenHouseCoreRecipe> GREENHOUSE_CORE_TYPE = getType(GREENHOUSE_CORE, GreenHouseCoreRecipe.class);
//    public static final RecipeType<WetterStructure> WETTER_TYPE = getType(WETTER, WetterStructure.class);
//    public static final RecipeType<SeasonQuest> SEASON_QUEST_TYPE = getType(SEASON_QUEST, SeasonQuest.class);
//
//    public static <T> RecipeType<T> getType(Identifier rs, Class<? extends T> recipeClass) {
//        return RecipeType.create(rs.getNamespace(), rs.getPath(), recipeClass);
//    }
//
//    @Override
//    public @NotNull Identifier getPluginUid() {
//        return PLUGIN_ID;
//    }
//
//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registry) {
//        registry.addRecipeCategories(new JEIHumidityControlCategory(registry.getJeiHelpers().getGuiHelper()));
//        registry.addRecipeCategories(new CauldronRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
//        registry.addRecipeCategories(new GreenHouseCoreRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
//        registry.addRecipeCategories(new JEIWetterCategory(registry.getJeiHelpers().getGuiHelper()));
//        registry.addRecipeCategories(new JEISeasonQuestCategory(registry.getJeiHelpers().getGuiHelper()));
//    }
//
//    @Override
//    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
//        for (Block block : BlockRegistry.getAllChangedGrateBlocks()) {
//            registration.addRecipeCatalyst(BlockRegistry.getOriginalCopperGrateBlock(block).asItem().getDefaultInstance(), HUMIDITY_CONTROL_RECIPE_TYPE);
//        }
//        registration.addRecipeCatalyst(Items.CAULDRON, CAULDRON_RECIPE_TYPE);
//        registration.addRecipeCatalysts(GREENHOUSE_CORE_TYPE, ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance(),
//                ItemRegistry.greenhouse_core_container_item.get().getDefaultInstance());
//        registration.addRecipeCatalyst(ItemRegistry.seasonal_prayer_scroll_item.get(), SEASON_QUEST_TYPE);
//    }
//
//    @Override
//    public void registerRecipes(@NotNull IRecipeRegistration registration) {
//        Level level = Minecraft.getInstance().level;
//        if (level == null) return;
//        level.registryAccess().registry(ESRegistries.HUMIDITY_CONTROL)
//                .ifPresent(controls -> registration.addRecipes(HUMIDITY_CONTROL_RECIPE_TYPE, ESSortInfo.sorted2(controls)));
//        level.registryAccess().registry(ESRegistries.WETTER)
//                .ifPresent(controls -> registration.addRecipes(WETTER_TYPE, ESSortInfo.sorted2(controls)));
//        level.registryAccess().registry(ESRegistries.SEASON_QUEST)
//                .ifPresent(controls -> registration.addRecipes(SEASON_QUEST_TYPE,
//                        ESSortInfo.sorted2(controls)
//                ));
//
//        registration.addRecipes(CAULDRON_RECIPE_TYPE, CauldronRecipe.caldronRecipeList.get());
//        registration.addRecipes(GREENHOUSE_CORE_TYPE, GreenHouseCoreRecipe.lazy.get());
//    }
//}
