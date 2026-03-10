> As the ecosystem for 1.20.1 begins to wind down with major mods like Create moving on, we are also transitioning into
> the final maintenance phase for our mod on this version.

### 0.12.18.3

* Fixed an issue where rendered snow could unexpectedly melt in cold biomes without rainfall.

### 0.12.18.2

* Optimized several rendering settings related to the extra snow layer.

### 0.12.18.1

* When **forced compatibility** is enabled for modded leaves that do not support Forge events, and a **random tick event
  ** is triggered but the seasonal check fails, the leaves will now proceed with the **decay process**.
* Fixed an issue where the **`season_textures` resource pack** did not properly support the `start_season` field.
* Improved the **ParticleIcon detection logic** for models defined in the `season_textures` resource pack.

### 0.12.18

* Fixed an issue where **seasonal texture models** would not apply if in snowy status.
* Extra snow layer rendering on leaves now only applies to the **top side of leaves**, aligning more closely with
  vanilla behavior.
* Snow state is now initialized by default in **extremely cold biomes**.

Configuration:

* Added a new option: **ExtraSnowLayerCulling**.
* **SnowInFence** and **SnowInFenceOnlySnowy** are now **disabled by default**.
* Added compatibility **common config synchronization support** for **EclipticSeasonsBundles**.

### 0.12.17

- Added model-like face culling for snow inside fences and extra snow layers, reducing z-fighting at long distances.

### 0.12.16

- Added an automatic configuration synchronization system that syncs all Common config files between the server and
  client. If the client does not want their original settings to be restored after leaving a world, the enforcement
  option can be disabled in the configuration.
- Fixed several legacy issues.

### 0.12.15

- Added an optional **Extra Snow Layer** visual feature. When enabled, snowy blocks may render a thin additional snow
  layer based on the biome’s current snow depth, making snow accumulation appear more gradual and natural across the
  terrain. This effect works with **Sodium** and **Embeddium**.
- Renamed the internal block tag **`snow_layer_cannot_survive_on`** to **`snow_layer_cannot_survive_in`** to better
  reflect its purpose.

### 0.12.14

- Fixed several issues in Season Definitions, including deserialization errors, incorrect rule evaluation, and some
  feature placement problems.

#### 0.12.13

- HeatStroke has been adjusted to a progressive count, so you won't get heatstroke immediately or for long periods of
  time in the sun of summer.

#### 0.12.12.1

- Change the default for `RainTogether` in CommonConfig from false to true

#### 0.12.12

Update: Mixin Control Support

- New Feature: Added a dedicated configuration file for Mixins.
- Toggleable Mixins: You can now freely enable or disable specific Mixins to improve compatibility or customize your
  experience.

#### 0.12.11.5.1

- Default enable datapack option `RegionalSnowTime` to align snowfall schedules based on three broad climate zones (
  Warm, Temperate, and Cold) instead of per-biome.

#### 0.12.11.5

- Add option `VoxyReloadWhenSeasonChanged` to auto clean voxy cache when solar term changes.

#### 0.12.11.4

- Added the SimpleSeasonHud option, extracted from our debug information, intended for players who do not want to
  install additional HUD mods. As part of maintaining a healthy community ecosystem, no extra configuration options are
  provided.
- Optimized the biome color update logic.

#### 0.12.11.3

- Fixed an issue where **SolarTermChangeEvent** could not be received on the client logic thread.
- Aligned DH’s LOD update determination logic with that of **Voxy**.
- Season changes now also trigger LOD updates when the relevant option is enabled.

#### 0.12.11.2.1

- Optimized some rendering logic for vanilla blocks such as snow connecting to fences.

#### 0.12.11.2

* Fixed an issue where some runtime-built snow-covered models (such as snow-covered fences) were not generated correctly
  in Voxy rendering.
* Added the option **VoxyLODAutoReload**. This option now determines whether to automatically refresh LOD appearances
  based on changes in weather-related snow coverage.
