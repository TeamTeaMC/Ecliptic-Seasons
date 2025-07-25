> Note: Crop data packs have the highest priority. Since we also use crop tags, you must either use the `replace` property
> to override them, or define the blocks within a crop datapack to ensure proper behavior.

## Basic Description

Crop growth is used to adjust the growth speed of blocks. It is primarily handled via the Neo/Forge event pipeline and
supports general crop block growth, sapling growth, and bone meal triggers.
For blocks that do **not** support Neo/Forge events, you can enable **force compatibility mode** in the configuration or
add their block IDs to the `eclipticseasons:natural_plants` tag.
Growth speed is adjusted by limiting random ticks then.

It is divided into two parts: **seasonal conditions** and **humidity conditions**.

These are JSON files located at the root of the data pack under the path:
`data/<namespace>/eclipticseasons/crop`.

## File Contents

Crop growth is one of the earlier data-driven systems, and due to maintenance considerations,
it does **not** directly support the newer `SolarTermMap`, so the results may differ slightly — though the logic remains
fundamentally similar.

In most cases, the **tag system** is sufficient.
Only consider using custom crop files when you need to target specific blocks or block states.

### Definition Example

Below is an example where crop growth is controlled based on whether the sunflower is the **upper half**.
Since we don't need to manually define growth parameters, we can use `parent` to inherit existing growth configurations.

For climate-related settings, it is recommended to default to the **temperate** zone, which maps to various agricultural
climate zones internally.
Optionally, you may define parameters per climate zone.

```json
{
  "parent": [
    "eclipticseasons:seasons/spring_summer",
    "eclipticseasons:humidity/average_moist"
  ],
  "apply_target": {
    "blocks": "minecraft:sunflower",
    "state": {
      "half": "upper"
    }
  },
  "climate": "eclipticseasons:temperate"
}
```

### Growth Parameters

If you've explored the built-in presets like `eclipticseasons:seasons/xx`, you may want to customize some growth
parameters.

You can configure:

* `grow_chance` (default: `1`) — growth speed
* `fertile_chance` (default: `1`) — bone meal success chance
* `death_chance` (default: `0`) — chance of dying if growth fails
* `dead_state` — the block state to switch to when the plant dies (default: dead bush)

If you want to define a `dead_state` with specific `BlockState` values, refer to Minecraft's block state JSON format.
Note: some parameter keys are capitalized due to historical reasons.
All parameters are **optional**.

Setting values above `1.0` **requires Neo/Forge event support** to have an enhancing effect.
However, since both **season** and **humidity** factors are applied multiplicatively, slight values above `1.0` can
still make a difference even for blocks without event support.

```json
{
  "grow_chance": 0.9,
  "fertile_chance": 0.8,
  "death_chance": 0.01,
  "dead_state": {
    "Name": "minecraft:wheat",
    "Properties": {
      "age": "1"
    }
  }
}
```
