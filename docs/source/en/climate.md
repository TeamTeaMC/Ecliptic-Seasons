## Biome Weather (Localized Weather)

Unlike vanilla global weather or most existing local climate systems, Solar Term Weather is defined by biome. Different
biomes, unless specifically marked, maintain independent weather states.

These varying climate states determine vegetation color and hydrothermal conditions in each biome. Another benefit is
that weather states are not limited by chunk loading and can directly support shader systems. In Minecraft, modifying
chunk data smoothly without loading is difficult, so weather synchronization cannot rely solely on chunk positions.

### Biome Classification

Biomes are primarily categorized into three types: Four-Season Regions, Wet-Dry Regions, and Thermal Constant Regions.
Additionally, Mini Biomes inherit the climate state of adjacent biomes and are typically ignored.

#### Four-Season Biomes

These biomes exhibit significant seasonal variation, especially in temperature, affecting hydrothermal conditions. The
table below outlines seasonal rainfall and thunder probabilities:

* Spring: Rainfall increases from Beginning of Spring to Grain Rain; thunder increases accordingly.
* Summer: High rainfall with fluctuation, peaking at Beginning of Summer; slight decrease by Greater Heat but thunder
  remains high.
* Autumn: Rainfall drops from Beginning of Autumn to First Frost, with declining thunder.
* Winter: Low rainfall, slight increase during Heavy Snow; thunder is nearly absent.

| Season | Solar Term          | Rainfall | Thunder |
|--------|---------------------|----------|---------|
| Spring | BEGINNING_OF_SPRING | 0.3F     | -       |
|        | RAIN_WATER          | 0.5F     | 0.08F   |
|        | INSECTS_AWAKENING   | 0.55F    | 0.15F   |
|        | SPRING_EQUINOX      | 0.5F     | 0.1F    |
|        | FRESH_GREEN         | 0.65F    | 0.05F   |
|        | GRAIN_RAIN          | 0.75F    | -       |
| Summer | BEGINNING_OF_SUMMER | 0.9F     | -       |
|        | LESSER_FULLNESS     | 0.7F     | 0.1F    |
|        | GRAIN_IN_EAR        | 0.6F     | 0.15F   |
|        | SUMMER_SOLSTICE     | 0.7F     | 0.25F   |
|        | LESSER_HEAT         | 0.65F    | 0.2F    |
|        | GREATER_HEAT        | 0.5F     | 0.05F   |
| Autumn | BEGINNING_OF_AUTUMN | 0.42F    | -       |
|        | END_OF_HEAT         | 0.4F     | -       |
|        | WHITE_DEW           | 0.35F    | -       |
|        | AUTUMNAL_EQUINOX    | 0.32F    | -       |
|        | COLD_DEW            | 0.3F     | -       |
|        | FIRST_FROST         | 0.25F    | -       |
| Winter | BEGINNING_OF_WINTER | 0.3F     | -       |
|        | LIGHT_SNOW          | 0.4F     | 0.05F   |
|        | HEAVY_SNOW          | 0.5F     | -       |
|        | WINTER_SOLSTICE     | 0.45F    | -       |
|        | LESSER_COLD         | 0.4F     | -       |
|        | GREATER_COLD        | 0.2F     | -       |

#### Wet-Dry Biomes

Defined as a monsoon-type climate. Due to Minecraft's lack of latitude, the wet season spans summer and autumn; spring
and winter are dry. This highlights the contrast with Four-Season biomes, as in tropical savannas.

| Season | Solar Term          | Rainfall | Thunder |
|--------|---------------------|----------|---------|
| Spring | -                   | -        | -       |
| Summer | BEGINNING_OF_SUMMER | 0.3F     | -       |
|        | LESSER_FULLNESS     | 0.5F     | 0.1F    |
|        | GRAIN_IN_EAR        | 0.7F     | 0.15F   |
|        | SUMMER_SOLSTICE     | 0.8F     | 0.2F    |
|        | LESSER_HEAT         | 0.95F    | 0.15F   |
|        | GREATER_HEAT        | 0.8F     | 0.1F    |
| Autumn | BEGINNING_OF_AUTUMN | 0.7F     | 0.05F   |
|        | END_OF_HEAT         | 0.6F     | 0.03F   |
|        | WHITE_DEW           | 0.5F     | 0.02F   |
|        | AUTUMNAL_EQUINOX    | 0.4F     | 0.02F   |
|        | COLD_DEW            | 0.3F     | 0.01F   |
|        | FIRST_FROST         | 0.25F    | 0.01F   |
| Winter | -                   | -        | -       |

#### Thermal Constant Biomes

These do change, but more gradually than other types.

