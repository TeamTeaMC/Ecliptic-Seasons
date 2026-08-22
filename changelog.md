### Voxy Compatibility

* Added seasonal snow and frozen-water rendering for Voxy LODs.
* Seasonal effects are applied during mesh generation without modifying Voxy’s stored world data.
* LODs now update automatically when solar terms or snow density change.
* Prioritized nearby sections when rebuilding LOD geometry.
* Improved cache invalidation and retry handling to reduce missed updates.
* Added dynamic seasonal biome-color updates without restarting the renderer.
* Added refresh progress messages when debug mode is enabled.
* Removed the legacy region reimport and database-rebuild approach.
