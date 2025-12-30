## Basic Description

We can also define some multiblock structures to adjust the surrounding humidity. Note that this mechanism relies on random ticks. For blocks that cannot use random ticks, it must be forcibly enabled.
You can use the tag `eclipticseasons:volatile`.

These are JSON files, placed in the resource pack at the root path:
`data/<namespace>/eclipticseasons/wetter`.

## File Content

### Example Definition

```json
{
  "core": {
    "blocks": "minecraft:bubble_column"
  },
  "require": [
    {
      "offset": [
        0,
        -1,
        0
      ],
      "block": {
        "blocks": "minecraft:magma_block"
      }
    }
  ],
  "level": 0.75,
  "range": 4.0,
  "lasting_time": 600
}
```
