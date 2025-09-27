### 0.12.0-z

- Enabled ForceCompatMode by default, since many crop-related mods do not use Forge crop events. Forced compatibility essentially restricts random block ticks; if issues occur (though unlikely), you may try disabling it.
- Optimized data recording and caching fields for the “Spring Gone, Autumn Come” advancement. This change invalidates old version data, but it improves tracking of how many seasonal cycles a player has passed, allowing for special use cases.
- Added a special extra_info data pack for prioritizing Ecliptic Seasons’ data packs and removing built-in registrations. For example, biomes can now use an independent argo climate without overriding the built-in argo files. This applies to all mod-provided data pack registrations; please report if you find cases where it doesn’t.
- The FrozenWater feature now requires SnowyWinter to be enabled first, keeping it consistent with existing seasonal mechanics.
- Added temporary caching to server-side global weather parameter calculations when Solar Weather is enabled.
- Optimized the snow reflection parameter object during Iris loading, now aligned with the snow block rather than the snow layer, fixing distant transparency issues in certain shaders (e.g., Complementary Shaders Unbound).

### 0.12.0-pre19-1-2

- Fixed a deserialization error in the Crop datapack’s NbtPredicate, which could cause crop requirements to be displayed incorrectly on the client.

### 0.12.0-pre19-1

- Fixed an issue where deserialization of specified seasons in the animal class configuration file was not working.

### 0.12.0-pre19

- Fixed a crash that occurred when entering a dimension without seasons while SnowInWorld was enabled.

### 0.12.0-pre18-1

- Added the option FrozenWaterBreakable, which can be used to disable the ability for entities to break thin ice layers.
- Added compatibility between the DH mod and FrozenWater.
- Temporarily removed the restriction that prevented FrozenWater from being used when SnowInWorld is enabled.

### 0.12.0-pre18

- Added support for water_colors, water_fog_colors, fog_colors, etc. in Biome Color resource packs, allowing seasonal
  variation adjustments.
- Added null-safety checks when querying BiomeDataVersion related to levels, to prevent null pointers in special mod
  environments (e.g., when the Level does not exist during chunk loading or no corresponding cache is available).
- Added a preview feature option FrozenWater: rivers will form a thin ice layer during snowfall, which breaks when
  entities step on it. (WIP — since Water blocks have no Snow Type, this will have no effect when SnowInWorld is
  enabled.)

### 0.12.0-pre17-1-4

- When the player is not holding Shift as the sneak key, modify the crop tooltips in Jade or TOP.

### 0.12.0-pre17-1-3

- Added a new option `ClearAfterSleep`.

### 0.12.0-pre17-1-2

- Add tag `eclipticseasons:crops/unaffected_by_seasons`,`eclipticseasons:crops/unaffected_by_humidity` to remove the
  crop info of some blocks at the end of data build.

### 0.12.0-pre17-1

- Add a sky_colors field to the biome color resource pack, which can be used to modify biome colors.

### 0.12.0-pre17

> This version mainly organizes configuration names and some comments.
> Please pay attention to the updates and adjustments.

**Particle**

- SeasonParticle → SeasonalParticles
- butterflySpawnWeight → ButterflySpawnDelay
- FallenLeavesDropWeight → FallenLeavesDropDelay
- FireflySpawnWeight → FireflySpawnDelay
- WildGooseSpawnWeight → WildGooseSpawnDelay

**Renderer**

- TopFaceCulling → CullTopFaceWithSnow
- UseVanillaSnowCheck → UseVanillaLightCheckForSnow

**Debug**

- SnowyEdges → SmoothSnowyEdges
- MinChunkCompileWaringTime → MinChunkCompileWarningTime

**Snow**

- StepMelt → SnowStepMelt
- SnowyUnderSnowLike → SnowCoverUnderBlocks

**Crop**

- DarkGreenhouseFailChance → LowLightGreenhouseFailChance

**Animal**

- EnableCoreWork → SeasonCoreAffectsAnimals

### 0.12.0-pre16

