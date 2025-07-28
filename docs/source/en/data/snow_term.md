> Snowfall timing follows three priority levels: the base biome temperature (lowest),
> biome climate settings override datapack (middle), and snow term datapack (highest).

## Basic Description

Snow terms based on solar terms are primarily time-driven.
If no custom settings are provided, snow seasons will be determined automatically based on the biomes’ temperature.

These are JSON files located at the root of the data pack under the path:
`data/<namespace>/eclipticseasons/snow_term`.

## File Contents

### Definition Example

Below is an example of a file that defines the snow period for the plain biome.
Here, `biomes` is a `HolderSet`, so biome tags or biome ID strings are also supported.

```json
{
  "biomes": "minecraft:plains",
  "start": "light_snow",
  "end": "greater_cold"
}
```
