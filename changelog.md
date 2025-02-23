0.3

**Update Highlights**  
This update focuses on backport mod improvements and ecosystem compatibility,
introducing new climate monitoring tools while refining core mechanics.
Key additions include a calendar system and humidity measurement devices,
alongside critical fixes for biome anomalies and cross-mod interoperability.
The changes span four main areas:

### ✨ New Features

- Added a calendar item and block
- Added three types of climate measurement tools (for humidity detection) that can be placed on item frames
- Added a disable option for crop humidity control

### ⚙️ Optimizations

- Optimized the playback logic of environmental sound effects
- Modernized portions of the code structure
- Removed obsolete code and resources
- Adjusted language (lang) key values to align with the latest version
- Solar day records no longer set zero at the end of a year.

### 🐛 Bug Fixes

- Fixed issues where disabling notifications might cause other problems
- Fixed solar data synchronization issues when returning from the End to the Overworld
- Fixed abnormal skybox brightness during client-side thunderstorms
- Fixed inconsistencies between the `/setTerm` command results and configurations about the 'lastDays'
- Fixed snow occurrence in certain hot biomes
- Fixed biome temperature anomalies caused by incorrect object references
- Resolved missing tags due to improper references to glow berries

### 🔄 Compatibility & Integration

- Added built-in tag conversion compatibility for Serene Seasons
- Implemented compatibility measures for weather mods incompatible with this mod (to prevent Overworld conflicts)

0.3.1
Optimized Transition Effects:
Enhanced the visual transition for the onset and conclusion of rain/snow, and refined the vanilla rain effects to
achieve a smoother, more natural appearance.

Removed Obsolete Feature:
Removed the unused "snowy dandelions" model from the files.

0.3.2
backport support for Cold Sweat
backport support for Legendary Survival Overhaul (only 1.16)
remove the support for rubidium (use embeddium instead)
add support for Fancy Block Particles - Renewed