- Added configurable biome snow lines.
- Removed `isRainingAtBiome` and `isSnowingAtBiome` from class `WeatherManager`, as they are no longer compatible with
  the current version’s mechanics.
- Introduced additional data serialization tag caching, significantly reducing serialization and deserialization
  overhead, especially for chunk surface biome caches.
- Split chunk snow status into two separate additional data groups: snow records and weather status, optimizing
  serialization overhead when `SnowInWorld` is enabled and it is raining.

### 0.12.0-pre15-1

- Fixed an issue where the snow flag could not be set on blocks by SnowDefinition datapack in certain cases.

### 0.12.0-pre15

- Optimized the tick calculation multiplier for weather.
- The Snowy Edge model is no longer wrapped as a snowy model to avoid conflicts with the TopFaceCulling setting.
- Fixed the issue where Enum2ObjectMap did not implement link traversal and the equals method.
- Fixed the issue where a single Block object could not apply multiple SnowDefinition data packs for splitting.

### 0.12.0-pre14-1

- Fixed #89 _Rare random rendering crash when snowy edge is enabled_
- Removed a deprecated option: Client.Render.snowUnderTree.

### 0.12.0-pre14

- Slightly improved chunk rendering performance.
- Optimized snowy models of the Custom type, enhancing handling of slopes and other irregular surfaces.
- Refined global weather queries and internal function calls: when other mods attempt to call vanilla weather functions
  while Solar Weather is enabled, the system will now perform checks based on the areas around players instead of
  returning null. If no players are present, the spawn point parameters will be applied. For mods that set weather
  parameters for specific levels, the latter will be applied to all biomes where rain can occur.

### 0.12.0-pre13-3-1

- Set the default start time to the Spring Equinox.
- Optimized the way the 1.20 client receives additional chunk packets: if the corresponding chunk is not loaded, the
  effect is applied with a delay. Some mods may interfere, causing the additional chunk data to arrive at the client
  before the initially sent chunk data, which can prevent the optimization from being applied correctly (this may result
  in client lag when loading new chunks due to repeated biome queries, and SnowInWorld may not take effect).

### 0.12.0-pre13-3

- Fixed an issue where the Ice Wand could not forcibly prevent certain chunks from being covered with snow.
- Optimized client-side rendering: when SnowInWorld is enabled, fixed a rare case where block updates could briefly go
  out of sync.
- Optimized the StepMelt feature to make it more performance-friendly and with more stable detection conditions.
- Fixed an issue where negative surface heights could cause incorrect random tick calculations in additional chunks.

### 0.12.0-pre13-2

- Optimized broom usage: when clearing snowy grass, it now removes the snow status of grass plant directly instead of
  affecting the grass block underneath.
- Added the Snow → StepMelt setting: when SnowInWorld is enabled, creatures stepping on snowy blocks now have a chance
  to remove the snow cover, revealing paths in the snow.

### 0.12.0-pre13-1

- Fixed an issue where the game could fail to load when SnowInWorld was disabled due to empty network packets being sent
  incorrectly.
- Other minor optimizations.

### 0.12.0-pre13

- Slightly optimized the issue where snow cover rate was affected by the number of biomes.
- Fixed an issue where disabling Solar Weather and enabling vanilla snowing might not work.
- Fixed an issue where, with Solar Weather enabled, the vanilla precipitation update mechanism was not applied to
  surface biomes.
- Moved several snow-related options from the **Season** section into a dedicated **Snow** section.
- Relocated the **EnableSeasonDefinition** option from the **Debug** section to the **Season** section.
- Removed the **SnowyFullCollisionShape** configuration due to compatibility issues, as it can now be fully replaced
  with datapacks.
- Added the **SnowInWorld** configuration series, which stores actual snowfall in chunks and updates with chunk ticks,
  providing precise behavior. Brooms will work under this configuration. You can also clear snowfall states via block
  updates — for example, to
  create a clean boulevard.
- When **SnowInWorld** is enabled, if only **ForceChunkRenderUpdate** is enabled but **EnhancementChunkRenderUpdate** is
  not, the mod will no longer attempt to refresh the chunk, since chunk updates are now more targeted.

