## Data Packs

Supporting data packs is a foundational feature for Minecraft mods, and this is no exception for Ecliptic Seasons. Due
to limited development time, only a few data pack types are currently supported.

## Biome Weather (Localized Weather)

Biome data support begins with setting up classification. For more advanced requirements, see the later sections (
reference only, not yet supported).

### Biome Classification

The fundamental task for biomes is classification by characteristics. This system is based on the Biome Tag platform and
currently includes three main categories: seasonal, monsoonal, and thermally stable. In addition, there is a special
category called "small biomes," which inherit climate states from adjacent biomes and are generally ignored for
customization.

| Tag                         | Name        | Auto Assignment Logic          | Priority | Rainfall (Relative) |
|-----------------------------|-------------|--------------------------------|----------|---------------------|
| `eclipticseasons:seasonal`  | Seasonal    | Based on Overworld status      | Lowest   | Varies with season  |
| `eclipticseasons:monsoonal` | Monsoonal   | Based on tropical savanna type | Medium   | Distinct wet/dry    |
| `eclipticseasons:rainless`  | Rainless    | Based on forecast availability | High     | No rainfall         |
| `eclipticseasons:arid`      | Arid        | Based on rainfall amount       | High     | 0.01F               |
| `eclipticseasons:droughty`  | Droughty    | Based on rainfall amount       | High     | 0.1F                |
| `eclipticseasons:soft`      | Soft        | Based on rainfall amount       | High     | 0.3F                |
| `eclipticseasons:rainy`     | Rainy       | Based on rainfall amount       | High     | 0.9F                |
| `eclipticseasons:is_small`  | Small Biome | No                             | Highest  | Inherit from nearby |

By default, the `seasonal` tag is assigned to all Overworld biomes. If other tags are assigned, those features take
precedence.

By default, `monsoonal` is only assigned to tropical savanna.

If no tags are assigned, biomes will automatically be treated as thermally stable types based on their existing rainfall
values—no need to explicitly configure this.

## Seasonal Crops and Humidity Conditions

This section is covered in more detail in the Agriculture chapter. By default, unregistered crops are not assigned any
control tags and behave as if tagged with both `eclipticseasons:crops/all_seasons` and
`eclipticseasons:crops/arid_humid`.

If the configuration `Crop.RegisterCropDefaultValue` is enabled, then all subclasses of `CropBlock` without tags will
default to having the seasons **Spring + Summer + Autumn** and humidity range **Moderate to Humid**.

### Season Types

Crop seasonal growth control now supports a richer set of data pack options, replacing the older simple configuration system. However, you may still use the system described here if preferred.

By default, out-of-season crops may still grow at a reduced rate during adjacent or similar seasons. For example, crops suited for summer and autumn may grow slowly in spring. However, they will not grow at all during winter.

| Type Name                                    | Supported Seasons        |
|----------------------------------------------|--------------------------|
| `eclipticseasons:crops/spring`               | Spring                   |
| `eclipticseasons:crops/summer`               | Summer                   |
| `eclipticseasons:crops/autumn`               | Autumn                   |
| `eclipticseasons:crops/winter`               | Winter                   |
| `eclipticseasons:crops/spring_summer`        | Spring + Summer          |
| `eclipticseasons:crops/spring_autumn`        | Spring + Autumn          |
| `eclipticseasons:crops/spring_winter`        | Spring + Winter          |
| `eclipticseasons:crops/summer_autumn`        | Summer + Autumn          |
| `eclipticseasons:crops/summer_winter`        | Summer + Winter          |
| `eclipticseasons:crops/autumn_winter`        | Autumn + Winter          |
| `eclipticseasons:crops/spring_summer_autumn` | Spring + Summer + Autumn |
| `eclipticseasons:crops/spring_summer_winter` | Spring + Summer + Winter |
| `eclipticseasons:crops/spring_autumn_winter` | Spring + Autumn + Winter |
| `eclipticseasons:crops/summer_autumn_winter` | Summer + Autumn + Winter |
| `eclipticseasons:crops/all_seasons`          | All seasons (year-round) |

---

### Humidity Types

There are only five humidity levels. By default, crops can grow slowly under humidity levels adjacent to their required range. 
However, if the difference is too great, growth will not occur at all.

| Type Name                               | Minimum Humidity | Maximum Humidity |
|-----------------------------------------|------------------|------------------|
| `eclipticseasons:crops/arid_arid`       | Arid             | Arid             |
| `eclipticseasons:crops/arid_dry`        | Arid             | Dry              |
| `eclipticseasons:crops/arid_average`    | Arid             | Average          |
| `eclipticseasons:crops/arid_moist`      | Arid             | Moist            |
| `eclipticseasons:crops/arid_humid`      | Arid             | Humid            |
| `eclipticseasons:crops/dry_dry`         | Dry              | Dry              |
| `eclipticseasons:crops/dry_average`     | Dry              | Average          |
| `eclipticseasons:crops/dry_moist`       | Dry              | Moist            |
| `eclipticseasons:crops/dry_humid`       | Dry              | Humid            |
| `eclipticseasons:crops/average_average` | Average          | Average          |
| `eclipticseasons:crops/average_moist`   | Average          | Moist            |
| `eclipticseasons:crops/average_humid`   | Average          | Humid            |
| `eclipticseasons:crops/moist_moist`     | Moist            | Moist            |
| `eclipticseasons:crops/moist_humid`     | Moist            | Humid            |
| `eclipticseasons:crops/humid_humid`     | Humid            | Humid            |
