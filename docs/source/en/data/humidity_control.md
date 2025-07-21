## Basic Description

Humidity control mainly affects grid blocks, adjusting the humidity within a certain range.

It is a JSON file placed in the resource pack root directory at `data/<namespace>/eclipticseasons/humidity_control`.

## File Content

### Definition Example

Note that checks can be ignored, and the `count` in both elements and results does not need to be considered.  
Also, if `infinity` is not set, the `lasting_time` field must be specified, which is of type int.  
Currently, the range does not support values greater than 15.

```json
{
  "level": 1,
  "checks": [
    {
      "offset": [
        0,
        -1,
        0
      ],
      "block": {
        "blocks": "#eclipticseasons:soft_heat_sources"
      }
    }
  ],
  "infinity": true,
  "ingredient": {
    "item": "minecraft:wet_sponge",
    "count": 1
  },
  "result": {
    "id": "minecraft:sponge",
    "Count": 1
  },
  "range": 5
}
```