### 0.12.0-pre12

- For the sake of code structure and data stability, `RealisticSnowyChange` and broom usage have been temporarily
  removed. As a result, we no longer need to intervene in the internal implementation of the lighting engine.
- The biome caching mode has been adjusted to stay closer to 1.21, with the missing capability acquisition part from
  Forge now supplemented.
- Further optimizations have been made to the conversion between BlockState and Block Snowy Type queries.
- The default snow-covered grass block model has been adjusted to a composite overlay model, in order to avoid false
  positives from Fusion-style connected-texture mods that rely on model detection.
- Internal use of EnumMap has been revised wherever possible in favor of our custom implementation Enum2ObjectMap.
- Fixed an issue when rendering extra models, might not have correctly applied precise offset position checks.
- Special thanks to Beishanwei and Orangesoda for their special authorization to carry and adapt the song “Snowless
  Hometown” in this mod. Due to scheduling constraints for the arrangement, the acquisition method is not open at this
  time.

### 0.12.0-pre11-4

- Fixed a bug where snow fall but blocks within view were not covered with snow. This issue may have been introduced
  since version 0.12.0-pre11-1.
- Fixed a bug where textures could be missing from the season_textures resource pack.
- Optimized object lookup when sending data with the broom and ice wand.

### 0.12.0-pre11-3

- Added a config option `SeasonalColorChangeExtend` to disable seasonal color changes for birch and other two trees.
- Backported wind chimes and pinwheels from 1.21 to 1.20.
- Various other optimizations.

### 0.12.0-pre11-2

- Optimized several configuration options related to seasonal animal behaviors.
- For the season_definitions datapack, if the biomes parameter is not set, it will now default to applying to all
  biomes.
- The two deprecated configuration options, CropGrowChanceInWrongSeason and CropGrowChanceInWrongHumidity, have been
  removed.

### 0.12.0-pre11-1

- Added a `NotRainInDesert` option in `Common.Weather`  to prevent rain in desert and other biomes where it does not
  rain in vanilla game. It is disabled by default and must be enabled manually.

### 0.12.0-pre11

⚠️ **Note:** This version is not fully compatible with the `season_definitions` datapack structure from **pre10**,
except for the basic place methods.

- The main change in this release is a complete rewrite of datapack extensibility.
  It now allows addons to introduce their own placement condition checks and placement methods.

### 0.12.0-pre10-1

- Added the `copy_state` field to objects inside the place section of the `season_definitions` datapack, which
  determines whether the old block state should be copied to the new one.
- Added the `copy_state_properties` field to restrict the range of properties to be copied.

### 0.12.0-pre10

- Added the `season_definitions` datapack, which is used to define the actual seasonal block changes. Note that since
  this
  datapack affects the world, it is currently placed under the Debug configuration and must be enabled before use.
- Seasonal block models are no longer restricted to surface-only placement.
- Various other optimizations.

### 0.12.0-pre9

**Bug Fixes:**

- Fixed an issue where disabling Solar Weather caused infinite biome precipitation queries. This bug was unintentionally
  introduced in version 0.12.0-pre8-3.

**Changes / Improvements:**

- Refactored the biome tag system: old biome tags have been reorganized into categories. Tags affecting Biome Rain have
  been moved to the rain directory.
- Added new tags for Overworld Agro Climatic biomes and simplified nested tags used in `agro_climate` and `season_cycle`
  data packs.
- The biome tag system for the mod now uses a three-layer structure. Biomes can be assigned tags directly without
  `replace` field. Unassigned biomes are automatically allocated via internal logic.
- Removed the built-in demo snow-covered oak leaves block definition.

### 0.12.0-pre8-5

- Fixed an issue where level 0 light checks did not support custom snow-covered blocks.
- Changed the built-in snow-covered model for grass blocks to use Multivariant instead of the demo MultiPart model. It
  also no longer applies random rotation. Variant-type models require fewer checks and combinations, resulting in faster
  rendering.
- Removed the built-in client resource pack override file for the snow-covered cobblestone definition.