| Type     | Rainfall | Thunder |
|----------|----------|---------|
| RAINLESS | -        | -       |
| ARID     | 0.01F    | -       |
| DROUGHTY | 0.1F     | 0.001F  |
| SOFT     | 0.3F     | 0.005F  |
| RAINY    | 0.9F     | 0.01F   |

### Snow Period

Snow periods are determined by a biome's base temperature.

| Temp ID | Range        | Start               | End                 |
|---------|--------------|---------------------|---------------------|
| T1      | >0.95        | None                | None                |
| T08     | (0.8, 0.95]  | WINTER_SOLSTICE     | LESSER_COLD         |
| T06     | (0.6, 0.8]   | LIGHT_SNOW          | GREATER_COLD        |
| T05     | (0.5, 0.6]   | BEGINNING_OF_WINTER | GREATER_COLD        |
| T04     | (0.4, 0.5]   | FIRST_FROST         | GREATER_COLD        |
| T03     | (0.3, 0.4]   | COLD_DEW            | BEGINNING_OF_SPRING |
| T02     | (0.2, 0.3]   | AUTUMNAL_EQUINOX    | RAIN_WATER          |
| T015    | (0.15, 0.2]  | WHITE_DEW           | INSECTS_AWAKENING   |
| T01     | (0.1, 0.15]  | BEGINNING_OF_AUTUMN | FRESH_GREEN         |
| T005    | (0.05, 0.1]  | GREATER_HEAT        | GRAIN_RAIN          |
| T001    | (0.01, 0.05] | LESSER_HEAT         | BEGINNING_OF_SUMMER |
| T0      | <=0          | ALL_YEAR            | -                   |

### Humidity

Humidity depends on real-time biome temperature and base rainfall.

**Temperature Types:**

| Type     | Range         |
|----------|---------------|
| FREEZING | <=0.15F       |
| COLD     | (0.15F, 0.4F] |
| COOL     | (0.4F, 0.65F] |
| WARM     | (0.65F, 0.9F] |
| HOT      | (0.9F, 1.25F] |
| HEAT     | >1.25F        |

**Rainfall Types:**

| Type     | Range        |
|----------|--------------|
| RARE     | <=0.1F       |
| SCARCE   | (0.1F, 0.3F] |
| MODERATE | (0.3F, 0.6F] |
| ADEQUATE | (0.6F, 0.8F] |
| ABUNDANT | >0.8F        |

**Humidity Calculation:**

```
HumidityLevel = max(0, RainfallLevel - |RainfallLevel - TemperatureLevel| / 2)
```

**Humidity Levels:**

| Level   |
|---------|
| ARID    |
| DRY     |
| AVERAGE |
| MOIST   |
| HUMID   |

#### Humidity Reference of Minecraft Biome

