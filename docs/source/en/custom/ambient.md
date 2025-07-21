## Basic Description

Seasonal ambient sounds are a client-side only feature, implemented via resource packs, used to customize seasonal
environmental background audio.

These are JSON files located at the root of the resource pack under the path:
`assets/<namespace>/eclipticseasons/ambient`.

## File Contents

### Definition Example

Below is an example of an ambient sound setting for a summer night.
By default, `"rain"` is set to `false`.
To differentiate between day and night, you need to set `"ignore_time"` accordingly.

Unlike real-time sound blending mods, the seasonal ambient sound system provided by this mod is designed as a full
background layer, to help compensate for Minecraft’s sometimes overly quiet environment.

```json
{
  "sound": "eclipticseasons:ambient.night_river",
  "biomes": "#minecraft:is_savanna",
  "ignore_time": false,
  "day": false,
  "rain": false,
  "season": "summer"
}
```