### 0.12.0-pre8-4

- Backported the extra rendering acceleration from 1.21 to 1.20, greatly reducing rendering performance loss.
- Currently supports three renderers: Minecraft, Embeddium, and OptiFine.

### 0.12.0-pre8-3

- Added transition textures between snow-covered blocks and normal blocks, which can be enabled in the Debug options.
- Introduced chunk surface biome cache to speed up biome queries.
- Other miscellaneous optimizations.

### 0.12.0-pre8-2

- Now, if a mod tries to query biome precipitation using the raw method instead of ours, corrections will only be
  applied when the biome is a small biome, with an added config option to disable this behavior. At the same time, when
  completing the Level parameter, assignment will be done based on threads (only in 1.20) to reduce related queries to
  ServerLevel, since server level involves slower chunk lookups, especially in single player mode. However, we also need
  to properly distinguish between mods attempting to query biome precipitation via `getPrecipitationAt` for chunk
  generation or chunk ticks, and those related to client-side weather effects.
- Optimized chunk extra information lookups by adding some optimistic limits against multithreaded access to prevent
  null pointers or out-of-bounds queries caused by thread synchronization, while minimizing performance loss.
  Additionally, specifically optimized client queries for faster lookups.
- Fixed an issue where the `snow_tint` parameter in the `season_textures` resource pack was not working.
- Fixed an issue where disabling tinting caused the original model to lose its tint data.
- Fixed an issue where, when using season only, the transition parameters in the `season_definitions` and
  `season_textures`
  resource packs were not applied exclusively at the start of a season as intended.
- Optimized the loot condition `eclipticseasons:season`: if only the season parameter is provided and no fixed season
  type is specified via climate, it will now apply the Agro season.

### 0.12.0-pre8-1

**Description:**
A new `eclipticseasons:weather_region` datapack has been added. When **Solar Weather** is enabled, it allows you to bind
similar biomes to share the same weather, reducing fragmented biome transitions.

**Example JSON:**

```json
{
  "core": "minecraft:forest",
  "sub": [
    "minecraft:flower_forest",
    "minecraft:dark_forest",
    "minecraft:birch_forest"
  ]
}
```

* `core` specifies the main biome ID.
* `sub` is a list of associated biomes (as `HolderSet`).
* You can also use the optional `priority` parameter (integer) to adjust application order, preventing conflicts when
  using tags.

**Fix**

Fixed an issue in single-player mode where the weather server logic incorrectly used client-side biome variables, which
may cause unexpected errors.

### 0.12.0-pre8

- Fixed an issue where the `snow_tint` parameter in the `season_textures` resource pack was not working.**
- Added local season conversion support for all data packs and resource packs using the `SolarTermValueMap` structure,
  including `biome_colors`, `particles/fallen_leaves` resource packs, and `biome_climate_setting`, `season_cycle`,
  `biome_rain` data packs. When initialized with the `seasons` parameter, you can adjust season mappings to more
  accurate intervals by using entries such as `"climate": "eclipticseasons:cold"` (AGRO).
- Provided the same parameter for `season_definitions`, `season_textures`, and `ambient` resource packs. Note that this
  is a top-level parameter.
- The same parameter is also supported for the loot condition `eclipticseasons:season`. Note that it is a sub-parameter
  under `require`.
- For the `agro_climate` data pack definition, `seasonal_signal_durations` does not need to be provided. If omitted, it
  defaults to `none`.

### 0.12.0-pre7-3

- Fixed an issue where the annual snowfall duration parameter was not saved when unloading levels.
- Added a dynamic clean-up mechanism for the chunk extra info cache, which can be disabled in Debug configuration.
- Removed the peer access lock from the chunk extra info cache.
- These two changes will save long-distance explorers (players who travel across hundreds or thousands of blocks)
  100–200 MB or more of memory usage.
- Backported the multidimensional chunk info caching method from 1.21 to 1.20, improving server-side query efficiency
  and fixing potential cache errors.
- Biome information is no longer cached immediately on chunk load. Instead, results are stored after random access,
  preventing potential stutters caused by instant caching.