* The essence of this compatibility is invoking Voxy’s world import process. Voxy currently stores LOD build results in
  its database, which makes direct modification of that data difficult. Adjusting the generation process of the build
  results is comparatively easier. Therefore, LODs can now also be updated manually by running
  `/voxy import world <world_name>` instead of relying on automatic updates. This approach reduces performance
  requirements and mitigates update overhead for very large worlds.

### 0.12.11

- Optimized Voxy compatibility code again, resolving some runtime conflicts and significantly improving performance.

> Note that this update may affect Voxy data. Please back up your Voxy database. If you enable the compatibility option
> and later wish to disable or uninstall this mod, make sure to reset the Voxy database.

#### 0.12.10.3

- Move the Voxy test compatibility control option from Client to Common

#### 0.12.10.2

- Updated the code for voxy compablity.

#### 0.12.10.1

- Adjust the greenhouse dark-light detection logic from "sky-light only" to include "supplementary light."

### 0.12.10

- All configuration comments have been improved to help players better understand the purpose of each option.
  Renaming of configuration entries will be carried out once the configuration migration system is ready.

> Unfortunately, my available time for mod development will be significantly reduced in the near future. I hope these
> recent updates can improve a large part of the overall experience.
> If you encounter issues or compatibility problems, please provide accurate testing results and isolated test cases
> when
> reporting them; otherwise, I may not have sufficient time to investigate and resolve the issue.

### 0.12.9

- The debug information interface has been updated and refined. It has been moved to the top-left corner of the screen,
  with a reorganized layout and the addition of rainfall probability prediction data.

### 0.12.8

- Smart Fence Snow Rendering: Improved snow rendering within fences. It now intelligently detects cliff edges to adjust
  visuals accordingly, with new configuration options for precision.
- Dynamic Accumulation & Melting: Biome-specific snow accumulation and melt speeds are now supported. Global multipliers
  have also been added for easier overall balance control.
- Internal Refactoring: Direct access to BiomeWeather.snowDepth is now restricted; please use the provided getter/setter
  methods for improved data consistency.

#### 0.12.7.1

- New Feature with option `SnowInFence`: Vegetation Snow Allows snow to accumulate inside fences and grass, seamlessly
  matching surrounding
  terrain height for enhanced winter immersion.

### 0.12.7

- Adds the foundational code for split and cross-version support in Ecliptic Seasons: Bundles.
  Ecliptic Seasons: Bundles provides basic DataPack and resource pack support for other biome or crop mods.
  Contributions to add or merge additional support are welcome and greatly appreciated.

### 0.12.6

- Due to bloom conflicts between foggy weather and certain shader packs (such as newer versions of Photon) under
  specific conditions, Foggy Weather is now disabled by default.
- Experimental compatibility with Voxy has been added. This feature must be enabled in the settings and requires a
  restart.
  Because Voxy’s design philosophy does not allow seasonal systems to directly intervene in its rendering pipeline, a
  special workaround is used. Please back up your world and modpack before enabling this option.
- Fixed an initialization issue with BigGlobe when Voxy compatibility is enabled.

### 0.12.5.8

- Added an option `DistantHorizonsWinterLODForceUpdateAll`, default false since it cause more performance time cost.

### 0.12.5.4

- If IceAndSnowMelt and WaterFreezesInFrozenBiomes are enabled, ice blocks in cold biomes will not melt even if they are
  not outside the Snowy season.
- Added the option SnowKeepInSnowyBiomes to reconcile the previous behavior of snow blocks.
- Marked mountains as freezing biomes as well.
- Adjusted all 24000 variable values used in the code to now call `EclipticUtil.getDayLengthInMinecraft(level)` to
  improve compatibility with time mods in the future.
- Prevented the renderer from crashing when an empty level is passed in during level unloading if FoliageUnderTree is
  enabled.

### 0.12.5.3

