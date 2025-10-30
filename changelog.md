### 0.12.1

- Added seasonal ritual: after crafting the Greenhouse Core Container, it can be activated with a Seasonal Prayer Scroll to begin the ritual and obtain the Seasonal Greenhouse Core. Be careful not to place it directly on the ground.
- Added a new display mode for the calendar that shows the current solar term day.
- Added optional datapacks “Rain Together” and “Snow Together” to synchronize weather across biomes, requiring configuration to be enabled.

### 0.12.0.5.1

- Prevent crashes caused by the illegal repeated triggering of chunk loading events when the Create mod's deployer uses items while SnowInWorld is enabled.

### 0.12.0.5

- When a WeatherRegion's weather subgroup contains a reference to its own Core, it will no longer copy itself repeatedly.
- Added a fallback mechanism for the sequence table corresponding to built-in Biome IDs; if a Biome does not exist, it will return the ID of the Plains biome.

### 0.12.0.4

- Upgrade Distant Horizons compatibility to version 2.3.5-b.
- Fixed an issue where, with the FrozenWater experimental feature enabled and lighting checks turned on, breaking thin ice would incorrectly trigger cracking sounds due to inverted lighting check results.

### 0.12.0.3

- Provided an optional classic-style snowy block resource pack to prevent Z-fighting issues when used together with CTM models.

### 0.12.0.2.1

- Adjusted the model settings of the Snowy Grass Block to be compatible with resource packs like Open Lower Grass.

### 0.12.0.2

- When querying a block’s snowy type, a null (empty) object is now used by default to maintain consistency.
  This also helps prevent certain Fabric mods using connectors from triggering queries at incorrect times (such as when
  a chunk is not fully loaded), which could otherwise cause chunk reload loops and server deadlocks.
- Slightly optimized particle effect rendering and generation performance.

### 0.12.0 - *First Official Release – Anniversary Edition*

> On this special anniversary, we proudly present our first official release! While there may be no significant content changes, this version sets the stage for stability and marks the beginning of a long-lasting experience. Moving forward, expect fewer frequent content updates, focusing instead on maintaining and refining what we’ve built.

- **Spring After Autumn** achievement now unlocks a commemorative reward: **Snowless Hometown**—a symbol of warmth and the passage of time.

### 0.12.0-z-3

- Now, when it’s snowing, a cauldron filled with water will turn into an ice-filled cauldron,
and a cauldron filled with powder snow will turn into a snow-filled cauldron.
- Fixed a potential world initialization crash that could occur when SereneSeasonsCropTag is enabled and an agriculture mod provides both Forge and c namespace tags for the same seed.
- Blocks containing water are no longer affected by the Frozen Water feature.
And the Frozen Water feature is now affected by light levels.

### 0.12.0-z-2

- Fixed an issue where the calendar displayed incorrectly in Next mode when it was the last solar term of the year.
- Added a more complete built-in compatibility system for crop growth conditions. Note that humidity requirements are now included; if you encounter errors, you can disable this feature in the settings.

### 0.12.0-z

- Enabled ForceCompatMode by default, since many crop-related mods do not use Forge crop events. Forced compatibility
  essentially restricts random block ticks; if issues occur (though unlikely), you may try disabling it.
- Optimized data recording and caching fields for the “Spring Gone, Autumn Come” advancement. This change invalidates
  old version data, but it improves tracking of how many seasonal cycles a player has passed, allowing for special use
  cases.
- Added a special extra_info data pack for prioritizing Ecliptic Seasons’ data packs and removing built-in
  registrations. For example, biomes can now use an independent argo climate without overriding the built-in argo files.
  This applies to all mod-provided data pack registrations; please report if you find cases where it doesn’t.
- The FrozenWater feature now requires SnowyWinter to be enabled first, keeping it consistent with existing seasonal
  mechanics.
- Added temporary caching to server-side global weather parameter calculations when Solar Weather is enabled.
- Optimized the snow reflection parameter object during Iris loading, now aligned with the snow block rather than the
  snow layer, fixing distant transparency issues in certain shaders (e.g., Complementary Shaders Unbound).

## 0.12-preview

#### Core Mod & Compatibility

- Moved non-core Mixin compatibility to the Multi Patch mod; Ecliptic Seasons will be developed as the core seasonal
  framework mod.
- Optimized compatibility with DH.
- When other mods query global weather, it now returns a player-location-based voted result instead of a fixed null
  value, improving compatibility, especially in single-player.

#### Gameplay Mechanics

- Creative mode players are no longer affected by heatstroke.
- Animals are now affected by seasons according to biome and can benefit from the seasonal - core without requiring a
  greenhouse.
- Now allows setting weather to clear when players wake up.
- Added experimental feature FrozenWater, which renders a thin layer of ice on water surfaces after snow.

#### Crops & Agriculture

- Items without corresponding blocks will still display crop growth condition information.
  Optimized growth rates for some crops.