### 0.12.0-pre7-2

- Added snow-covered variants to the `season_textures` resource pack.** You can now prefix the original key with `snow_`
  to use them. Additionally, if no specific season is set, the textures will apply to all seasons.
  Example:

  ```json
  {
    "target": [
      "biomeswevegone:block/amaranth"
    ],
    "slices": [
      {
        "snow_textures": [
          {
            "cross": "biomeswevegone:block/snowy_amaranth"
          }
        ]
      }
    ]
  }
  ```

- Fixed an issue where `season_textures` resource packs could not inherit the render type from the original model JSON.
- Optimized the duplicate baking issue of identical models in `season_textures`.
- Adjusted the required crop quantities for seasonal tasks.

### 0.12.0-pre7-1

- Optimized the annual snow timing variation calculation method to improve central stability.
- Optimized the surface biome caching mechanism, as well as buffering operations involving small biomes such as rivers,
  removing server-side cache restrictions on border chunks.
- Removed restrictions on querying or correcting surface biomes in border or unloaded chunks, improving result for
  weather- and rainfall-related operations such as ticking and mob spawning in these.

### 0.12.0-pre7

- The snowfall timing now varies within a certain range each year and can be adjusted via configuration toggle or
  command. Changes to the Snow Term datapack can be referenced from the built-in example.

### 0.12.0-pre6-1-0-1

- Due to limitations of the Forge configuration system in 1.20, the Extra Snow resource pack is now forced to load;
  however, the actual content loaded into the game still depends on the configuration.

### 0.12.0-pre6-1

- Moved Resource configuration from Common Config to Start Config to avoid cases where some mods cause the built-in
  resource pack to register too early, preventing configuration access (1.21 only).
- Optimized client-side snow definitions override, allowing the use of custom flag for real-time model generation during
  rendering.

### 0.12.0-pre6

- Added smooth transitions within solar terms for biome grass and foliage colors, which can be disabled in settings.
- Other performance improvements and optimizations.

### 0.12.0-pre5-1

- Animals can now benefit from the power of the Season Core, even though they do not require a greenhouse for breeding.
- Environmental humidity modifier structures can now be set to save along with chunk data.

### 0.12.0-pre5

- Optimized the environmental passive as a humidity adjustment mechanism, now by default adding magma bubble columns
  that can randomly increase humidity. Developers can refer to the wetter datapack for details.
- Added the `eclipticseasons:volatile` tag to mark non-plant blocks that require random ticks to be enabled (can be used
  in conjunction with environmental passive adjustment).
- The humidity control datapack now also supports state checks, similar to the crop datapack.

### 0.12.0-pre4-3

- Added a new Patchouli guide, the _Seasons Chronicle_, craftable with seeds and a book.
  It provides a brief introduction to the mod’s seasons, agriculture, regional differences, and explanations of certain
  terms.
- Cauldrons, honey blocks, slime blocks, and composters are no longer treated as blocks that snow can pass through by
  default.

### 0.12.0-pre4-2

- Split the tags used for Biome Rain and Biome Color (these can also be customized via resource packs or data packs).
- Adjusted the default seasonal biome colors and biome rain for cold and hot regions.

### 0.12.0-pre4-1

- Enabling the EnhancementChunkRenderUpdate option while using DH may cause slow generation of DH fake chunks. This
  potential issue has been optimized.
- Added registration for crop data converting `sereneseasons:year_round_crops` tag to `eclipticseasons:all_seasons`.
- **Developer related:**
    - Renamed `useDefaultValue` field in `CommonConfig$Crop` to `registerCropDefaultValue` for consistency with 1.21.
    - Added API method `getAgroSeason` to query the locally applied season based on agricultural zones, avoiding biome
      characteristics being overlooked due to global season limitations.

### 0.12.0-pre4

**Main Changes:**

- Adjustments to the **default built-in data pack**, especially biome and seasonal definitions.
  Feedback is welcome.

**Biome Classification:**