- Improved compatibility when FoliageUnderTree is enabled, avoiding log errors when used with Xaero Maps Mod.
- Slightly adjusted the winter LOD check logic for DH and provided an option to disable it.

### 0.12.5.2

- Fixed #121 Snow leaf particles without snow layer.

### 0.12.5.1

- Removed entity collision for the windmill.
- Added the tag `eclipticseasons:crops/not_killed_by_climate`, which provides a higher-priority way to prevent certain
  plants—if configured in crop datapacks—from dying due to incorrect seasons or humidity.

### 0.12.5

- Improved chunk handling thanks to Pink_Cats’s suggestion in #117, fixing rare cases where river-biome climate refill
  could freeze the game when certain world-gen mods load chunks slowly or use unusual multithreading.
- Snow-covered leaves now release snow particles when broken.

### 0.12.4.1

- Added `RealWorldSolarTerms` option to synchronize in-game solar terms with real-world time.
- Added `EnableTimeBreed` option to remove day/night restrictions on animal breeding.

### 0.12.4

- Added JEI integration for Environmental Humidity, Season Quests, Cauldron Snow Transformation, and Season Ritual.

### 0.12.3.6

- Added `ForceChunkUpdateOnlyWhenMelt` option.
- Added `BeforeCheckSnowStatusEvent`.

### 0.12.3.5

- Added optional `NotIgnoreRiver` datapack for climatic queries.

### 0.12.3.4

- Added optional `RegionalSnowTime` built-in datapack for zone-based snowfall duration.
- Added `VanillaBiomeClimateSettings` built-in datapack to stabilize biome temperature and downfall behavior.

### 0.12.3.3

- Added `WaterFreezesInFrozenBiomes` option (enabled by default).
- Added biome tag `eclipticseasons:extreme_cold`.

### 0.12.3.2

- Added command to reset surface biome cache.
- Added optional autumn grass withering effect under trees.

### 0.12.3.1

- Added `is_snowy` condition for seasonal variation datapacks.

### 0.12.3-beta

- Added composite weather effects.
- Added `none` weather effect to cancel precipitation.
- Added persistent caching and weighted randomness for `BiomeRain`.
- Added KubeJS bindings for Ecliptic Seasons APIs.
- Fixed `NotRainInDesert` option not preventing desert rainfall.

### 0.12.2-beta

- Added `special_effect` field to `biome_rain` datapacks.
- Added `biome_rain_effect` datapack with `none` and `fog` effects.
- Stabilized fog rendering initialization.

### 0.12.1

- Added Seasonal Ritual for activating Seasonal Greenhouse Cores.
- Added experimental fog rendering during rain.
- Added calendar display mode for current solar term day.
- Added optional `RainTogether` and `SnowTogether` datapacks for weather synchronization.

### 0.12.0.5.1

- Fixed crash caused by repeated chunk load events triggered by Create deployers when SnowInWorld is enabled.

### 0.12.0.5

- Fixed recursive copying in WeatherRegion subgroup definitions.
- Added fallback handling for missing built-in biome IDs.

### 0.12.0.4

- Updated Distant Horizons compatibility to v2.3.5-b.

### 0.12.0.3

- Added optional classic-style snowy block resource pack to avoid CTM Z-fighting.

### 0.12.0.2.1

- Adjusted Snowy Grass Block models for compatibility with lower-grass resource packs.

### 0.12.0.2

- Standardized snowy type queries to return an empty object instead of null.

### 0.12.0-z-3

- Added cauldron snow and ice transformation during snowfall.

### 0.12.0-z-2

- Fixed an issue where the calendar displayed incorrectly in Next mode when it was the last solar term of the year.
- Added a more complete built-in compatibility system for crop growth conditions. Note that humidity requirements are
  now included; if you encounter errors, you can disable this feature in the settings.

## 0.12.0-z

