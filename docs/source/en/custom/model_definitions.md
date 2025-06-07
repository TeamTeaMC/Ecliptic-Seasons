## Basic Explanation

If you want to create a snow-covered model resource pack or a four-season model resource pack, you must first create model file mappings.

These are JSON files placed in the resource pack root path at `assets/<namespace>/eclipticseasons/model_definitions`.

## File Content

Basically, the model file mapping is very similar to the `blockstates.json` used to create block models, except with some additional properties and no need to correspond to block names.

Therefore, you can directly use the Multi-Variant or Multi-Part system. As shown below, they are no different from the standard — the `id` here is the ID inside the model folder:

### Model Variants

```json
{
  "variants": {
    "": {
      "model": "eclipticseasons:block/overlay"
    }
  }
}
```

You can also use weights to adjust spawn probability. Note that block property conditions such as `facing=west,half=bottom,shape=straight` can be added here.

```json
{
  "variants": {
    "": [
      {
        "model": "eclipticseasons:block/flower_1"
      },
      {
        "weight": 120,
        "model": "minecraft:block/air"
      }
    ]
  }
}
```

### Model Multipart

By combining properties, you can easily compose models and reduce the number of registered models. Note that such blocks increase performance costs and should be avoided for frequent use.

```json
{
  "multipart": [
    {
      "when": {
        "snowy": "false"
      },
      "apply": [
        {
          "model": "minecraft:block/grass_block_snow"
        },
        {
          "y": 90,
          "model": "minecraft:block/grass_block_snow"
        },
        {
          "y": 180,
          "model": "minecraft:block/grass_block_snow"
        },
        {
          "y": 270,
          "model": "minecraft:block/grass_block_snow"
        }
      ]
    },
    {
      "apply": [
        {
          "model": "eclipticseasons:block/grass_block_overlay"
        },
        {
          "y": 90,
          "model": "eclipticseasons:block/grass_block_overlay"
        },
        {
          "y": 180,
          "model": "eclipticseasons:block/grass_block_overlay"
        },
        {
          "y": 270,
          "model": "eclipticseasons:block/grass_block_overlay"
        }
      ]
    }
  ]
}
```

## Additional Settings

Sometimes, we need the model to completely replace the original model instead of simple overlay. In this case, use the `replace` property to achieve replacement.

```json
{
  "....": "....",
  "replace": true
}
```
