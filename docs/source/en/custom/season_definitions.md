## Basic Explanation

Season models are a client-only resource pack feature, used when you don't want blocks to actually undergo seasonal changes in the game.

These are JSON files placed in the resource pack root path at `assets/<namespace>/eclipticseasons/season_definitions`.

## File Content

Before reading this section, consider how many changes you want your blocks to have. Common cases include seasonal overlay models or block model switching.  
Design according to your needs, avoiding excessive or abrupt changes.

### Definition Example

Below is an example of a summer lotus bloom change: the lotus gradually fills out as summer arrives and then gradually disappears in autumn.

```json
{
  "blocks": "minecraft:lily_pad",
  "biomes": "#eclipticseasons:seasonal",
  "slices": [
    {
      "transition_models": [
        "eclipticseasons:empty",
        "eclipticseasons:lily_pad_common"
      ],
      "solar_term": "grain_rain"
    },
    {
      "mid": "eclipticseasons:lily_pad_common",
      "start": "beginning_of_summer",
      "end": "greater_heat"
    },
    {
      "transition_models": [
        "eclipticseasons:lily_pad_common",
        "eclipticseasons:empty"
      ],
      "solar_term": "beginning_of_autumn"
    }
  ]
}
```

### Parameter Explanation

#### Parameter: blocks【String|Object】

Defines the block target, which can be an ID, a tag (starting with `#`), or a HolderSet. HolderSet is complex, so tags or IDs are generally recommended.

#### Parameter: biomes【String|Object】

Defines the biome check, supporting the same three forms as `blocks`.

#### Parameter: slices【Object Array】

Means "slices", used to match seasonal time segments with models; this should be an array of objects.

**Optional Sub-parameters: start【String】, end【String】, solar_term【String】**

If time is divided by solar terms, these three can be used to specify the start, end, or a unique solar term. Use lowercase IDs. Refer to the Climate section for exact names.

**Optional Sub-parameters: start_season【String】, end_season【String】, season【String】**

If you prefer to distinguish by seasons, these three parameters can be used. Use lowercase IDs. Note the difference: biome seasonal minimum granularity is solar terms, while these seasons are dimension seasons, so they may not apply to some special biomes (if you care about seasonal distinctions).

**Optional Sub-parameters: mid【String】, transition_models【String Array】**

If the block appearance does not change within the season, use `mid`. Otherwise, use `transition_models`.
Note that `transition_models` only apply at the first solar term in the interval. Appearance gradually changes over time.
Currently, `transition_models` supports only two models.

**Optional Sub-parameter: empty_above【Bool】**

A special parameter to check if the block above is empty. Only `true` or `false` are valid.
