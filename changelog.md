0.3.14
add compat for legendarysurvivaloverhaul
fix the sound in the rain and water

0.3.15
add a config for crop humidity
add a config for crop season growth chance
fix #3 for use EclipticTagClientTool in server method

0.3.16
add compat for cold_sweat
fix compat for embeddium
* fix desert tag
* add small biome check
* fix biome of bound rain and snow
** fix model cache problem with ModernFix
** close fog weather which is used for test

0.4
update textures
add more leaves color
add api instance
update weather check
update render method
add sapling grow control
* update weather transition
* fix level check

0.5
more snowy block with more compatibility(such as glass and more)
fix birch leaves color

0.5.1
fix offset of snowy grass

0.5.2
fix snowy block renderer
** fix the render of flower on grass block

0.6
fix mixin crash
update config
update api package
* fix crash with Simple Cloud

0.6.1
you can use vanilla weather by close Solar Weather
fix the color of fallen leaves
fix debug info

0.7.0
snowy blocks can now be interacted with (client-side effect only, not saveable)
when you break the snowy block, the thin snow layer will disappear until it starts to fall again
fix an issue with snowy blocks flickering at long distances

0.7.1
optimized performance consumption when rain
* fix a simple render with some special mods slabs
** fix some codes
** fix sync problem if player die in other dimension
** fix bee

0.7.2
optimize particle spawn
optimize chunk renderer query efficiency
* fix snowy simple glass renderer
* fix cache problem with simple snowy block when reload resource

0.7.3
add a config to hide crop planting info
fix where incorrectly tagged biomes were marked as impossible to rain.
fix grass flower model
fix snowy block render when not in fancy render
fix rain drop in biome which would never rain if using command
fix snow term in hot biomes if close to the baseline

0.7.4
support season crop tag for Serene Seasons

0.7.5
improved snow-covered effect support for more models, such as fences, walls and more
reduce the limitation of humidity on crop growth
allow config not use dynamic daylight duration change
allow use config to set whether the light controls the snow cover
add enum values cache for some class
allow config use enhanced seasonal rendering refresh
optimize some solar term calculation performance
fix an issue with biome grass colors

0.7.6
fix using ReplayMod would not rain in replay
fix /setTerm command setting error term if the config "LastingDaysOfEachTerm" is not 7
fix temperature mapping error for Legendary Survival Overhaul compatibility layers
add a suggestion tooltip for /setTerm command
*** expand means of resisting heat stroke with some special enchantments, and also ice and snow
*** fix the crash if a tree sapling tried to grow in wrong season and was denied by Ecliptic Seasons

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

0.8.1
add support for journeymap
add two advancements
show a message when use the calendar (need open in config)

* support latest version of Cold Sweat
** fix advancements problem
** adjust the way seasonal temperatures are calculated when the Cold Sweat mod is enabled

## 0.8.2

### Changes:

- **Optimize the rendering performance and rendering performance when using SolarWeather and raining**
  This will greatly reduce the rendering performance at this stage in most cases, but it will increase in some cases, such as near large river areas, in order to avoid treating biomes such as rivers as an independent climate distribution. 
- Fixed a bug that caused abnormal temperature in Cold Sweat.

0.8.3
fix the issue that crops would grow faster when the humidity level was not suitable
add config to adjust the base probability of when humidity level is not suitable

0.8.4
Fixed an issue with weather rendering errors when switching dimensions
Fixed an issue where starting a world for the first time would trigger weather transition effects

0.8.4.1
fix a very small probability of occasional crash
make spruce trees no longer drop leaves particles

0.9
### Things changes:

*Please proceed with this update carefully, as it requires feedback from players. 
Especially for the greenhouse mechanism, it is currently only a test version, and further mechanism optimization and content expansion may occur in the future.*

**Add two event.**

Now, when the solar term changes, the Game Bus will be notified, please check it in the API package.

At the same time, you can use events to modify and register crop information.

**Add greenhouse and humidity raising mechanism.**

You can now build greenhouses to protect against seasonal and humidity changes. You can build glass greenhouses or other enclosed greenhouses, each suitable for different seasons. With convenience comes challenges. Winters in cold biomes are now slightly longer for crops, while summers in hot biomes are similarly changed.

In addition, in a greenhouse, bubble columns exposed to air will slightly increase the humidity of the surrounding area if activated by magma blocks below. Rain has a similar effect for open air environments.

### Performance Optimization：
add config cache

faster the weather query time

faster the small biome check time
### Bug Fixes:
fix thunder level in client check

fix sound in desert summer

clean necessary biome cache (not need for 1.20)

Here's an improved version of the text with better formatting:

---

**0.10-pre**

### Critical Fixes:
- Fixed an issue where certain mods could not obtain correct rainfall prediction information when using biome queries. This was due to the lack of Level input and no interaction with the mod. These mods will now receive query results related to the Overworld.
- Fixed an issue regarding the run of solar terms system when informs are disabled.
- The light check of handheld maps for snow-covered blocks is temporarily disabled to avoid erroneous calls that could cause server disconnects.

### Improvements:
- Refactored and organized the registration-related code due to the increasing amount of registration information.
- When players walk on snow-covered blocks, the sound will now resemble footsteps on snow.
- Now counts days beyond a year instead of resetting to zero.
- Add there meters to detect the climate info at the stand position.

### Minor Updates:
- Added translation support for "Rainfall."
- Optimized the display method for some non-critical warning messages.


---

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

--

**0.10-pre3**

- Added a null pointer check for model detector to prevent crash from some mod releasing the render context prematurely
- Provided colored foliage particle compatibility for resource packs that change foliage textures.

--

**0.10-pre4**

- Add support for Snowy Spirit, let enjoy skiing and Christmas Holiday
- Add support for Haunted Harvest, let enjoy Halloween Holiday

---

## **0.10-pre5**

- Add support for InControl to check four seasons
- fix a remap issue with Snowy Spirit

## **0.10-pre6**
- Fixed an issue where the morning sun would move instantly when the solar term was about to change.
- Fix zombie would burn in some case like just snow locally in the day.
- Changed the default chunk rendering update strategy to prevent chunk rendering delays and performance impact.
- Moved Snowy Winter and related options to Common Config. Now Snowy detection can be performed on both ends.
- If RealisticSnowyChange is enabled, snow overlay that has been melted by light will no longer be replaced immediately, even if the light source is removed.
- Fixed an issue with unexpected snow blocks appearing when exploring the Biome in winter.
- Added legacy snow/melting mechanics as a configurable optional feature, and they are separate

pre7

fix leaves particles color
add maid support
add snow not like tag
add fake player support