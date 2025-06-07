## **0.11.0-pre5**
- Added item tags `eclipticseasons:cooling_items` and `eclipticseasons:heat_protective_helmets`, and armor enchantment tag `eclipticseasons:heatstroke_resistant`. A `eclipticseasons:heatstroke_resistant` mob effect tag is also introduced to control heatstroke behavior. You can tag your own straw hat to provide heatstroke resistance.
- Greenhouses now use Box Distance by default to determine their working radius.
- Adjusted the humidity control data pack. By default, sponges and wet sponges will no longer be consumed. Data pack authors can still configure the lasting time manually.
- Fixed an issue where the hygrometer could not detect greenhouse humidity in greenhouse in real time.
- Fixed an issue where Silk Touch enchantment could not harvest the greenhouse core block.

### **0.11.0-pre4-7**
- Fixed a potential crash that could occur when using a resource pack with custom seasons/snow cover models from Ecliptic Seasons alongside Embeddium, especially when Multipart models are involved.

### **0.11.0-pre4-6**
- The snow_definitions in the resource pack can now override the offset used during model calculation, and it will no longer skip evaluation if not defined on the data pack side.
  This allows for purely resource-pack-based visual changes for snow-covered blocks—even though, from the server's perspective, they are not actually considered snow-covered blocks.

### **0.11.0-pre4-5**
- Other mods are not supposed to call level.isRaining() frequently when solar weather is enabled , but since some might still do so, we must defensively optimize this query just in case.

### **0.11.0-pre4-4**
- Temporarily Fixed an issue where would cause that certain moving block models lose their top parts.
- Fixed an issue where the adjustment to the In Terms mechanism in version 0.11.0-pre3 prevented snowfall in cold regions.

### **0.11.0-pre4-1**
- Added logging hints for custom resources to help pinpoint errors.

## **0.11.0-pre4**
- Now the falling leaf particles allow setting `replace` to completely disable the original particle rendering, but only when the seasonal particle texture provided for the set season exists.
- The particle color string key has been changed from `sColor` to `color_string` to better align with the overall naming style. Unless someone has been digging deep into the code, it's unlikely this was being used yet.
- Snow-covered models can now correctly overlay seasonal models. If the snow-covered model has the `replace` attribute, the seasonal model will be ignored.
- Fixed a Codec decoding error in non-trivial cases within MultiPart models.

## **0.11.0-pre3**
- Added a transition_models field to the Season Model to enable more granular block appearance changes beyond just seasonal transitions.
- Added a Box Distance option, allowing greenhouse blocks to use Manhattan distance instead of Euclidean distance when calculating their effective range.

### **0.11.0-pre2-1**
- Fixed an issue where grass blocks beneath snow-leaking blocks such as walls and fences were not covered with snow.

### **0.11.0-pre2-1**
- Removed the erroneous code that incorrectly kept Ecliptic Seasons JEI plugin from loading in production environments.

## **0.11.0-pre2**

- Added support for seasonal and snow-covered model replacements for Embeddium. To disable the original model rendering, simply add an entry with replace=true in the model_definitions file.

## Ecliptic Seasons v0.11 Preview Update

> ⚠️ **Note: This is a preview update.**

The **0.11 update** brings us one step closer to the official release. We're excited to announce that **Ecliptic Seasons** has made further progress with a **dual-drive system based on data packs and resource packs**.

---

### ❄️ Custom Snow-Covered Block Types

You can now customize the **snow-covered state of blocks** via a two-step definition:

1. **Model definition file** — written in standard Minecraft BlockStates JSON format.

2. **Placement path**:

   ```
   resources/assets/<namespace>/eclipticseasons/model_definitions
   ```

   This system allows you to combine and randomize models to enhance visual richness.

For snow-covered types (which are calculated both client- and server-side), define them under:

```
resources/data/<namespace>/eclipticseasons/snow_definitions
```

At minimum, provide:

- A block ID
- A model definition file ID

Resource packs can also supply snow definitions, but with limited property control (model reference only).

> ⚠️ `model_definitions` currently have limited support for replacing vanilla models. This must be **manually enabled**, and is only available in **Minecraft 1.21 with Sodium** (support for 1.20 and Embeddium is planned). If you want to completely suppress default block rendering, you can try overriding the original block’s blockstate model definition.

---

### 🌸 Seasonal Appearance for Blocks

Blocks can now have **seasonal model variants**. To define these:

- Use the `season_definitions` directory
- Variants can be defined per **block type**, **biome**, and **season**
- Supports seamless seasonal transitions by combining models

