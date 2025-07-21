## Basic Description

Seasonal textures are a client-side only feature implemented via resource packs.
They are used when you want to visually reflect seasonal changes **without** actually modifying the behavior of the
blocks.

These are JSON files located at the root of the resource pack under the path:
`assets/<namespace>/eclipticseasons/season_textures`.

## File Contents

Seasonal textures are a simplified design of seasonal models, allowing seasonal texture replacement for regular
Minecraft models without the need to create complex model files. Naturally, the functionality is also limited
accordingly.

### Definition Example

Below is an example of replacing the texture of oak leaves in spring with another texture:

```json
{
  "target": "minecraft:block/oak_leaves",
  "biomes": "#eclipticseasons:seasonal",
  "slices": [
    {
      "textures": [
        {
          "all": "minecraft:block/cherry_leaves"
        },
        {
          "all": "minecraft:block/spruce_leaves"
        }
      ],
      "tint": {
        "#all": -1
      },
      "season": "spring"
    },
    {
      "transition_textures": [
        {
          "all": "minecraft:block/cherry_leaves"
        },
        {
          "all": "minecraft:block/spruce_leaves"
        }
      ],
      "tint": {
        "#all": -1
      },
      "season": "summer"
    }
  ]
}
```

### Parameter Descriptions

#### Optional Parameter: `target` 【String | List】

Defines the model to apply the textures to. Can be a model ID or a list of ID strings.
If omitted, it will be automatically inferred from the file path.
For example, if the file is located at `minecraft/eclipticseasons/season_textures/oak_leaves.json`, it will
automatically apply to the model `minecraft:block/oak_leaves`.

#### Parameter: `biomes` 【String | Array】

Specifies the target biomes. Supports biome tags or a list of biome ID strings.

#### Parameter: `slices` 【Object Array】

Represents seasonal "slices" — segments that define model behavior per season or solar term.
The value should be an array of objects.

**Optional Sub-parameters: `start`【String】, `end`【String】, `solar_term`【String】**

When defining time segments using **solar terms**, use these three parameters:

* `start`: starting solar term
* `end`: ending solar term
* `solar_term`: specific single solar term
  Use lowercase IDs; refer to the climate section for valid term names.

**Optional Sub-parameters: `start_season`【String】, `end_season`【String】, `season`【String】**

If you prefer to define time using **seasons**, you may use these parameters.
Use lowercase IDs.
**Note:** Seasonal dimensions are coarser than solar terms, so these parameters may not work reliably in biomes that
rely on fine-grained solar term resolution.

**Optional Sub-parameters: `textures`【Array | Object】, `transition_textures`【Array】**

Use `textures` if the block appearance does **not** change during the slice.
Use `transition_textures` if a visual change should occur at the **first solar term** in the interval.

Important notes:

* If using multiple texture options, wrap them in an array (`[]`)
* `transition_textures` is best written as a **double list** format (e.g., `[[..., ...]]`)

**Optional Sub-parameter: `tint`【Object】**

This special parameter adjusts the tint index.
Set the value to `-1` to **disable tinting** on the corresponding faces.