- Added `extra_info` datapack support to prioritize Ecliptic Seasons datapacks and disable built-in registrations.
- Mod-provided datapack registrations can now be fully overridden (e.g. independent agro climate definitions per biome).
- Added temporary caching for server-side global weather parameter calculations when Solar Weather is enabled.
- Optimized snow reflection parameter handling during Iris shader loading, fixing distant transparency issues.

## 0.12.0-pre18-1

- Added compatibility between Distant Horizons and FrozenWater.

## 0.12.0-pre18

- Added seasonal support for water colors, water fog colors, fog colors, and related biome color entries.
- Added preview support for FrozenWater: rivers may form thin ice layers during snowfall.

## 0.12.0-pre17-1-3

- Added `ClearAfterSleep` configuration option.

## 0.12.0-pre17-1-2

- Added crop tags `eclipticseasons:crops/unaffected_by_seasons` and `eclipticseasons:crops/unaffected_by_humidity`.

## 0.12.0-pre17-1

- Added `sky_colors` field to biome color resource packs.

## 0.12.0-pre17

- Renamed multiple configuration keys for consistency and clarity:
    - `SeasonParticle` → `SeasonalParticles`
    - `butterflySpawnWeight` → `ButterflySpawnDelay`
    - `FallenLeavesDropWeight` → `FallenLeavesDropDelay`
    - `FireflySpawnWeight` → `FireflySpawnDelay`
    - `WildGooseSpawnWeight` → `WildGooseSpawnDelay`
    - `TopFaceCulling` → `CullTopFaceWithSnow`
    - `UseVanillaSnowCheck` → `UseVanillaLightCheckForSnow`
    - `SnowyEdges` → `SmoothSnowyEdges`
    - `MinChunkCompileWaringTime` → `MinChunkCompileWarningTime`
    - `StepMelt` → `SnowStepMelt`
    - `SnowyUnderSnowLike` → `SnowCoverUnderBlocks`
    - `DarkGreenhouseFailChance` → `LowLightGreenhouseFailChance`
    - `EnableCoreWork` → `SeasonCoreAffectsAnimals`

## 0.12.0-pre16

- Added configurable biome snow lines.
- Added additional data serialization caching to reduce chunk surface biome overhead.
- Split chunk snow data into snow records and weather status for more efficient serialization.

## 0.12.0-pre14

- Weather queries from other mods now resolve based on player surroundings or spawn point fallback.

## 0.12.0-pre13-2

- Optimized broom behavior when clearing snowy grass blocks.
- Added `SnowStepMelt` behavior: entities stepping on snowy blocks can remove snow cover.

## 0.12.0-pre13

- Moved snow-related options into a dedicated `Snow` configuration section.
- Added `SnowInWorld` configuration series with persistent, chunk-based snowfall tracking.
- Adjusted chunk render update logic when SnowInWorld is enabled.

## 0.12.0-pre12

- Temporarily removed `RealisticSnowyChange` and broom usage for internal stability.
- Updated default snow-covered grass model to a composite overlay to avoid false positives with CTM-style mods.

## 0.12.0-pre11-3

- Added `SeasonalColorChangeExtend` config option to disable seasonal color changes for birch and selected tree types.
- Applied various internal optimizations.

## 0.12.0-pre11-2

- `season_definitions` datapack now defaults to applying to all biomes when the `biomes` field is omitted.

## 0.12.0-pre11-1

- Added `NotRainInDesert` option in `Common.Weather` to prevent rain in biomes that do not rain in vanilla (disabled by
  default).

## 0.12.0-pre11

- Addons can now define custom placement conditions and placement methods.
- Datapack structure from pre10 is partially incompatible.

## 0.12.0-pre10-1

- Added `copy_state` option to `season_definitions` placement entries.
- Added `copy_state_properties` to limit copied block state properties.

## 0.12.0-pre10

- Added `season_definitions` datapack for defining seasonal block placement behavior (debug-only by default).
- Seasonal block placement is no longer restricted to surface-only blocks.

## 0.12.0-pre9