| Biome Name               | ID                                 | Humidity Level | English Label |
|--------------------------|------------------------------------|----------------|---------------|
| Bamboo Jungle            | minecraft:bamboo_jungle            | HUMID          | HUMID         |
| Jungle                   | minecraft:jungle                   | HUMID          | HUMID         |
| Mangrove Swamp           | minecraft:mangrove_swamp           | HUMID          | HUMID         |
| Mushroom Fields          | minecraft:mushroom_fields          | HUMID          | HUMID         |
| Swamp                    | minecraft:swamp                    | HUMID          | HUMID         |
| Cherry Grove             | minecraft:cherry_grove             | MOIST          | MOIST         |
| Dark Forest              | minecraft:dark_forest              | MOIST          | MOIST         |
| Flower Forest            | minecraft:flower_forest            | MOIST          | MOIST         |
| Forest                   | minecraft:forest                   | MOIST          | MOIST         |
| Meadow                   | minecraft:meadow                   | MOIST          | MOIST         |
| Sparse Jungle            | minecraft:sparse_jungle            | MOIST          | MOIST         |
| Beach                    | minecraft:beach                    | AVERAGE        | AVERAGE       |
| Birch Forest             | minecraft:birch_forest             | AVERAGE        | AVERAGE       |
| Cold Ocean               | minecraft:cold_ocean               | AVERAGE        | AVERAGE       |
| Deep Cold Ocean          | minecraft:deep_cold_ocean          | AVERAGE        | AVERAGE       |
| Deep Dark                | minecraft:deep_dark                | AVERAGE        | AVERAGE       |
| Deep Frozen Ocean        | minecraft:deep_frozen_ocean        | AVERAGE        | AVERAGE       |
| Deep Lukewarm Ocean      | minecraft:deep_lukewarm_ocean      | AVERAGE        | AVERAGE       |
| Deep Ocean               | minecraft:deep_ocean               | AVERAGE        | AVERAGE       |
| Dripstone Caves          | minecraft:dripstone_caves          | AVERAGE        | AVERAGE       |
| Frozen Peaks             | minecraft:frozen_peaks             | AVERAGE        | AVERAGE       |
| Grove                    | minecraft:grove                    | AVERAGE        | AVERAGE       |
| Jagged Peaks             | minecraft:jagged_peaks             | AVERAGE        | AVERAGE       |
| Lukewarm Ocean           | minecraft:lukewarm_ocean           | AVERAGE        | AVERAGE       |
| Lush Caves               | minecraft:lush_caves               | AVERAGE        | AVERAGE       |
| Ocean                    | minecraft:ocean                    | AVERAGE        | AVERAGE       |
| Old Growth Birch Forest  | minecraft:old_growth_birch_forest  | AVERAGE        | AVERAGE       |
| Old Growth Pine Taiga    | minecraft:old_growth_pine_taiga    | AVERAGE        | AVERAGE       |
| Old Growth Spruce Taiga  | minecraft:old_growth_spruce_taiga  | AVERAGE        | AVERAGE       |
| Plains                   | minecraft:plains                   | AVERAGE        | AVERAGE       |
| River                    | minecraft:river                    | AVERAGE        | AVERAGE       |
| Snowy Slopes             | minecraft:snowy_slopes             | AVERAGE        | AVERAGE       |
| Sunflower Plains         | minecraft:sunflower_plains         | AVERAGE        | AVERAGE       |
| Taiga                    | minecraft:taiga                    | AVERAGE        | AVERAGE       |
| Warm Ocean               | minecraft:warm_ocean               | AVERAGE        | AVERAGE       |
| Frozen Ocean             | minecraft:frozen_ocean             | DRY            | DRY           |
| Frozen River             | minecraft:frozen_river             | DRY            | DRY           |
| Ice Spikes               | minecraft:ice_spikes               | DRY            | DRY           |
| Snowy Beach              | minecraft:snowy_beach              | DRY            | DRY           |
| Snowy Plains             | minecraft:snowy_plains             | DRY            | DRY           |
| Snowy Taiga              | minecraft:snowy_taiga              | DRY            | DRY           |
| Stony Shore              | minecraft:stony_shore              | DRY            | DRY           |
| Windswept Forest         | minecraft:windswept_forest         | DRY            | DRY           |
| Windswept Gravelly Hills | minecraft:windswept_gravelly_hills | DRY            | DRY           |
| Windswept Hills          | minecraft:windswept_hills          | DRY            | DRY           |
| Badlands                 | minecraft:badlands                 | ARID           | ARID          |
| Desert                   | minecraft:desert                   | ARID           | ARID          |
| Eroded Badlands          | minecraft:eroded_badlands          | ARID           | ARID          |
| Savanna                  | minecraft:savanna                  | ARID           | ARID          |
| Savanna Plateau          | minecraft:savanna_plateau          | ARID           | ARID          |
| Stony Peaks              | minecraft:stony_peaks              | ARID           | ARID          |
| Windswept Savanna        | minecraft:windswept_savanna        | ARID           | ARID          |
| Wooded Badlands          | minecraft:wooded_badlands          | ARID           | ARID          |

#### Biome Humidity: Biomes O' Plenty