- Replaced “Tropical / Temperate / Cold” zones with **Hot / Warm / Cold regions** for overworld biome classification.
- **Hot** regions no longer defaults to eternal summer. They now feature **all four seasons**, with **summer lasting
  over half the cycle**.
- **Cold region coverage reduced**. Upper threshold lowered — e.g., *Windswept Forest* is no longer considered cold — to
  **reduce player confusion**.

**Seasonal Info (Optional Setting):**

- When `EnableLocalInfoAndCalendar` is enabled:

    - **New inform content** added for **Hot regions**.
    - **Desert, Jungle**, and similar biomes **no longer have custom informs/calendar** — they now follow **Hot region**
      templates.

#### 0.12.0-pre-3-1-3

- Fixed an issue where using RegisterCropDefaultValue would add crop information to all blocks.

#### 0.12.0-pre-3-1

- Surface biome lookup no longer depends on neighboring chunk load state.

### 0.12.0-pre-3

- Mixins or compatibilities for Cold Sweat, InControl, and patch for Dynamic Trees are no longer included. Please use
  Ecliptic Seasons: MultiMod Patch instead.

#### 0.12.0-pre-2-4

- Fixed excessively low growth rates for `eclipticseasons:crops/all_seasons` tagged crops during winter.

#### 0.12.0-pre-2-3

- Now if an item crop tag is added without a corresponding block, the tooltip information will also be displayed.

#### 0.12.0-pre-2-2

- Fixed a crash that could occur when biome registries were modified during world reload, due to outdated
  deserialization mappings.

#### 0.12.0-pre-2-1

- Added gameplay tips for some greenhouse-related items to help players understand how to build a proper greenhouse.

### 0.12.0-pre-2

- Completed the additional snow overlay resource pack for vanilla plants, which needs to be enabled in the settings.
- Added crafting recipes for the Seasonal Greenhouse Core. It is now also possible to extract the essence from the core.

### 0.12.0-pre-1

- Move mixins for Simple Clouds to Ecliptic Seasons: MultiMod Patch.
- Players in creative or spectator mode will no longer suffer from heatstroke.

### 0.12.0-pre

- Mixins or compatibilities for JourneyMap, Snowy Spirit, Haunted Harvest, and Touhou Little Maid are no longer
  included. Please use
  Ecliptic Seasons: MultiMod Patch instead.
  Ecliptic Seasons will now serve solely as a core mod.
- The default value of `Realistic Snowy Change` is now set to false; the functionality remains unchanged.

## 0.11

- Added seasonal texture switching for models with optional tint disabling, allowing visual changes according to the current season. Add Biome Color resource pack for customizable biome color changes.
- Introduced seasonal loot conditions, crop growth conditions with block state checks, animal breeding seasons, bee hibernation mechanics, and fishing control. Added Jade and The One Probe support for displaying breeding season information.
- Balanced crop growth and climate simulation: humidity smoothing to reduce boundary effects, adjusted greenhouse core working range, and improved crop growth mapping for non-temperate regions.
- Improved snow coverage: realistic snowy changes, expanded snow-covered block support, better handling of bamboo and leaf blocks, and synchronized snow appearance across players.
- Refined rainfall and humidity calculations on both client and server, preventing errors from unloaded chunk queries.
- Enhanced API and configuration options: query weather, daylight duration, precipitation, snow coverage, and force blocks to not be snowy.
- Overall improvements: fixed legacy issues, enhanced stability for singleplayer and multiplayer, and extended support for data packs and resource packs.

## 0.11-preview

- Custom Seasonal & Snow-Covered Models: Define seasonal block variants and snow-covered blocks using model_definitions and snow_definitions. Supports smooth seasonal transitions, biome-specific appearances, and optional model replacement.
- Particles & Ambient Sounds: Fallen leaf particles customizable per block, season, biome, texture, color, and weight; ambient sounds configurable by time, season, biome, and rainfall.
- Seasonal Greenhouses & Cores: Configurable Season Core radius, adjustable or disabled particle effects, and Box Distance (Manhattan-style) calculation for greenhouse range.
- Hygrometer Improvements: Easier detection and real-time greenhouse readings.
- Grate Humidifiers: Interact with other blocks like hoppers, support JEI integration, and can optionally avoid consuming source blocks.
- Humidity System Optimization: Expanded effective sponge range and simplified configuration via data packs.
- Visual & Rendering Enhancements: Correct overlay of snow-covered and seasonal models, new transition models for granular block appearances, improved OptiFine compatibility, and slight rendering performance optimizations.
- Miscellaneous Enhancements: Mitigated map mod and chunk-loading issues, client-side FlowerOnGrass option retained, and added logging hints for debugging resource packs and models.

