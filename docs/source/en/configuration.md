## Client Configuration Additional Notes

### Renderer

* `ForceChunkRenderUpdate`: Has a performance impact. Since in the default settings, solar term snowfall is not directly
  stored as world blocks, this forces updates to certain client chunk rendering.
* `EnhancementChunkRenderUpdate`: Disabled by default. Enable only if the computer performance is sufficient; it forces
  rendering of all surface chunks.

### Particle

* `SpawnWeight`: Note this value acts as a random boundary; the larger the value, the lower the actual particle spawn
  probability.

### Weather

If Solar Weather is enabled, pay attention to whether these configurations meet your needs.  
Many client mods and shaders rely on global settings. These settings are mainly for better weather rendering
aesthetics.  
The main logic samples the weather around the player and combines it to obtain a new global weather value.  
If you find strange raindrop sounds annoying, it is generally recommended to use a lower `WeatherBufferDistance` value.

## Common Configuration Additional Notes

### Season

* `LastingDaysOfEachTerm`: Sets the length of each solar term. Note that one season contains six solar terms.
* `InitialSolarTermIndex`: Sets the initial solar term index, defaulting to the Spring Equinox.
* `DynamicDaylightDuration`: Makes daylight duration vary with the season.
* `ValidDimensions`: Dimensions where the season applies. Note that in 1.20 this option will not synchronize to clients
  and must be synced by the server itself.

### Snow
* `SnowyWinter`: Some players may think this is a client setting, but snow cover calculations need to consider
  server-side requirements.

### Weather

* `UseSolarWeather`: Whether to enable the local weather system. If encountering incompatibilities, such as some mods
  using global weather parameters (especially server mods), this can be used to adjust.
* `ShouldInitWeather`: Whether to initialize weather parameters; can make it rain when the world is created. Also
  initializes biome snow coverage.

### Temperature

* `HeatStroke`: Whether to enable heatstroke.
* `IceAndSnow`: Vanilla snow and ice; note this affects the world.
* `IceAndSnowMelt`: Vanilla snow and ice melting; note this affects the world.

### Map

* `ServerRealisticSnowyChange`: As the name implies, realistic snowy changes on the server side, for fun only.
  Currently, it is not saved to data and is disabled by default.

### Resource

* `ExtraSnowDefinitions`: Additional snow-covered blocks and appearance resource packs, usually plants. This actually
  includes both datapack and resource pack assets.
