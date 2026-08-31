## Ecliptic Seasons 0.15.0 — All Things in Their Season

Version 0.15.0 introduces a new way to customize the depth of seasonal gameplay, a completely redesigned configuration screen, and major improvements to distant seasonal rendering.

### Seasonal Simulation Levels

The new **Seasonal Simulation Level** option allows players to choose how deeply the seasons affect their worlds:

* **Environment** — Seasonal scenery, weather, snow, and atmosphere without gameplay changes.
* **Ecology** — Natural blocks, vegetation, and ecosystems respond to the seasons.
* **Agriculture** — Seasons also affect crops, humidity, farming, and greenhouses.
* **Survival** — Enables the complete seasonal experience, including animals, temperature, and survival mechanics.
* **Custom** — Allows seasonal features to be configured individually.

Snow rendering and vanilla snow-and-ice mechanics can now also be controlled independently through the new **Snow Behavior** option.

Recipes, advancements, loot tables, and other datapack content can adapt to the selected simulation level. Existing configurations will be migrated automatically wherever possible.

### Redesigned Configuration Screen

![The redesigned seasonal configuration screen](https://cdn.modrinth.com/data/ofdM7yJE/images/6295aa56d25e0531fbc7a68caa4cd43e10db6ce9.png)

The in-game configuration screen has been completely redesigned with seasonal backgrounds, ambient effects, improved navigation, configuration search, and clearer descriptions.

Its underlying framework can also be reused by Ecliptic Seasons addons, allowing related mods to provide their own configuration screens with a consistent appearance and experience.

### Voxy Compatibility Rework

![Snowy Autumnal Forest in Spyglass with Voxy](https://cdn.modrinth.com/data/ofdM7yJE/images/e91b851778c5a22828e9955dd6a497b11c4a674a.png)

*Snowy Autumnal Forest in Spyglass, rendered with Voxy.*

Seasonal Voxy support has been extensively redesigned.

* Voxy LODs can display seasonal vegetation, snow-covered terrain, and frozen water.
* Seasonal appearances are applied during mesh generation without modifying Voxy’s stored world data.
* Snow changes now rebuild visible LOD geometry instead of reimporting world regions.
* Solar-term changes can update distant seasonal colors without restarting the entire renderer.
* Nearby areas are prioritized, with improved task coordination and retry handling to reduce missed updates.

> **Minecraft 1.20.1 and 1.21.1:** Voxy compatibility on these versions is provided through [Ecliptic Seasons: Voxy Compat](https://legacy.curseforge.com/minecraft/mc-mods/ecliptic-seasons-voxy-compact). Please wait for that compatibility mod to receive its corresponding update before using the new system.

### Distant Horizons Improvements

Distant Horizons can now reproduce appearances provided by Ecliptic Seasons’ **seasonal model system**, allowing compatible blocks to retain their seasonal appearance in distant LODs.

Seasonal snow, frozen water, color calculation, and world-reloading behavior have also been improved.

### Falling Leaves

Fallen leaves can now remain briefly on the ground and gradually fade instead of disappearing immediately. Related particle frequency handling has also been corrected.

### Performance and Compatibility

New lightweight Mixin injection methods reduce unnecessary allocations in extremely high-frequency code paths. Compatibility Mixins can also be applied more precisely according to the installed mod and version.

### Additional Improvements

* Added automatic migration for renamed and reorganized configuration options.
* Improved snow-covered model generation.
* Added seasonal foliage color blending support for Embeddium.
* Fixed incorrect heightmap usage that could cause stuttering while chunks were loading.
* Improved several greenhouse, crop compatibility, rendering, and world-cache behaviors.

Version 0.15.0 makes Ecliptic Seasons easier to configure, more flexible for different playstyles, and capable of carrying the changing seasons all the way to the distant horizon.

---

Seasonal UI backgrounds adapted from [Seasonal Tilesets](https://grafxkid.itch.io/seasonal-tilesets) by GrafxKid.
