## **0.10.5.0.1**
- Fixed an issue where snow-covered hedges were incorrectly colored when using Embeddium and Quark.

## **0.10.5**
- Thermometers and hyetometers have been hidden.

To avoid player confusion regarding the concepts of thermometer and hyetometer, thermometers and rain gauges are no longer displayed by default in the Creative mode inventory (including JEI). Additionally, their recipe unlock condition has been adjusted to require obtaining them at least once. Meanwhile, the recipe for the hygrometer has been modified to allow direct one-step crafting.

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

## **0.10.4.0.1**
- Fixed incorrect packet processing timing for biome climate settings on multiplayer clients.

## **0.10.4.0.1**
- Fixed incorrect packet processing timing for biome climate settings on multiplayer clients.

## **0.10.4**
- Added a biome control temperature and downfall data pack to adjust biome humidity changes without modifying the biome JSON definitions, avoiding conflicts with terrain generation control systems like WWOO, which modify temperature and downfall solely based on terrain considerations.
- Adjusted some humidity calculation methods.

## **0.10.3.1.2**
- Fixed an issue where wet sponges would be ejected from the humidifier during level reloading if they were present and the conditions below met the soft heat source activation criteria.
- Fixed an issue where seasonal quest notices could lose their assigned quests after reloading.

## **0.10.3.1.1**
- Fixed the missing translation for "Season Wall Quest Sign" that was incorrectly overwriting the "Air" entry.

## **0.10.3.1**
- Fixed an issue with crop growth errors when incomplete crop growth information was registered.

## **0.10.3-hotfix**
- Fixed an issue where putting a greenhouse essence into the greenhouse container would not consume the item.

## **0.10.2.2**
- Optimized the LOD (Level of Detail) update mechanism in Distant Horizons' compatibility module when both ForceChunkRenderUpdate and EnhancementChunkRenderUpdate are enabled.

## **0.10.2.1**
- Since the mod uses some methods backported by Forge from version 1.21, the version requirements have been raised to avoid misunderstandings. This aligns with the mainstream mods.
- Fixed an issue where players might be unable to join multiplayer games. The current data pack system was migrated from version 1.21, while Minecraft's 1.20 data pack network synchronization mechanism was somewhat unstable. We sincerely apologize for the inadequate coordination during this transition.

## **0.10.2**
- Optimized the placement experience of the calendar, allowing it to be placed even when clicking on diagonal surfaces.
- Fixed a bug related to whether Biome has weather precipitation.

## **0.10.1**
- Updated the texture of the Wooden Grate.
- Fixed a potential item duplication issue.
- Optimized the rendering speed of greenhouse items.

## **0.10**
This version, ported from 1.21, features comprehensive reworks to seasonal greenhouse mechanics:
- Fixed the built-in compatibility handling for the Serene Seasons tag data pack. Due to significant modifications in the underlying logic of data packs in version 0.10-pre8, data conversion issues existed prior to the new version.
- Reworked Greenhouse Construction: Completely redesigned seasonal greenhouse building system.
- Growth Detector: Added a monitoring device to track crop growth progress.
- Greenhouse Core System: Introduced Seasonal Greenhouse Core blocks and related components. Crops now require placement within the core's operational radius to receive greenhouse growth bonuses.
- Humidity Control: Implemented Humidity Regulator Blocks for localized moisture adjustment. Multiple regulators create stacked effects when working in proximity.
- Seasonal Questline: Added themed quests and associated items. Greenhouse Cores can now be obtained by completing introductory seasonal missions.
- Progression Optimization: Overhauled achievement system and quest guidance. Completing basic seasonal quests now grants material rewards for crafting Greenhouse Cores.
- Other simple fixes.

## **0.10-pre12**
- Optimized temperature calculation method.
- Optimized the interaction performance of this mod with dimensions and weather, making its configuration items more stable and reserving room for expansion.
- The ambient temperature required to induce heatstroke has been increased.

## **0.10-pre11-2**
- The default global probability coefficients of thunder and rain have been reduced.
- Fire resistance grants immunity to heatstroke as well.
- Adjusted subsequent check limits for ValidDimension.
- Added some missing translations for calendar.

## **0.10-pre11**
### Fixes
- Fixed an issue in the 0.10-pre6 update where biome temperature adjustments during world generation could prevent snow from generating in cold biomes in multiplayer games.
- Resolved a problem where special creature particles and related sound effects would appear during snow in some cold biomes during spring and summer.

