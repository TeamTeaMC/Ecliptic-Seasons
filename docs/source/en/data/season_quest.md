## Basic Description

The season quest datapack is currently quite simple. Its main purpose is to allow solo players to still obtain the
greenhouse core after completing the seasonal advancements and corresponding seasonal planting.  
It is recommended that mod pack authors customize how to obtain the greenhouse core, such as by boss challenges, server
shops, or FTB quests.

These are JSON files placed in the resource pack root directory at `data/<namespace>/eclipticseasons/season_quest`.

## File Content

### Definition Example

Below is an example for the spring core quest. Note that for `need`, it uses `Ingredient`, so some special types
can be used.

```json
{
  "weight": 10,
  "glowing": true,
  "color": 43520,
  "climate": "#eclipticseasons:all",
  "award": [
    {
      "id": "eclipticseasons:spring_greenhouse_essence",
      "Count": 1
    }
  ],
  "tittle": {
    "translate": "season_quest.eclipticseasons.spring_core"
  },
  "end": "beginning_of_summer",
  "start": "spring_equinox",
  "need": [
    {
      "items": "minecraft:wheat",
      "count": 640
    }
  ]
}
```