| Biome Name              | ID                                    | Humidity Level | English Label |
|-------------------------|---------------------------------------|----------------|---------------|
| Bayou                   | biomesoplenty:bayou                   | HUMID          | HUMID         |
| Floodplain              | biomesoplenty:floodplain              | HUMID          | HUMID         |
| Fungal Jungle           | biomesoplenty:fungal_jungle           | HUMID          | HUMID         |
| Jade Cliffs             | biomesoplenty:jade_cliffs             | HUMID          | HUMID         |
| Rainforest              | biomesoplenty:rainforest              | HUMID          | HUMID         |
| Rocky Rainforest        | biomesoplenty:rocky_rainforest        | HUMID          | HUMID         |
| Tropics                 | biomesoplenty:tropics                 | HUMID          | HUMID         |
| Grassland               | biomesoplenty:grassland               | MOIST          | MOIST         |
| Jacaranda Glade         | biomesoplenty:jacaranda_glade         | MOIST          | MOIST         |
| Lavender Field          | biomesoplenty:lavender_field          | MOIST          | MOIST         |
| Marsh                   | biomesoplenty:marsh                   | MOIST          | MOIST         |
| Mystic Grove            | biomesoplenty:mystic_grove            | MOIST          | MOIST         |
| Overgrown Greens        | biomesoplenty:overgrown_greens        | MOIST          | MOIST         |
| Wetland                 | biomesoplenty:wetland                 | MOIST          | MOIST         |
| Aspen Glade             | biomesoplenty:aspen_glade             | AVERAGE        | AVERAGE       |
| Bog                     | biomesoplenty:bog                     | AVERAGE        | AVERAGE       |
| Coniferous Forest       | biomesoplenty:coniferous_forest       | AVERAGE        | AVERAGE       |
| Crag                    | biomesoplenty:crag                    | AVERAGE        | AVERAGE       |
| Dune Beach              | biomesoplenty:dune_beach              | AVERAGE        | AVERAGE       |
| Field                   | biomesoplenty:field                   | AVERAGE        | AVERAGE       |
| Fir Clearing            | biomesoplenty:fir_clearing            | AVERAGE        | AVERAGE       |
| Forested Field          | biomesoplenty:forested_field          | AVERAGE        | AVERAGE       |
| Glowing Grotto          | biomesoplenty:glowing_grotto          | AVERAGE        | AVERAGE       |
| Gravel Beach            | biomesoplenty:gravel_beach            | AVERAGE        | AVERAGE       |
| Highland                | biomesoplenty:highland                | AVERAGE        | AVERAGE       |
| Hot Springs             | biomesoplenty:hot_springs             | AVERAGE        | AVERAGE       |
| Lush Desert             | biomesoplenty:lush_desert             | AVERAGE        | AVERAGE       |
| Lush Savanna            | biomesoplenty:lush_savanna            | AVERAGE        | AVERAGE       |
| Maple Woods             | biomesoplenty:maple_woods             | AVERAGE        | AVERAGE       |
| Moor                    | biomesoplenty:moor                    | AVERAGE        | AVERAGE       |
| Old Growth Woodland     | biomesoplenty:old_growth_woodland     | AVERAGE        | AVERAGE       |
| Ominous Woods           | biomesoplenty:ominous_woods           | AVERAGE        | AVERAGE       |
| Orchard                 | biomesoplenty:orchard                 | AVERAGE        | AVERAGE       |
| Origin Valley           | biomesoplenty:origin_valley           | AVERAGE        | AVERAGE       |
| Pumpkin Patch           | biomesoplenty:pumpkin_patch           | AVERAGE        | AVERAGE       |
| Redwood Forest          | biomesoplenty:redwood_forest          | AVERAGE        | AVERAGE       |
| Seasonal Forest         | biomesoplenty:seasonal_forest         | AVERAGE        | AVERAGE       |
| Snowblossom Grove       | biomesoplenty:snowblossom_grove       | AVERAGE        | AVERAGE       |
| Spider Nest             | biomesoplenty:spider_nest             | AVERAGE        | AVERAGE       |
| Tundra                  | biomesoplenty:tundra                  | AVERAGE        | AVERAGE       |
| Woodland                | biomesoplenty:woodland                | AVERAGE        | AVERAGE       |
| Auroral Garden          | biomesoplenty:auroral_garden          | DRY            | DRY           |
| Dead Forest             | biomesoplenty:dead_forest             | DRY            | DRY           |
| Muskeg                  | biomesoplenty:muskeg                  | DRY            | DRY           |
| Old Growth Dead Forest  | biomesoplenty:old_growth_dead_forest  | DRY            | DRY           |
| Snowy Coniferous Forest | biomesoplenty:snowy_coniferous_forest | DRY            | DRY           |
| Snowy Fir Clearing      | biomesoplenty:snowy_fir_clearing      | DRY            | DRY           |
| Snowy Maple Woods       | biomesoplenty:snowy_maple_woods       | DRY            | DRY           |
| Wintry Origin Valley    | biomesoplenty:wintry_origin_valley    | DRY            | DRY           |
| Cold Desert             | biomesoplenty:cold_desert             | ARID           | ARID          |
| Dryland                 | biomesoplenty:dryland                 | ARID           | ARID          |
| Mediterranean Forest    | biomesoplenty:mediterranean_forest    | ARID           | ARID          |
| Pasture                 | biomesoplenty:pasture                 | ARID           | ARID          |
| Prairie                 | biomesoplenty:prairie                 | ARID           | ARID          |
| Rocky Shrubland         | biomesoplenty:rocky_shrubland         | ARID           | ARID          |
| Scrubland               | biomesoplenty:scrubland               | ARID           | ARID          |
| Volcanic Plains         | biomesoplenty:volcanic_plains         | ARID           | ARID          |
| Volcano                 | biomesoplenty:volcano                 | ARID           | ARID          |
| Wasteland               | biomesoplenty:wasteland               | ARID           | ARID          |
| Wasteland Steppe        | biomesoplenty:wasteland_steppe        | ARID           | ARID          |