- Refactored biome tag system into categorized layers.
- Moved Biome Rain–related tags into a dedicated rain directory.
- Simplified agro_climate and season_cycle biome tag hierarchies.
- Introduced a three-layer biome tag allocation system with automatic fallback assignment.

## 0.12.0-pre8-5

- Replaced grass snow-covered model with multivariant-based implementation.
- Removed random rotation from grass snow-covered models.
- Improved rendering performance for snow-covered grass blocks.

## 0.12.0-pre8-3

- Added optional transition textures between snow-covered and normal block states.

## 0.12.0-pre8-2

- Optimized chunk extra information lookup with multithread safety guards.
- Fixed `snow_tint` not applying in `season_textures`.
- Fixed loss of original tint data when tinting was disabled.
- Fixed incorrect transition timing when using season-only configuration.
- Optimized `eclipticseasons:season` loot condition behavior for Agro seasons.

## 0.12.0-pre8-1

- Added `eclipticseasons:weather_region` datapack for regional weather binding when Solar Weather is enabled.

## 0.12.0-pre8

- Added local season conversion support for datapacks and resource packs using `SolarTermValueMap`.
- Added top-level season mapping support for `season_definitions`, `season_textures`, and `ambient` resource packs.
- Extended `eclipticseasons:season` loot condition to support local season mapping.
- Made `seasonal_signal_durations` optional in `agro_climate` datapacks.

## 0.12.0-pre7-3

- Added dynamic cleanup for chunk extra info cache (configurable).
- Reduced memory usage during long-distance exploration.

## 0.12.0-pre7-2

- Added snow-covered texture variants to `season_textures` resource packs.
- Fixed render type inheritance for `season_textures`.
- Optimized duplicate model baking in `season_textures`.

## 0.12.0-pre7

- Snowfall timing now varies yearly within a configurable range.

## 0.12.0-pre6-1

- Optimized client-side snow definition overrides for real-time model generation.

## 0.12.0-pre6

- Added smooth intra–solar-term transitions for biome grass and foliage colors (configurable).

## 0.12.0-pre5-1

- Animals can now be affected by Season Core power without requiring greenhouse conditions.

## 0.12.0-pre5

- Optimized environmental passive humidity adjustment behavior.
- Added `eclipticseasons:volatile` tag for non-plant blocks requiring random ticks.
- Humidity control datapack now supports block state checks.

## 0.12.0-pre4-3

- Added Patchouli guide book *Seasons Chronicle*.

## 0.12.0-pre4-2

- Split biome rain and biome color tags into separate systems.

## 0.12.0-pre4-1

- Added conversion support from `sereneseasons:year_round_crops` to `eclipticseasons:all_seasons`.
- Added API method `getAgroSeason` for querying locally applied agricultural seasons.

## 0.12.0-pre4

- Adjusted built-in datapack definitions for biomes and seasons.
- Reclassified overworld biomes into Hot, Warm, and Cold regions.
- Hot regions now support all four seasons with extended summer duration.
- Reduced cold region coverage to better match player expectations.
- Updated optional local season info and calendar templates.

## 0.12.0-pre-3

- Removed built-in compatibilities and mixins for Cold Sweat, InControl, Simple Clouds, and Dynamic Trees.
- Compatibility is now provided via Ecliptic Seasons: MultiMod Patch.

## 0.12.0-pre-2-3

- Tooltip information is now displayed when a crop item tag exists without a corresponding block.

## 0.12.0-pre-2

- Added a snow overlay resource pack for vanilla plants (must be enabled in settings).

## 0.12.0-pre

- Removed built-in compatibilities and mixins for JourneyMap, Snowy Spirit, and Touhou Little Maid.
- Ecliptic Seasons now functions strictly as a core mod.

## 0.11.8.3

- Switched Distant Horizons 2.3.3b–specific optimizations to reflection-based access.

## 0.11.8.2

- Applied seasonal settings from Agro Climatic Zones to animal-related hooks.

