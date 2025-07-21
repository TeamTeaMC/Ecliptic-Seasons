## Basic Description

Fallen leaves particles are a client-side only feature implemented via resource packs, used to customize falling leaf
particle effects for specific blocks.

These are JSON files located at the root of the resource pack under the path:
`assets/<namespace>/eclipticseasons/particles/fallen_leaves`.

## File Contents

### Definition Example

Below is a test example for modifying the falling cherry leaf particles.
If you do **not** want to override the original particles, simply omit the `replace` field.

In this example, since we don’t need coloring, we set `default` to `-1` and use `source: "custom"` to disable biome
tinting.
If coloring is needed, just provide the required parameters as specified.

The `colors`, `weights`, and `sprites` fields all support the **SolarTermMap** format, which includes:

* `default`: applies year-round
* `seasons`: per-season values
* `solar_terms`: per solar term values

```json
{
  "block": {
    "blocks": "minecraft:cherry_leaves"
  },
  "location": {
    "biomes": "#eclipticseasons:seasonal"
  },
  "replace": true,
  "colors": {
    "default": {
      "color": -1
    }
  },
  "sprites": {
    "solar_terms": {
      "beginning_of_autumn": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "end_of_heat": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "white_dew": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "autumnal_equinox": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "cold_dew": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "first_frost": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ]
    }
  },
  "weights": {
    "solar_terms": {
      "beginning_of_autumn": 6,
      "end_of_heat": 7,
      "white_dew": 8,
      "autumnal_equinox": 10,
      "cold_dew": 12,
      "first_frost": 16
    }
  },
  "source": "custom"
}
```