- Added agricultural season query methods to the API.
- Added climate parameters to some resource and data packs to specify agricultural climate zones under seasonal
  conditions.
- Added eclipticseasons:crops/unaffected_by_seasons and eclipticseasons:crops/unaffected_by_humidity tags to remove
  certain mod-added seasonal crop labels.

#### Biomes & Climate

- Surface biome checks are no longer limited by chunk loading status, preventing deadlocks when querying biomes outside
  the loaded area.
- Optimized agricultural climate zones: Overworld biomes are now divided into cold, warm, and hot regions, with
  independent seasonal update prompts and more reasonable biome color transitions.
- Biome tags are now categorized into biome weather, biome colors, and biome agricultural season zones, allowing
  individual customization.
- Added yearly snow timing variation.
- Added a weather_region data pack for sharing weather status among certain sub-biomes.
- Added NotRainInDesert setting to ensure certain vanilla dry biomes remain rain-free, - accommodating mods that
  generate sandstorm weather.
- Now allows setting a snowline to control at what height snowy block always appears.

#### Seasons & Visuals

- Added smoother seasonal transitions for biome colors.
- Added SnowInWorld setting, enabling interaction with snow in the world.
- Added experimental snowy edge feature for smoother transitions between snowy blocks and regular blocks.
- Added more dimensional support for biome color data packs.
- Added the season_definitions data pack to allow blocks to experience actual seasonal changes, with code-based
  extensibility.

#### Resources & Data Packs

- Added an extra snow-covered resource pack, which can be enabled in settings.
- Optimized the built-in data pack tag system to reduce the need for pack authors to set replace when assigning biome
  tags.
- Reorganized and standardized some configuration options.

#### Guides & Documentation

- Added the "Seasons Chronicle" Patchouli handbook.

#### Performance & Optimization

- Optimized biome and chunk height map caching.
- Significantly optimized rendering performance.

#### Special Thanks

- Special thanks to Beishanwei and Orangesoda for their special authorization to carry and adapt the song “Snowless
  Hometown” in this mod.

## 0.11

- Added seasonal texture switching for models with optional tint disabling, allowing visual changes according to the
  current season. Add Biome Color resource pack for customizable biome color changes.
- Introduced seasonal loot conditions, crop growth conditions with block state checks, animal breeding seasons, bee
  hibernation mechanics, and fishing control. Added Jade and The One Probe support for displaying breeding season
  information.
- Balanced crop growth and climate simulation: humidity smoothing to reduce boundary effects, adjusted greenhouse core
  working range, and improved crop growth mapping for non-temperate regions.
- Improved snow coverage: realistic snowy changes, expanded snow-covered block support, better handling of bamboo and
  leaf blocks, and synchronized snow appearance across players.
- Refined rainfall and humidity calculations on both client and server, preventing errors from unloaded chunk queries.
- Animals now have seasonal breeding periods and behaviors.
- Enhanced API and configuration options: query weather, daylight duration, precipitation, snow coverage, and force
  blocks to not be snowy.
- Overall improvements: fixed legacy issues, enhanced stability for singleplayer and multiplayer, and extended support
  for data packs and resource packs.

## 0.11-preview

- Custom Seasonal & Snow-Covered Models: Define seasonal block variants and snow-covered blocks using model_definitions
  and snow_definitions. Supports smooth seasonal transitions, biome-specific appearances, and optional model
  replacement.
- Particles & Ambient Sounds: Fallen leaf particles customizable per block, season, biome, texture, color, and weight;
  ambient sounds configurable by time, season, biome, and rainfall.
- Seasonal Greenhouses & Cores: Configurable Season Core radius, adjustable or disabled particle effects, and Box
  Distance (Manhattan-style) calculation for greenhouse range.
- Hygrometer Improvements: Easier detection and real-time greenhouse readings.
- Grate Humidifiers: Interact with other blocks like hoppers, support JEI integration, and can optionally avoid
  consuming source blocks.
- Humidity System Optimization: Expanded effective sponge range and simplified configuration via data packs.
- Visual & Rendering Enhancements: Correct overlay of snow-covered and seasonal models, new transition models for
  granular block appearances, improved OptiFine compatibility, and slight rendering performance optimizations.
- Miscellaneous Enhancements: Mitigated map mod and chunk-loading issues, client-side FlowerOnGrass option retained, and
  added logging hints for debugging resource packs and models.

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
- Added config for valid seasonal dimensions
- Added support for DH
- Added support for Journeymap
- Added config to adjust crop growth probability when humidity is unsuitable
- Provided dedicated temperature modification configs for Cold Sweat and Legendary Survival Overhaul

## 0.7

- Interactable snowy blocks (client-side effect), thin snow layer disappears when snowy block is broken
- Expanded snow-covered support (fences, walls, more models)
- Config options: dynamic daylight duration, light controls snow cover, enhanced seasonal rendering refresh, hide crop
  planting info
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