## 0.11.8.1

- Added Jade and The One Probe support to display animal breeding season information.
- Added breeding season information to spawn egg tooltips.

## 0.11.8

- Added support for seasonal texture switching on specified models, with optional tint disabling.
- Fixed an issue where agro climatic zone data could be lost after datapack reload.

## 0.11.7.1

- Updated compatibility logic for Distant Horizons v2.3.3b.

## 0.11.7

- Added humidity smoothing to reduce abrupt transitions between adjacent solar terms.
- Fixed incorrect automatic growth condition mapping for agro climatic zones outside temperate regions.
- Added BlockState-based targeting support for Crop datapacks.
- Migrated crop tooltip logic to rely on Crop datapacks instead of tags.
- Growth failure under humidity conditions can now trigger crop death.
- Added a command to set snow coverage percentage for biome sets.

## 0.11.6

- Added optional extra snow-covered block variants (configurable, incomplete).
- Removed the built-in desert agro climatic zone.
- Simplified Overworld agro climatic zones to temperate, warmer, and colder.

## 0.11.5.1

- Fixed incorrect humidity calculation when using custom BiomeRain data.
- Optimized snow-under-tree calculations.

## 0.11.5

- Added a test datapack for seasonal block changes (`season_definitions`, disabled by default, 1.21 only).

## 0.11.4.0.1

- Added bamboo saplings to the `natural_plants` block tag.

## 0.11.4

- Added datapack-based override support for Snow Term.
- Introduced `ISnowTerm` interface for Snow Term implementations.
- Added a Cold Sweat compatibility patch for Solar Weather rain detection.

## 0.11.3

- Added a datapack for controlling seasonal prompts and log displays (disabled by default).
- Added year-based and upcoming solar-term display modes to the calendar.

## 0.11.2.1

- Fixed a server freeze caused by map cache creation.
- Adjusted internal height map calculation logic.

## 0.11.1

- Fixed an exception when loading MultiVariant models with control conditions.
- Added API methods for querying day length and weather information.
- Added `ForceBlocksNotSnowy` configuration option.

## 0.11.0.1

- Renamed `ModelManager` to `ExtraModelManager` to avoid naming conflicts.

## 0.11.0

- Stabilized custom model loading and render type handling.
- Optimized custom model loading to fully asynchronous processing.
- Added optional mod dependency checks for custom model definitions.

---

## 0.11.0-pre8

- Added capability support for Grate Humidifier and related blocks to interact with automation blocks.

## 0.11.0-pre7-5

- Added defensive handling for unsupported map mods to prevent chunk-loading deadlocks.

## 0.11.0-pre7-4

- Extended fake biome temperature adjustments to affect snowfall timing.
- Prevented biome correction logic from running on unloaded chunks to avoid server deadlocks.

## 0.11.0-pre7-2

- Added optional behavior to prevent bone meal consumption on failed use.

## 0.11.0-pre7-1

- Added optional failure messages when bone meal use does not succeed.

## 0.11.0-pre7

- Added API methods for querying seasonal availability and daytime length per dimension.
- Made the working radius of Season Core blocks configurable.

## 0.11.0-pre6-1-1

- Expanded API support for querying weather state and humidity values.

## 0.11.0-pre6

- Replaced hard failures with warnings when dynamic registries are accessed incorrectly.

## 0.11.0-pre5-3

- Adjusted default culling behavior for snowy models with configurable overrides.
- Added an API base class `AbstractModelDefinitionProvider ` to support automated model generation.

## 0.11.0-pre5

- Added item, armor, enchantment, and mob effect tags related to heatstroke resistance.
- Changed greenhouse working radius calculation to use Box Distance by default.
- Changed humidity control behavior to no longer consume sponges by default (datapack configurable).

## 0.11.0-pre4

- Added support for replacing seasonal particle rendering when assets are provided.

## 0.11.0-pre3

