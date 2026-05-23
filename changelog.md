### Changed
- Enabled the Not Ignore River datapack by default and fixed its incorrect location.
- Disabled river weather merging during initial chunk loading to improve chunk loading performance.
- River biomes are now treated as non-rainy while `IndependentRiverWeather` is enabled, helping keep weather behavior consistent.

### Notes
- River weather merging means river biomes use nearby non-river biomes as the basis for weather calculation.
- For existing worlds, you may run `/ecliptic debug reset surface_biome_cache` to reset the biome weather cache. This step is optional.
- To restore the old behavior where rivers inherit weather from nearby biomes, disable `IndependentRiverWeather` in the config.