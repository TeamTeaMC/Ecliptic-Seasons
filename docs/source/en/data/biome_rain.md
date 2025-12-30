> Biome precipitation is a compound probability—
> you also need to consider the downfall value defined in the biome climate settings.

## Basic Description

Biome rain is mainly used to adjust the weather parameters for certain biomes. Generally, it does not require much
attention, especially when Solar Weather is turned off. Note that rainfall also has some impact on humidity.

It is a JSON file placed in the resource pack root directory at `data/<namespace>/eclipticseasons/biome_rain`.

## File Content

### Definition Example

Below is an example setting biome rain for plains. Here, `biomes` is a HolderSet, so tag strings or others can also be
used.  
The `weathers` field is based on the SolarTermMap structure, and can contain `solar_terms`, `seasons`, or `default`.  
Regarding weather parameters, you can use `rain_chance`, `thunder_chance`, `rain`, `rain_delay`, `thunder`,
`time_periods`, and so on.  
If you want to specify time periods, `time_periods` is a list of time period strings. Also, note that the weather object
should be changed to a list to cover all time periods.  
Generally, providing two probability parameters is sufficient: `rain_chance` is required, and `thunder_chance` defaults
to 0 if not specified.

```json
{
  "biomes": "minecraft:plains",
  "weathers": {
    "solar_terms": {
      "beginning_of_spring": {
        "rain_chance": 0.3,
        "thunder_chance": 0.0
      },
      "rain_water": {
        "rain_chance": 0.5,
        "thunder_chance": 0.08
      },
      "insects_awakening": {
        "rain_chance": 0.55,
        "thunder_chance": 0.15
      },
      "spring_equinox": {
        "rain_chance": 0.5,
        "thunder_chance": 0.1,
        "rain": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": 12000,
            "max_inclusive": 24000
          }
        },
        "rain_delay": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": 12000,
            "max_inclusive": 180000
          }
        },
        "thunder": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": 3600,
            "max_inclusive": 15600
          }
        }
      },
      "fresh_green": {
        "rain_chance": 0.65,
        "thunder_chance": 0.05
      },
      "grain_rain": {
        "rain_chance": 0.75,
        "thunder_chance": 0.0
      },
      "beginning_of_summer": {
        "rain_chance": 0.9,
        "thunder_chance": 0.0
      },
      "lesser_fullness": {
        "rain_chance": 0.7,
        "thunder_chance": 0.1
      },
      "grain_in_ear": {
        "rain_chance": 0.6,
        "thunder_chance": 0.15
      },
      "summer_solstice": {
        "rain_chance": 0.7,
        "thunder_chance": 0.25
      },
      "lesser_heat": {
        "rain_chance": 0.65,
        "thunder_chance": 0.2
      },
      "greater_heat": {
        "rain_chance": 0.5,
        "thunder_chance": 0.05
      },
      "beginning_of_autumn": {
        "rain_chance": 0.42,
        "thunder_chance": 0.0
      },
      "end_of_heat": {
        "rain_chance": 0.4,
        "thunder_chance": 0.0
      },
      "white_dew": {
        "rain_chance": 0.35,
        "thunder_chance": 0.0
      },
      "autumnal_equinox": {
        "rain_chance": 0.32,
        "thunder_chance": 0.0
      },
      "cold_dew": {
        "rain_chance": 0.3,
        "thunder_chance": 0.0
      },
      "first_frost": {
        "rain_chance": 0.25,
        "thunder_chance": 0.0
      },
      "beginning_of_winter": {
        "rain_chance": 0.3,
        "thunder_chance": 0.0
      },
      "light_snow": {
        "rain_chance": 0.4,
        "thunder_chance": 0.05
      },
      "heavy_snow": {
        "rain_chance": 0.5,
        "thunder_chance": 0.0
      },
      "winter_solstice": {
        "rain_chance": 0.45,
        "thunder_chance": 0.0
      },
      "lesser_cold": {
        "rain_chance": 0.4,
        "thunder_chance": 0.0
      },
      "greater_cold": {
        "rain_chance": 0.2,
        "thunder_chance": 0.0
      },
      "none": {
        "rain_chance": 0.0,
        "thunder_chance": 0.0
      }
    }
  }
}
```

---

### Weather Effects

Sometimes you may need to define special weather types.
These are JSON files, which should be placed in the root of your resource pack at
`data/<namespace>/eclipticseasons/biome_rain_effect`.

For example, we can define a snow storm as follows:

```json
{
  "contents": [
    {
      "type": "snow"
    },
    {
      "density": 0.52,
      "type": "fog"
    }
  ],
  "type": "composite"
}
```

You can also define a fog type, and use the `replace` field to cancel rainfall:

```json
{
  "density": 0.2,
  "replace": true,
  "type": "fog"
}
```

These weather effects should be included in the `biome_rain` datapack.
Below is an example for a biome with ID `example:colder_plains`.
Following the cold-climate rules, it will trigger a blizzard during winter:

```json
{
  "biomes": "minecraft:plains",
  "weathers": {
    "climate": "eclipticseasons:cold",
    "seasons": {
      "winter": {
        "rain_chance": 0.5,
        "special_effect": "eclipticseasons:snow_storm"
      }
    }
  }
}
```