- Added transition models to support fine-grained block appearance changes.
- Added Box Distance option for greenhouse range calculation.

## 0.11.0-pre2

- Added seasonal and snow-covered model replacement support for Embeddium.

## 0.11.0-pre

- Added data-driven support for customizing snow-covered block models via datapacks.
- Added datapack support for seasonal block model variants based on biome and season.
- Added datapack-defined fallen leaves particle effects for blocks.
- Added datapack-configurable seasonal ambient sound support.
- Changed humidity control block behavior to allow not consuming the contained block.

## 0.10.11

- Added JEI integration for the humidity adjustment system.
- Introduced the `eclipticseasons:volatile_plants` tag to support forced seasonal random ticks.
- Improved stability of humidity adjustment effects when blocks are removed mid-process.

## 0.10.10

- Allowed hygrometers to be placed on walls.
- Reorganized weather initialization configuration to improve early-game seasonal experience.
- Adjusted the default initial solar term to reduce early spring snow in survival gameplay.

## 0.10.9

- Added block snowy-state display support in Jade and The One Probe.
- Introduced seasonal clover visuals on grass blocks during summer.
- Unified reflection behavior between snowy blocks and their base blocks when using shaders.

## 0.10.7.2

- Fixed sleep restrictions during thunderstorms.

## 0.10.7.1

- Expanded greenhouse integrity checks to include water source blocks.

## 0.10.7

- Added Jade and The One Probe integration for crop growth condition inspection.
- Introduced simplified greenhouse mode without core blocks or humidity modifiers.
- Improved greenhouse interior space detection by including water-related blocks.

## 0.10.6

- Introduced `eclipticseasons:natural_plants` for compatibility-based crop growth control.
- Added a global compatibility growth mode for non-native crop implementations.

## 0.10.5.2.2

- Added configuration option to control initial weather and snow state.

## 0.10.5.2.1

- Improved daylight cycle handling in invalid or special dimensions.

## 0.10.5.1.4

- Improved compatibility with Distant Horizons LOD rendering during season transitions.

## 0.10.5

- Reworked humidity and measurement tools to improve usability.
- Added new crop tags to support dark and specialized greenhouse growth.
- Introduced configurable greenhouse wall material restrictions.

## 0.10.4.1

- Improved crop with feature placed handling to avoid duplicate processing.
- Refined renderer update behavior after sleeping.

## 0.10.4

- Added datapack-based biome temperature and humidity control without modifying biome JSONs.

## 0.10.2.6

- Optimized Distant Horizons compatibility when forcing chunk render updates.

## 0.10.2.3

- Added Wooden Grate as a functional greenhouse block.
- Improved interaction between wind chimes and weather.

## 0.10.1

- Renamed greenhouse core blocks to clarify structure and usage.
- Refined the relationship between biome temperature, season, and humidity via API.

## 0.10.0

- Introduced season-specific greenhouse core blocks and progression mechanics.
- Added humidity regulation blocks and growth inspection tools.
- Implemented seasonal quest and reward systems.

---

## 0.9.0

- Introduced solar term change events and expanded crop growth event handling.
- Added the initial greenhouse and humidity-raising mechanics.

---

## 0.8.1

- Added JourneyMap integration.

## 0.8.0

- Introduced solar term icons and seasonal calendar.
- Added dimension filtering configuration for seasonal systems.
- Added early support for Distant Horizons rendering.

---

### 0.7.5

- Improved snow-covered rendering support for additional block models
  (e.g. fences, walls).

### 0.7.4

- Added support for seasonal crop tags from Serene Seasons.

### 0.7.0

- Snowy blocks became interactable (client-side only, not persisted).
- Fixed long-distance flickering issues with snowy blocks.

### 0.6

- Updated public API.

### 0.4

- Introduced initial API instance.
- Added seasonal sapling growth control.

### 0.3.16

- support Cold Sweat

### 0.3.14

- support Legendary Survival Overhaul

