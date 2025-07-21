## Basic Description

Biome seasonal colors are a client-side only feature implemented via resource packs.
They are used when the default seasonal color transitions for biomes are unsatisfactory.

These are JSON files located at the root of the resource pack under the path:
`assets/<namespace>/eclipticseasons/biome_colors`.

## File Contents

### Definition Example

Below is a test example used to modify the seasonal colors of the plains biome.
For smoother transitions between seasons, it is recommended to use the **24 solar terms** variant instead.
Additionally, the `mix` parameter can be used to blend custom colors with the original biome colors proportionally. This
reduces the need to define colors for every biome while preserving visual variety.

```json
{
  "biomes": "minecraft:plains",
  "grass_colors": {
    "seasons": {
      "spring": {
        "color": -20561
      },
      "summer": {
        "color": -16711936
      },
      "autumn": {
        "color": -14336
      },
      "winter": {
        "color": -16776961
      }
    }
  }
}
```

### Parameter Description

#### Parameter: `biomes` 【String | Object】

Used to match target biomes.
Supports biome tags, biome ID strings, or a `HolderSet`.

#### Parameters: `grass_colors`【Solar Term Map】, `foliage_colors`【Solar Term Map】

These define the seasonal colors of grass and foliage.

**Optional sub-parameters: `default`【Color】, `seasons`【Map → Color】, `solar_terms`【Map → Color】**

You can choose to define colors by **seasons** or by **solar terms**.
Each object can contain the following fields:

* `color`: Integer-based color code
* `mix`: A float value from `0.0` to `1.0` representing blending ratio
* `color_string`: Color string in formats like `#5bae23`
