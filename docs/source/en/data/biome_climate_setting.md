> The purpose of the biome climate settings datapack is to enable seasonal variations without modifying the hardcoded
> values in the biome datapack.
> Most importantly, this value does not affect world generation.

## Basic Description

**Biome climate settings** are an auxiliary data pack system primarily used to adjust the native climate parameters of
specific biomes.
The original temperature and precipitation values of a biome significantly affect world generation and probability
calculations.

These are JSON files located at the root of the data pack under the path:
`data/<namespace>/eclipticseasons/biome_climate_setting`.

## File Contents

This data pack allows you to set parameters such as **biomes**, **downfall**, **temperature**, and *
*seasonal changes** to both.

Seasonal changes are defined using the `SolarTermMap` data structure.

### Definition Example

Below is an example showing how to increase downfall for savanna biomes during the rainy season, improving overall
moisture levels:

```json
{
  "biomes": "#minecraft:is_savanna",
  "downfall_changes": {
    "solar_terms": {
      "beginning_of_summer": 0.2,
      "lesser_fullness": 0.333,
      "grain_in_ear": 0.467,
      "summer_solstice": 0.533,
      "lesser_heat": 0.633,
      "greater_heat": 0.533,
      "beginning_of_autumn": 0.467,
      "end_of_heat": 0.4,
      "white_dew": 0.333,
      "autumnal_equinox": 0.267,
      "cold_dew": 0.2,
      "first_frost": 0.167
    }
  }
}
```

In some cases, the original biome climate parameters may have been altered in odd ways to suit world generation.
You can use this setting to correct those values manually.

> **Note:** If no specific snow period is defined for the biome in a snow term data pack, the **base temperature** here
> will affect when snow appears.

```json
{
  "biomes": "badlands",
  "downfall": 0.0,
  "temperature": 2.0
}
```