Model files are still placed under:

```
resources/assets/<namespace>/eclipticseasons/model_definitions
```

---

### 🍂 Custom Fallen Leaves Particle Effects

You can now define **block-specific falling leaf particle effects** with rich customization:

```
resources/assets/<namespace>/eclipticseasons/particles/fallen_leaves
```

You can configure it by:

- Season
- Biome
- Block
- Weight
- Texture
- Color source
- And more

---

### 🎵 Seasonal Ambient Sounds

Ambient sound settings can now be customized in the `ambient` directory.

Options include:

- Time of day
- Season
- Biome
- Rainfall conditions
- And more

---

### 💧 Humidity Control Block Updates

For the **Grate Block**:

- Now supports an option to **not consume the source block**
- **Relaxed restrictions** on extracting contents
- Enhanced **JEI integration**, clearly showing valid blocks for construction

---

### 📖 Documentation

We will update the **Wiki** soon with more detailed instructions. In the meantime, refer to the **example files included** in this preview build.

---

Thank you for testing and supporting *Ecliptic Seasons*!

___
___
___

## **0.10.11**
- Added JEI support for the humidity adjustment module.
- Added the tag `eclipticseasons:volatile_plants` to mark blocks that require forced additional random ticks for seasonal changes.
- Adjusted the naming of certain functions and fields in `SolarDataManager` and optimized its internal query speed.
- Fixed an issue where the humidity adjustment effect might take a long time to disappear if the sponge was removed midway from the humidity adjustment block.
- Merged Pull Request [#50](https://github.com/joe-vettek/Ecliptic-Seasons/issues/50), adding Russian localization contributed by Korben.

## **0.10.10**
- The hygrometer can now be placed on walls. After a period of measurement, it will also update the humidity display and emit a redstone signal.
- The configuration option `ShouldInitWeather` has been moved from `Common.Season` to `Common.Weather`, and its default value is now disabled. If manually enabled, players may see lingering early spring snow in certain regions. This change is intended to ensure players experience a proper spring when they first log in, avoiding confusion caused by snow in springtime.
- The configuration option `InitialSolarTermIndex` has been adjusted to "Spring Equinox" instead of "Beginning of Spring." This makes the start of the game less cold when survival mods are installed and also helps reduce early spring snow in some biomes (if `ShouldInitWeather` is enabled).
- Fixed an issue where the `SnowyWinter` configuration was not properly enabled on the server side.
- Added a new configuration option `ChangeMapColor` under `Common.Map`, allowing players to disable block map color modifications. This prevents thread deadlocks caused by Fabric mods caching map colors at inappropriate times.

## **0.10.9**
- Fixed an issue in version 0.10.8 where optimized chunk info initialization cached incorrect surface height, which could cause snowy leaves to display incorrectly.
- Added snowy state display for blocks in Jade and The One Probe to help new players understand block states.
- Added clovers to grass block in summer. Their distribution changes with the solar terms.
- Most snowy block variants no longer have different reflection behavior from their base blocks when used with shaders that simulate surface reflections.

## **0.10.8**
- Optimized the initialization of chunk information caching to better adapt to certain utility or optimization mods that modify initialization behavior, which could otherwise lead to performance degradation from frequent early-phase queries.
- Refined the advancement progression system to provide clearer guidance for players when constructing greenhouses.

## **0.10.7.2**
- Fixed incorrect night brightness levels caused by adjustments from 0.10.7
- Addressed inability to sleep during thunderstorms
- Optimized thunderstorm generation mechanics

## **0.10.7.1**
- Added some missing Crop Tags
- Greenhouse integrity check now includes water source blocks

## **0.10.7**
- Fixed the issue of abnormal mob spawning during rainy weather.
- Add supports for Jade and TOP to check the growth conditions of crops.
- Allow to choose if play in simple greenhouse mode without core blocks and humidity modifiers.
- Temporarily treat water and water-containing blocks as greenhouse interior space blocks.

## **0.10.6**
- Added `eclipticseasons:natural_plants` tag to identify Blocks that require growth control via compatibility mode — typically used for mods that do not natively support the Forge Event Bus or are added via Sinytra Connector (Fabric mods).
- Added a config entry named as ForceCompatMode: forces all crops to use compatibility mode for growth control, not just those tagged with `eclipticseasons:natural_plants`. Note native support offers significantly better performance.
- Added missing tags for season quest blocks.
- Include unused but extendable Crop tags in the resource folder for easy handling.
- Refined serval English localization strings. Thanks to Korben.

### **0.10.5.2.2**
- Added a configuration option to control whether weather and snow status should be initialized.

### **0.10.5.2.1**
- Fix daylight cycle if in an invalid dimension.

## **0.10.5.2**
- Added a configuration for snowy tree .
- Fixed an issue where setting the weight too high (with a value less than 10) would cause precision errors in particle spawn probability calculation.

## **0.10.5.1.5**
- Added null checks to greenhouse core to avoid null results from incorrectly tagged biomes and then crash (e.g., overworld biomes missing "is_overworld" tag).

## **0.10.5.1.4**
- When Distant Horizons is installed and 'EnhancementChunkRenderUpdate' is not enabled, LOD rendering will no longer reset after sleeping for season update.

## **0.10.5.1.3**
- Fixed the issue where the duration of seasonal day/night cycles was not affected by valid dimensions.

## **0.10.5.1.2**
- Fixed an issue where saplings could not grow into trees via bone meal acceleration based on the growth probability when they had seasonal data but lacked humidity information.

## **0.10.5.1**
- Fixed an issue where the growth speed of crops in non-temperate area could not be properly checked when datapack information was incomplete.
- Fixed an issue where leaves crop blocks were incorrectly identified as being in a greenhouse.

## **0.10.5**
- Thermometers and hyetometers have been hidden.

To avoid player confusion regarding the concepts of thermometer and hyetometer, thermometers and hyetometers are no longer displayed by default in the creative mode inventory (including JEI). Additionally, their recipe unlock condition has been adjusted to require obtaining them at least once. Meanwhile, the recipe for the hygrometer has been modified to allow direct one-step crafting.

Thermometers and hyetometers are essentially more like debug items—ordinary players don’t need to overly concern themselves with the specific numerical values of temperature and rainfall. Players only need to adjust based on the current humidity status.

- The hygrometer can now also measure the humidity status at the player's current location, rather than just displaying the base value.
- Added the `eclipticseasons:dark_grow_plants` tag to identify crops that can quickly thrive in dark greenhouse conditions.
- Greenhouses are no longer directly set as invalid during summer noons, but instead indirectly affect humidity calculations.
- Fixed naming ambiguity: GreenHouseMaxDiameter (horizontal radius) and GreenHouseMaxHeight (vertical limit) are now properly distinguished in configs.
- Added greenhouse wall material controls, allowing the configuration of walls disliked by crops to restrict growth. In the default settings, this only affects iceblock greenhouses for non-winter crops.
- Other minor changes and fixes.

## **0.10.4.1**
- Additional processing has been applied to the ripening event to cancel consecutively triggered events. For example, when ripening a mushroom into a giant mushroom, duplicate checks will no longer occur across two consecutive events.
- The renderer update logic has been adjusted. By default, the renderer will no longer forcibly reset after sleep completion.

## **0.10.4**
- Added a biome control temperature and downfall data pack to adjust biome humidity changes without modifying the biome JSON definitions, avoiding conflicts with terrain generation control systems like WWOO, which modify temperature and downfall solely based on terrain considerations.
- Adjusted some humidity calculation methods.

## **0.10.3.1.2**
- Fixed an issue where wet sponges would be ejected from the humidifier during level reloading if they were present and the conditions below met the soft heat source activation criteria.

## **0.10.3.1.1**
- Fixed the missing translation for "Season Wall Quest Sign" that was incorrectly overwriting the "Air" entry.

## **0.10.3.1**
- Fixed an issue where crops would grow incorrectly when growth information was not fully registered.  

## **0.10.3.0.1**
- Updated the calculation logic for the color of fallen leaves.

## **0.10.3-hotfix**
- Fixed an issue where placing blocks with animated textures and with snow-covered model while using the CIT resource pack could cause the JVM to report a crash named as EXCEPTION_ACCESS_VIOLATION.
- Fixed an issue where putting a greenhouse essence into greenhouse container would not consume the item.

## **0.10.2.6**
- Optimized the LOD (Level of Detail) update mechanism in Distant Horizons' compatibility module when both ForceChunkRenderUpdate and EnhancementChunkRenderUpdate are enabled.

## **0.10.2.5**
- Fixed the inability to submit seasonal tasks requiring items beyond the stack limit.

## **0.10.2.4**
- Fixed a bug related to whether Biome has weather precipitation.

## **0.10.2.3**
- Added Wooden Grate as an alternative block.
- Fixed a potential item duplication issue.
- Optimized the placement experience of the calendar, allowing it to be placed even when clicking on diagonal surfaces.
- Improved the interaction between wind chimes and weather conditions.

## **0.10.2.2**
- Fixed an issue where greenhouse particle spawn positions exceeding boundaries could cause client crashes.
- Fixed a problem with the growth detector incorrectly responding to humidity conditions.

## **0.10.2.1**
- Fixed some secondary effects related to weather.

## **0.10.2**
- Fire resistance grants immunity to heatstroke as well.
- Added some missing translations for calendar.
- Optimized temperature calculation method.
- Optimized the interaction performance of this mod with dimensions and weather, making its configuration items more stable and reserving room for expansion.

## **0.10.1**
- Added some guidance and quests to enhance the immersion of the game.
- Rename greenhouse_core_frame as greenhouse_core_container.
- Adjusted the relationship between temperature, season and biome. Instead of modifying the fixed biome temperature of Biome, add the Level parameter through the API to bind the correct humidity relationship.

## **0.10.0.1*
- The default global probability coefficients of thunder has been reduced.

## **0.10**
## Important Notice
- Due to a critical world generation bug discovered in the 0.10-pre6 update (where snow layers in snowy biomes might fail to place in world generate stage), this major update has been released ahead of schedule.  
  If you encounter adaptation issues, I recommend using version 0.10-pre5 for now.

- **Seasonal Greenhouse Core Blocks & Items**:
  - Each season now has its dedicated greenhouse core block and corresponding items.
  - Counter-season crops will **only grow** within the working range of both the greenhouse and its **season-specific core block**.
- **Growth Detector**:
  - A new tool to inspect whether crops can grow normally and display additional growth-related information.
- **Humidity Regulator Block**:
  - Replaces the removed multiblock humidity structure (temporarily disabled due to performance concerns).
  - Right-click with a **sponfge** or **wet sponge** on copper grates to adjust humidity in a small area.
  - **Note**: Wet sponges require a heat source (e.g., campfire) beneath them.
  - Both the addtional conditions and humidity modification rules can be customized via datapacks.
- **Seasonal Quests**:
  - Craft a `Seasonal Prayer Scroll` (crafted by seeds and paper) and right-click a hanging sign block to generate a seasonal quest sign.
  - Complete quests by the end of the season to obtain `Greenhouse Core Essence`, a material required for crafting greenhouse cores.

## **0.10-pre11-2**
- Fixed an issue in the 0.10-pre6 update where biome temperature adjustments during world generation could prevent snow from generating in cold biomes in multiplayer games.
- Resolved a problem where special creature particles and related sound effects would appear during snow in some cold biomes during spring and summer.

### Additions
- Added initialization of biome-related weather data during world initialization or when joining a mod for the first time. Now, if certain biomes can snow or have just ended a snow cycle upon login, snow overlay decorations will be visible. Additionally, the probability of initial weather being sunny has been increased.
- Added two configurations related to greenhouse determination.
- `SolarDay` can now be set to a negative value, potentially offering an experience akin to BCE (Before Common Era).
- Replaced some recipe `Item` references with `Item Tag` to improve compatibility.

### Removals
- Removed redundant temperature checks in weather calculations.


## **0.10-pre11**
- Added soft sound effects to the wind chime.
- Fixed an issue with thundering weather when it's not raining.
- Adjusted greenhouses in summer so that they only become inoperable at noon, rather than throughout the day.
- Adjusted the ResourceOrTagArgument.Result used by Ecliptic Seasons when issuing the proxy weather command to be dynamically created and return a non-null value.

## **0.10-pre10-3**
- Use toLowerCase(Locale.ROOT) to avoid locale conversion.

## **0.10-pre10-2**
- fix an issue with single player world data cache being lost due to clearing the cache at the wrong time.
- fix an issue where only one match would remain when stacking multiple different CTM resources.

## **0.10-pre10**

- clear some cache which might cause memory leak on client exit or server close
- fix two ctm properties parse and detect problem

## **0.10-pre9**

- Resolved a bug in IceAndSnow(Melt) config, and related functions to ensure they meet standard expectations.
- Optimized rendering performance during heat stroke conditions to maintain a stable frame rate.

## **0.10-pre8-2**

- Test crop growth control data pack.

Now, different biomes are divided into distinct argo climatic zones, initially set as cold,
temperate, hot, desert, nether, and end. You can configure growth parameter mappings or default values as needed.
Different crops will apply various growth parameters over time based on their agricultural climate zone. Existing crop
tags still work, and you can also create your own blockset to apply different crops and climate zones. Currently, if a
crop fails to grow and a death probability is set, there is a chance it may die. Fertilization probability can also be
configured, but for now defualt is set to always succeed.

This may sound a bit complex, but the mod provides some basic conveniences. You can use the existing crop growth control
tags without having to write detailed datapacks. If you need to edit datapacks, you can flexibly use blocksets to adjust
target objects. Since different climate zones require different growth parameters, a mapping method to temperate is
provided (except for the temperate zone itself) to simplify the setup. Moreover, it is not necessary to set time down to
the solar term—configuring by season is also allowed.

## **0.10-pre8**

- Reverted a change to improve map color calculation speed for brute force map mods
- Adjust renderer of the mod so that even at Fast Graphics Quality the standard render type is still used instead of
  just solid type.
- Wind chimes will now dance in the idle.

## **0.10-pre7**

- fix leaves particles color
- test maid support to clean snow overlay
- add fake player support to clean snow overlay such as Deployer in Create

---

## **0.10-pre6**

- Fix snowy block in the area light more than limit in Embeddium
- Fix zombie would burn in some case like just snow locally in the day.
- Fix an issue with unexpected snow blocks appearing when exploring the Biome in winter.
- Added legacy snow/melting mechanics as a configurable optional feature, and they are separate. use the game rule to
  adjust the stack height. 
- If RealisticSnowyChange in client config is enabled, snow overlay that has been melted by light will no longer be
  replaced immediately, even if the light source is removed.
- Moved Snowy Winter and related options to Common Config. Now Snowy detection can be performed on both ends.
- Changed the default chunk rendering update strategy to prevent chunk rendering delays and performance impact.

---

## **0.10-pre5**

- Add support for InControl to check four seasons
- Fixed an issue where the morning sun would move instantly when the solar term was about to change.
- Based on the fix used by Sodium 0.6.9, the issue where a small number of snow-covered blocks failed to render the snow
  cover layer in incorrect sort in the Sodium environment has been fixed temply.
- Update CTM Model compatibility methods to adapt to Sodium 0.6.9.

---

## **0.10-pre4**

- Provided colored foliage particle compatibility for resource packs that change foliage textures.
- Add support for Snowy Spirit, let enjoy skiing and Christmas Holiday

---

## **0.10-pre3**

### Experimental Updates:

- Testing agriculture datapack system.
- Testing CTM model compatibility.

### Minor Updates:

- Optimized the detection logic for greenhouses, enhancing detection speed and accuracy for more efficient performance.
- Fix a crash when Embeddium working as chunk renderer render snowy vine

---

**0.10-pre2**

### Experimental Updates:

-Built new looks for snowy trees and vines.

---

**0.10-pre**

### Critical Fixes:

- Fixed an issue where certain mods could not obtain correct rainfall prediction information when using biome queries.
  This was due to the lack of Level input and no interaction with the mod. These mods will now receive query results
  related to the Overworld.
- Fixed an issue regarding the run of solar terms system when informs are disabled.
- The light check of handheld maps for snow-covered blocks is temporarily disabled to avoid erroneous calls that could
  cause server disconnects.

### Improvements:

- Refactored and organized the registration-related code due to the increasing amount of registration information.
- When players walk on snow-covered blocks, the sound will now resemble footsteps on snow.
- Now counts days beyond a year instead of resetting to zero.
- Add there meters to detect the climate info at the stand position.

### Minor Updates:

- Added translation support for "Rainfall."
- Optimized the display method for some non-critical warning messages.



0.9

### Things changes:

*Please proceed with this update carefully, as it requires feedback from players.
Especially for the greenhouse mechanism, it is currently only a test version, and further mechanism optimization and
content expansion may occur in the future.*

**Add two event.**

Now, when the solar term changes, the Game Bus will be notified, please check it in the API package.

At the same time, you can use events to modify and register crop information.

**Add greenhouse and humidity raising mechanism.**

You can now build greenhouses to protect against seasonal and humidity changes. You can build glass greenhouses or other
enclosed greenhouses, each suitable for different seasons. With convenience comes challenges. Winters in cold biomes are
now slightly longer for crops, while summers in hot biomes are similarly changed.

In addition, in a greenhouse, bubble columns exposed to air will slightly increase the humidity of the surrounding area
if activated by magma blocks below. Rain has a similar effect for open air environments.

### Performance Optimization：

add config cache

faster the weather query time

faster the small biome check time

### Bug Fixes:

fix thunder level in client check

fix sound in desert summer

clean necessary biome cache (not need for 1.20)


0.8.4.1
fix a very small probability of occasional crash
make spruce trees no longer drop leaves particles

0.8.4
Fixed an issue with weather rendering errors when switching dimensions
Fixed an issue where starting a world for the first time would trigger weather transition effects

0.8.3
fix the issue that crops would grow faster when the humidity level was not suitable
add config to adjust the base probability of when humidity level is not suitable


## 0.8.2

### Changes:

- **Optimize the rendering performance and rendering performance when using SolarWeather and raining**
  This will greatly reduce the rendering performance at this stage in most cases, but it will increase in some cases, such as near large river areas, in order to avoid treating biomes such as rivers as an independent climate distribution.
- Fixed a bug that caused abnormal temperature in Cold Sweat.


0.8.1
add support for journeymap
add two advancements
show a message when use the calendar (need open in config)

* support latest version of Cold Sweat
  ** fix advancements problem
  ** adjust the way seasonal temperatures are calculated when the Cold Sweat mod is enabled

## 0.8

add solar term icons
add calendar
add a config to set valid dimensions
repair an issue from outbound biome query due to biome noise
✨ add support for DH 2.2.0+
✨ fix single instance of biome precipitation of Oculus
✨ stop supporting season-related prompts from Legendary Survival Overhaul (due to over-coupled code)
✨ stop using built-in temperature settings from Cold Sweat and Legendary Survival Overhaul
✨ provide dedicated temperature modification configurations for Cold Sweat and Legendary Survival Overhaul

- fix the names of some configuration items
```
Debug.Debug -> Debug.LogIllegalUse
Temperature.IceAndSnowMelt -> Debug.LegacySnowAndMelt
Crop.UseDefaultValue -> Crop.RegisterCropDefaultValue
```
* fix the light problem of other block entities when use calendar
  ** fix remap problem with Oculus

0.7.6
fix using ReplayMod would not rain in replay
fix /setTerm command setting error term if the config "LastingDaysOfEachTerm" is not 7
fix temperature mapping error for Legendary Survival Overhaul compatibility layers
add a suggestion tooltip for /setTerm command
*** expand means of resisting heat stroke with some special enchantments, and also ice and snow
*** fix the crash if a tree sapling tried to grow in wrong season and was denied by Ecliptic Seasons

0.7.5
improved snow-covered effect support for more models, such as fences, walls and more
reduce the limitation of humidity on crop growth
allow config not use dynamic daylight duration change
allow use config to set whether the light controls the snow cover
add enum values cache for some class
allow config use enhanced seasonal rendering refresh
optimize some solar term calculation performance
fix an issue with biome grass colors

0.7.4
support season crop tag for Serene Seasons

0.7.3
add a config to hide crop planting info
fix where incorrectly tagged biomes were marked as impossible to rain.
fix grass flower model
fix snowy block render when not in fancy render
fix rain drop in biome which would never rain if using command
fix snow term in hot biomes if close to the baseline


0.7.2
optimize particle spawn
optimize chunk renderer query efficiency
* fix snowy simple glass renderer
* fix cache problem with simple snowy block when reload resource

0.7.1
optimized performance consumption when rain
* fix a simple render with some special mods slabs
  ** fix some codes
  ** fix sync problem if player die in other dimension
  ** fix bee

0.7.0
snowy blocks can now be interacted with (client-side effect only, not saveable)
when you break the snowy block, the thin snow layer will disappear until it starts to fall again
fix an issue with snowy blocks flickering at long distances

0.6.1
you can use vanilla weather by close Solar Weather
fix the color of fallen leaves
fix debug info

0.6
fix mixin crash
update config
update api package
* fix crash with Simple Cloud

0.5.2
fix snowy block renderer
** fix the render of flower on grass block

0.5.1
fix offset of snowy grass

0.5
more snowy block with more compatibility(such as glass and more)
fix birch leaves color

0.4
update textures
add more leaves color
add api instance
update weather check
update render method
add sapling grow control
* update weather transition
* fix level check

0.3.16
add compat for cold_sweat
fix compat for embeddium
* fix desert tag
* add small biome check
* fix biome of bound rain and snow
  ** fix model cache problem with ModernFix
  ** close fog weather which is used for test

0.3.15
add a config for crop humidity
add a config for crop season growth chance
fix #3 for use EclipticTagClientTool in server method

0.3.14
add compat for legendarysurvivaloverhaul
fix the sound in the rain and water

