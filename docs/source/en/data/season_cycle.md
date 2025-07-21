## Basic Description

Season cycles are used together with season phases to modify the mapping from the current solar term to the actual
displayed season. This enables special biome effects, such as eternal autumn.  
Note that season cycles are not enabled by default and need to be turned on in the settings.

These are JSON files placed in the resource pack root directory at `data/<namespace>/eclipticseasons/season_cycle`.

## File Content

### Definition Example

Below is an example file setting this mapping for the tropical savanna biome. Here, `biomes` is a HolderSet, so tag
strings or others can also be used.  
The `phases` field is based on the SolarTermMap structure, and can include `solar_terms`, `seasons`, or `default`.

```json
{
  "biomes": "#eclipticseasons:monsoonal",
  "phases": {
    "solar_terms": {
      "beginning_of_spring": "eclipticseasons:dry_middle",
      "rain_water": "eclipticseasons:dry_middle",
      "insects_awakening": "eclipticseasons:dry_end",
      "spring_equinox": "eclipticseasons:dry_end",
      "fresh_green": "eclipticseasons:dry_end",
      "grain_rain": "eclipticseasons:dry_end",
      "beginning_of_summer": "eclipticseasons:rain_start",
      "lesser_fullness": "eclipticseasons:rain_middle",
      "grain_in_ear": "eclipticseasons:rain_middle",
      "summer_solstice": "eclipticseasons:rain_end",
      "lesser_heat": "eclipticseasons:rain_end",
      "greater_heat": "eclipticseasons:rain_end",
      "beginning_of_autumn": "eclipticseasons:rain_end",
      "end_of_heat": "eclipticseasons:wet_start",
      "white_dew": "eclipticseasons:wet_start",
      "autumnal_equinox": "eclipticseasons:wet_middle",
      "cold_dew": "eclipticseasons:wet_middle",
      "first_frost": "eclipticseasons:wet_end",
      "beginning_of_winter": "eclipticseasons:dry_start",
      "light_snow": "eclipticseasons:dry_start",
      "heavy_snow": "eclipticseasons:dry_start",
      "winter_solstice": "eclipticseasons:dry_start",
      "lesser_cold": "eclipticseasons:dry_middle",
      "greater_cold": "eclipticseasons:dry_middle"
    }
  }
}
```
