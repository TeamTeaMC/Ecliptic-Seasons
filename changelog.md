1.0.2
- Fire resistance grants immunity to heatstroke as well.
- The ambient temperature required to induce heatstroke has been increased.


1.0.1
Fixes

Fixed an issue where biome temperature adjustments during world generation could prevent snow from generating in cold biomes in multiplayer games.

Resolved a problem where special creature particles and related sound effects would appear during snow in some cold biomes during spring and summer.

Additions

Added initialization of biome-related weather data during world initialization or when joining a mod for the first time. Now, if certain biomes can snow or have just ended a snow cycle upon login, snow overlay decorations will be visible. Additionally, the probability of initial weather being sunny has been increased.

SolarDay can now be set to a negative value, potentially offering an experience akin to BCE (Before Common Era).

Removals

Removed redundant temperature checks in weather calculations.

1.0.0.3
Fixed a config name error ("snow line").

1.0.0.2
Fixed a mixin error related to Fancy Block Particles.

1.0.0.1
Added a null pointer check to avoid world creation freeze.

Legacy-1.0
New Features

Fancy Block Particles - Renewed Support

Added compatibility for the particle effects mod Fancy Block Particles - Renewed.

Dynamic Snow Line

Added per-biome snow line height configurations with global override settings.

Snow Overlay Light Check

Snow Overlays now only generate if the block meets specific light level conditions.

Realistic Snow Updates

Improved snowy block render logic to prevent instant snow overlay generation after fast block placement.

Backports

Cold Sweat Compatibility

Backported support for the survival mod Cold Sweat.

Legendary Survival Overhaul Compatibility

Backported support for Legendary Survival Overhaul (1.16-only).

Simple Greenhouse System

Backported a basic greenhouse feature, restricted via block tags.

Tree Sapling Grow Control

Saplings can now be controlled by seasonal and humidity tags.

API & Developer Tools

Official API Release

Added an official API version for developers to integrate and extend mod functionality.

Optimizations & Fixes

Rubidium Support Removed

Dropped compatibility with Rubidium; replaced with Embeddium as the rendering backend.

Chunk Rendering Optimization

Enhanced chunk update logic to reduce lag and performance overhead.

Legacy Snow & Melt (Optional)

Added legacy snow/melting mechanics as a configurable optional feature.

Fixed an issue where zombies would burn in some cases, such as when snow is present locally during the day.

Miscellaneous

Code cleanup and internal refactoring for improved stability.

0.3.1
Optimized Transition Effects

Enhanced the visual transition for the onset and conclusion of rain/snow, and refined the vanilla rain effects to achieve a smoother, more natural appearance.

Removed Obsolete Feature

Removed the unused "snowy dandelions" model from the files.

0.3
Update Highlights
This update focuses on backport mod improvements and ecosystem compatibility, introducing new climate monitoring tools while refining core mechanics. Key additions include a calendar system and humidity measurement devices, alongside critical fixes for biome anomalies and cross-mod interoperability. The changes span four main areas:

✨ New Features

Added a calendar item and block.

Added three types of climate measurement tools (for humidity detection) that can be placed on item frames.

Added a disable option for crop humidity control.

⚙️ Optimizations

Optimized the playback logic of environmental sound effects.

Modernized portions of the code structure.

Removed obsolete code and resources.

Adjusted language (lang) key values to align with the latest version.

Solar day records no longer set zero at the end of a year.

🐛 Bug Fixes

Fixed issues where disabling notifications might cause other problems.

Fixed solar data synchronization issues when returning from the End to the Overworld.

Fixed abnormal skybox brightness during client-side thunderstorms.

Fixed inconsistencies between the /setTerm command results and configurations about the 'lastDays'.

Fixed snow occurrence in certain hot biomes.

Fixed biome temperature anomalies caused by incorrect object references.

Resolved missing tags due to improper references to glow berries.

🔄 Compatibility & Integration

Added built-in tag conversion compatibility for Serene Seasons.

Implemented compatibility measures for weather mods incompatible with this mod (to prevent Overworld conflicts).