## 0.10

### Seasonal Crop System

* Fully redesigned seasonal greenhouse mechanics.
* **Greenhouse Cores** added; crops must be within core radius to receive growth bonuses.
* **Humidity Regulator Blocks** allow localized moisture control; effects stack when multiple blocks are nearby.
* Simple greenhouse mode supported (without cores or humidity modifiers).
* Crops thriving in dark conditions now recognized (`dark_grow_plants` tag).
* **Growth Detector** added to monitor crop progress.
* Seasonal questline added; completing quests rewards greenhouse cores and materials.
* Achievements and progression system overhauled for clearer guidance.
* New plant/crop tags: `volatile_plants`, `natural_plants`, `dark_grow_plants`.
* Compatibility mode can be forced for crops, supporting mods without native Forge/Fabric event support.

### Other Optimizations

* Hygrometer can be wall-mounted, displays local humidity, and outputs redstone signal.
* JEI support for humidity adjustment module.
* Jade / The One Probe displays crop and snowy block states.
* Map color modification can be disabled to avoid conflicts with other mods.
* Optimized chunk info caching and LOD updates to reduce performance impact.
* Calendar placement improved for diagonal surfaces.
* Multiplayer data pack synchronization improved for better 1.21 backport compatibility.
* Game now starts at **Spring Equinox** for a more natural initial climate.
* Seasonal visual updates: snow on winter leaves, clovers on summer grass, etc.


## 0.10-preview

- Added climate detection meters at player position
- Support for Snowy Spirit (skiing, Christmas holiday)
- Support for Haunted Harvest (Halloween holiday)
- Support for InControl to check four seasons
- Greenhouse detection improvements
- Legacy snow/melting mechanics as optional config
- Agricultural climate zones for crop growth (cold, temperate, hot, desert, nether, end), with datapack support
- Fake player and maid support for cleaning snow overlays
- Initialization of biome-related weather data at world start or player join
- Greenhouse configuration options
- SolarDay can be negative (BCE-style calendar)
- Fire resistance grants immunity to heatstroke
- Increased ambient temperature threshold for heatstroke
- Improved dimension and weather interaction with more stable configs

## 0.9

- Added two events (solar term change event, crop info modification/registration event via API)
- Added greenhouse mechanism to protect crops from seasonal and humidity changes
- Added humidity raising mechanism (bubble columns with magma blocks, rain in open air)

## 0.8

- Added solar term icons
- Added calendar (with optional message on use) and related advancements
- Added config for valid  seasonal dimensions
- Added support for DH
- Added support for Journeymap
- Added config to adjust crop growth probability when humidity is unsuitable
- Provided dedicated temperature modification configs for Cold Sweat and Legendary Survival Overhaul

## 0.7
- Interactable snowy blocks (client-side effect), thin snow layer disappears when snowy block is broken
- Expanded snow-covered support (fences, walls, more models)
- Config options: dynamic daylight duration, light controls snow cover, enhanced seasonal rendering refresh, hide crop planting info
- Support for Serene Seasons crop tags
- /setTerm command suggestion tooltip
- More ways to resist heat stroke (special enchantments, ice, snow)

## 0.6
- Support to disable Solar Weather and use vanilla weather

## 0.5

- More blocks support snowy effect (e.g., glass and more)

## 0.4

- Updated textures and render methods
- More leaf colors
- Added API instance
- Sapling growth control
- Updated weather check and transition

## 0.3

- Compatibility with Cold Sweat, Legendary Survival Overhaul
- Config options for crop humidity and seasonal growth chance

