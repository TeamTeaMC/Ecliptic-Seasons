0.3.14
add compat for legendarysurvivaloverhaul
fix the sound in the rain and water (1.20-1.21, 1.19-1.16 plan)

0.3.15
add a config for crop humidity (1.20-1.21, and 1.19-1.16 plan)
add a config for crop season growth chance
fix #3 for use EclipticTagClientTool in server method

0.3.16
add compat for cold_sweat
fix compat for embeddium
* fix desert tag (1.20-1.21, 1.19-1.16 need check)
* add small biome check
* fix biome of bound rain and snow
** fix model cache problem with ModernFix (1.20-1.21, 1.19-1.16 need check)
** close fog weather which is used for test (1.20-1.21, 1.19-1.16 need check)

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
** fix sync problem if player die in other dimension (1.20-1.21, 1.19-1.16 plan)
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



