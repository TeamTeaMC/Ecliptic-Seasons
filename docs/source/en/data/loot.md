## Basic Description

The seasonal system also provides a condition for loot tables based on solar terms, written similarly to standard loot conditions.

## File Content

### Example Definition

The following is a standard loot table with an additional seasonal control condition. Pay special attention to the `require` field.

```json
{
  "type": "minecraft:gift",
  "pools": [
    {
      "bonus_rolls": 0.0,
      "conditions": [
        {
          "condition": "eclipticseasons:season",
          "require": {
            "end": "beginning_of_winter",
            "start": "autumnal_equinox"
          }
        }
      ],
      "entries": [
        {
          "type": "minecraft:item",
          "name": "eclipticseasons:spring_greenhouse_essence",
          "weight": 10
        }
      ],
      "rolls": 1.0
    }
  ],
  "random_sequence": "eclipticseasons:gifts/autumn_greenhouse_essence"
}
```

#### Optional Parameters (Solar Term Method):

* `start`: Starting solar term
* `end`: Ending solar term
* `solar_term`: A specific solar term

When using solar terms for time segmentation, specify the lowercase solar term ID. See the *Climate* section for available term names.
`start` and `end` define a range; `solar_term` matches a single point in time.

#### Optional Parameters (Seasonal Method):

* `start_season`: Starting season
* `end_season`: Ending season
* `season`: A specific season

If you prefer using seasons, you can use the above parameters instead, also using lowercase IDs.
Please note: because biome seasons are defined at the granularity of solar terms, the season parameters here refer to *dimension seasons*.
As a result, seasonal matching may not work correctly in certain special biomes. If precise season control is required, we recommend using solar terms instead.
