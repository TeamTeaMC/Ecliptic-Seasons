## 0.13.0-beta-9

- Refactored the resource reloading system for multithreading.
- Improved JEI compatibility.
- Fixed rendering for the seasonal quest sign block.
- Re-enabled item storage for the copper grid.
- Readded biome sky color adjustment support.

## 0.13.0-beta-8

- Adjusted the code compatibility of Ecliptic Seasons: bundles to make it work in version 26.1.
- Slightly adjusted the rendering of the heatstroke effect.

## 0.13.0-beta-7

- Fixed an issue where built-in tags were not being bound correctly.
- Rewrote the logic for seasonal texture changes and adjusted it for simple model application.

## 0.13.0-beta-6

- Support for the custom model system has been restored; seasonal models and custom snow-covered models should now
  function correctly.

## 0.13.0-beta-5

- Fixed resource loading issues in production environments caused by invalid path-based access by migrating to the new
  built-in resource system.

## 0.13.0-beta-4

- The particle system has been updated to be consistent with the current version.
- Timeline compatibility has been further optimized; sleep times and villager schedules are now more closely aligned
  with seasonal changes.

## 0.13.0-beta-3

- Fixed an issue where built-in functions could incorrectly identify night as day.
- Fixed an issue where seasonal sound effects were not working.

## 0.13.0-beta-2

- Reinstated support for snow-covered models on special blocks.
- Tested the seasonal timeline and fixed some timeline-related compatibility issues.

## 0.13.0-beta-1

- This update primarily applies migration functions to version 26.1.

## 0.12.99-alpha-2

This update ports the mod to **Minecraft 26.1** and introduces a major internal refactor to align with the new *
*timeline / environment attribute system**.

🌐 Environment Attribute System

* Supported environmental data (season, solar term, temperature, etc.) to the new **EnvironmentAttribute system**
* Improved compatibility with MC 26+ timeline & clock APIs

⏱️ Time Handling Updates

* Better alignment with timeline-based time progression

Debug & UI Improvements

* Debug/info rendering now uses **registry-based data access**
* Improved biome and agro-climate display via generic helpers

### ⚠️ Notes

* This is an **alpha build** focused on internal migration
* Some features (e.g. seasonal rendering, snow model integration) may be incomplete or temporarily disabled
* Further stabilization and feature restoration will follow

If you encounter issues after the update, please report them—this version lays the groundwork for future seasonal and
environmental improvements.

---

## 26.1 Update (for snapshot-7 or maybe above)

* Updated to Minecraft 26.1
* This version is playable, but some features are still missing

### Known Issues

* Particles may render incorrectly
* Bees may leave hives during rain on servers

### Missing Features (temporary)

* Snow overlay does not support custom model types
* Seasonal models are not available yet
* Snow rendering is not supported for fences, walls, and similar blocks

### Changes

* Seasonal day/night length changes are currently disabled due to MC internal changes
