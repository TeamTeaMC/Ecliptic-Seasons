## Basic Description

By default, the Ecliptic Seasons mod assigns different weather to different biomes. However, due to fragmented biome distribution, you may sometimes want to merge the weather behavior of several biomes to smooth transitions.&#x20;

This feature is defined with JSON files placed at:
`data/<namespace>/eclipticseasons/weather_region`.

# File Contents

## Example Definition

Below is an example that groups forest-related biomes into one weather region. Here, `core` is the main biome ID; `sub` is a HolderSet (so you can also use tag strings, etc.); and `priority` is optional.&#x20;

```json
{
  "core": "minecraft:forest",
  "sub": [
    "minecraft:flower_forest",
    "minecraft:dark_forest",
    "minecraft:birch_forest"
  ],
  "priority": 1000
}
```
