## Basic Description

**Agro-climate zones** are a special type of data pack used to handle limitations in biome tags and to simplify mappings
for seasonal crop growth conditions.
This helps avoid the need to define an excessive number of data pack entries.

Humidity conditions, on the other hand, are controlled by biome-specific parameters and seasonal fluctuations.

These are JSON files located at the root of the data pack under the path:
`data/<namespace>/eclipticseasons/agro_climate`.

## File Contents

In most cases, you **don’t need to edit** this data pack.
Instead, you should define crop growth conditions for other climate zones via crop data entries.

If adjustments are necessary, you can override this file.
Setting `"biomes": []` disables the current entry, allowing you to create your own setup.

### Definition Example

Below is an example of an agro-climate configuration for tropical regions.
It primarily maps conditions based on temperate-region seasonal behavior during the **Beginning of Summer** and **Summer
Solstice**.

As a result, crops lacking summer adaptability may grow poorly in tropical zones.

The `seasonal_signal_durations` parameter is used for **seasonal greenhouse core redstone signals**.
The value should match the number of solar terms (typically 24).
When the greenhouse is in the appropriate season, the signal strength will increase.

```json
{
  "mappings": {
    "spring": [
      {
        "beginning_of_summer": 1.0
      }
    ],
    "summer": [
      {
        "summer_solstice": 1.0
      }
    ],
    "autumn": [
      {
        "summer_solstice": 0.6
      },
      {
        "beginning_of_summer": 0.4
      }
    ],
    "winter": [
      {
        "beginning_of_summer": 1.0
      }
    ]
  },
  "seasonal_signal_durations": [
    {
      "summer": 24
    }
  ],
  "biomes": {
    "values": [
      "#forge:is_hot/overworld"
    ],
    "type": "forge:and"
  }
}
```