### Additions
- Added initialization of biome-related weather data during world initialization or when joining a mod for the first time. Now, if certain biomes can snow or have just ended a snow cycle upon login, snow overlay decorations will be visible. Additionally, the probability of initial weather being sunny has been increased.
- Added two configurations related to greenhouse determination.
- `SolarDay` can now be set to a negative value, potentially offering an experience akin to BCE (Before Common Era).
- Replaced some recipe `Item` references with `Item Tag` to improve compatibility.

### Removals
- Removed redundant temperature checks in weather calculations.

---

## **0.10-pre10-3**
- Use toLowerCase(Locale.ROOT) to avoid locale conversion.
- Fixed an issue where `setThunder` might not function correctly when `SolarWeather` is not in use.
- Fixed an issue with thundering weather when it's not raining.
- Adjusted greenhouses in summer so that they only become inoperable at noon, rather than throughout the day.
- Adjusted the ResourceOrTagArgument.Result used by Ecliptic Seasons when issuing the proxy weather command to be dynamically created and return a non-null value.

## **0.10-pre10-2**
- fix an issue with single player world data cache being lost due to clearing the cache at the wrong time.

## **0.10-pre10**

- Fixed a server sync error in 0.10-pre8 caused by using a data pack that met the 1.21 requirements but was not designed for 1.20.
  Dynamic data registration types cannot be accessed in the 1.20 login registration data package synchronization, and also the tag data has not yet been synchronized to the client.
- clear some cache which might cause memory leak on client exit or server close

## **0.10-pre9**

- Resolved a bug in IceAndSnow(Melt) config, and related functions to ensure they meet standard expectations.
- Enhanced chunk-level weather calculations for improved realism.
- Optimized rendering performance during heat stroke conditions to maintain a stable frame rate.


## **0.10-pre8**

- Revert a change to improve map color calculation speed for brute force map mods
- Test crop growth control data pack.

Ported from version 1.21. Now, different biomes are divided into distinct argo climatic zones, initially set as cold,
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

## **0.10-pre7**

fix leaves particles color
add snow overlay not like tag
test maid support to clean snow overlay
add fake player support to clean snow overlay such as Deployer in Create

---

## **0.10-pre6**
- Fixed an issue where the morning sun would move instantly when the solar term was about to change.
- Fix zombie would burn in some case like just snow locally in the day.
- Changed the default chunk rendering update strategy to prevent chunk rendering delays and performance impact.
- Moved Snowy Winter and related options to Common Config. Now Snowy detection can be performed on both ends.
- If RealisticSnowyChange is enabled, snow overlay that has been melted by light will no longer be replaced immediately, even if the light source is removed.
- Fixed an issue with unexpected snow blocks appearing when exploring the Biome in winter.
- Added legacy snow/melting mechanics as a configurable optional feature, and they are separate

---

## **0.10-pre5**

- Add support for InControl to check four seasons
- fix a remap issue with Snowy Spirit
---

**0.10-pre4**

- Add support for Snowy Spirit, let enjoy skiing and Christmas Holiday
- Add support for Haunted Harvest, let enjoy Halloween Holiday

---

**0.10-pre3**

- Added a null pointer check for model detector to prevent crash from some mod releasing the render context prematurely
- Provided colored foliage particle compatibility for resource packs that change foliage textures.

--

**0.10-pre2**

- Rendering Pipeline Compatibility Optimization:
  Resolved the issue where Embeddium and FF API,
  when loaded simultaneously,
  caused Fabric Models to use the Fabric rendering pipeline while Forge Models continued to use the legacy pipeline.
  Continuity forced conversion of all models to Fabric Models if it's not a CTM model,
  resulted in occasional face flickering in Ecliptic Seasons' snow model rendering due to smooth lighting.
  This update addresses the issue, ensuring consistent rendering.

- Greenhouse Detection Mechanism Improvement:
  Optimized the detection logic for greenhouses,
  enhancing detection speed and accuracy for more efficient performance.

- Slowed down the rate of growth in the wrong season.


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

support latest version of Cold Sweat
fix advancements problem
adjust the way seasonal temperatures are calculated when the Cold Sweat mod is enabled

## 0.8

add solar term icons
add calendar
add a config to set valid dimensions
repair an issue from outbound biome query due to biome noise
add support for DH 2.2.0+
fix single instance of biome precipitation of Oculus
provide dedicated temperature modification configurations for Cold Sweat and Legendary Survival Overhaul

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

