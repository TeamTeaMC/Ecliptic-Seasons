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

Legacy-1.0
# Mod Update Changelog

### New Features
1. **Fancy Block Particles - Renewed Support**
    - Added compatibility for the particle effects mod *Fancy Block Particles - Renewed*.
2. **Dynamic Snow Line**
    - Added per-biome snow line height configurations with global override settings.
3. **Snow Overlay Light Check**
    - Snow Overlays now only generate if the block meets specific light level conditions.
4. **Realistic Snow Updates**
    - Improved snowy block render logic to prevent instant snow overlay generation after fast block placement.

---

### Backports
1. **Cold Sweat Compatibility**
    - Backported support for the survival mod *Cold Sweat*.
2. **Legendary Survival Overhaul Compatibility**
    - Backported support for *Legendary Survival Overhaul* (1.16-only).
3. **Simple Greenhouse System**
    - Backported a basic greenhouse feature, restricted via block tags.
4. **Tree Sapling Grow Control**
   - Sapling can now be controlled by seasonal and humid tag too.

---

### API & Developer Tools
1. **Official API Release**
    - Added an official API version for developers to integrate and extend mod functionality.

---

### Optimizations & Fixes
1. **Rubidium Support Removed**
    - Dropped compatibility with *Rubidium*; replaced with *Embeddium* as the rendering backend.
2. **Chunk Rendering Optimization**
    - Enhanced chunk update logic to reduce lag and performance overhead.
3. **Legacy Snow & Melt/ (Optional)**
    - Added legacy snow/melting mechanics as a configurable optional feature.

---

### Miscellaneous
- Code cleanup and internal refactoring for improved stability.  

