## 群系天气（局部天气）

与原版全局天气或者现有大多数局部气候不同的是，节气天气依赖群系进行分野，不同群系除非特殊标记，否则拥有独立的天气状态。
不同的气候状态，因而决定了不同群系的草木颜色、水热条件。
这样做的另一个好处是，天气状态不会受到区块加载状态的限制，也能直接兼容现有的着色器系统。
而Minecraft区块数据未加载时很难做到流畅修改，这意味仅依靠区块位置无法做到天气同步。

### 群系分类

对于群系，最根本的工作是根据其特性分类别，目前主要分为三种。四季型地区，干湿季地区，恒温地区。此外还有一类特殊群系被标记为小群系，用于附着临近群系气候状态，一般无需考虑。

#### 四季型群系

四季型地区将具有明显的四季变化特征，尤其是温度变化，这将直接影响到水热条件的判定，下表是其对应数据表。
春季从立春的低降雨概率逐渐增加到谷雨的高降雨概率，雷击概率也随之上升；
夏季降雨概率整体较高但波动明显，立夏时达到峰值，大暑时降雨概率略有下降但雷击概率依然较高；
秋季从立秋的中等降雨概率逐渐降低到霜降的低降雨概率，雷击概率也随之减少；
冬季降雨概率整体较低，立冬时略有降水，大雪时降水概率增加，但雷击概率几乎为零。
整体上，降雨和雷击概率随季节变化呈现出明显的周期性规律。

| 季节    | 节气名称 | 英文名称                | 降雨概率（相对） | 雷击概率（相对） |
|:------|:-----|:--------------------|:---------|:---------|
| **春** | 立春   | BEGINNING_OF_SPRING | 0.3F     | -        |
|       | 雨水   | RAIN_WATER          | 0.5F     | 0.08F    |
|       | 惊蛰   | INSECTS_AWAKENING   | 0.55F    | 0.15F    |
|       | 春分   | SPRING_EQUINOX      | 0.5F     | 0.1F     |
|       | 清明   | FRESH_GREEN         | 0.65F    | 0.05F    |
|       | 谷雨   | GRAIN_RAIN          | 0.75F    | -        |
| **夏** | 立夏   | BEGINNING_OF_SUMMER | 0.9F     | -        |
|       | 小满   | LESSER_FULLNESS     | 0.7F     | 0.1F     |
|       | 芒种   | GRAIN_IN_EAR        | 0.6F     | 0.15F    |
|       | 夏至   | SUMMER_SOLSTICE     | 0.7F     | 0.25F    |
|       | 小暑   | LESSER_HEAT         | 0.65F    | 0.2F     |
|       | 大暑   | GREATER_HEAT        | 0.5F     | 0.05F    |
| **秋** | 立秋   | BEGINNING_OF_AUTUMN | 0.42F    | -        |
|       | 处暑   | END_OF_HEAT         | 0.4F     | -        |
|       | 白露   | WHITE_DEW           | 0.35F    | -        |
|       | 秋分   | AUTUMNAL_EQUINOX    | 0.32F    | -        |
|       | 寒露   | COLD_DEW            | 0.3F     | -        |
|       | 霜降   | FIRST_FROST         | 0.25F    | -        |
| **冬** | 立冬   | BEGINNING_OF_WINTER | 0.3F     | -        |
|       | 小雪   | LIGHT_SNOW          | 0.4F     | 0.05F    |
|       | 大雪   | HEAVY_SNOW          | 0.5F     | -        |
|       | 冬至   | WINTER_SOLSTICE     | 0.45F    | -        |
|       | 小寒   | LESSER_COLD         | 0.4F     | -        |
|       | 大寒   | GREATER_COLD        | 0.2F     | -        |

#### 干湿季群系

干湿季是一种季风型气候。由于Minecraft无南北纬度设定，此处设定为在节气的夏季和秋季为雨季，春季和冬季为旱季。
以凸显和四季型群系的差异，如热带草原。

| 季节    | 节气名称 | 英文名称                | 降雨概率（相对） | 雷击概率（相对） |
|:------|:-----|:--------------------|:---------|:---------|
| **春** | -    | -                   | -        | -        |
| **夏** | 立夏   | BEGINNING_OF_SUMMER | 0.3F     | -        |
|       | 小满   | LESSER_FULLNESS     | 0.5F     | 0.1F     |
|       | 芒种   | GRAIN_IN_EAR        | 0.7F     | 0.15F    |
|       | 夏至   | SUMMER_SOLSTICE     | 0.8F     | 0.2F     |
|       | 小暑   | LESSER_HEAT         | 0.95F    | 0.15F    |
|       | 大暑   | GREATER_HEAT        | 0.8F     | 0.1F     |
| **秋** | 立秋   | BEGINNING_OF_AUTUMN | 0.7F     | 0.05F    |
|       | 处暑   | END_OF_HEAT         | 0.6F     | 0.03F    |
|       | 白露   | WHITE_DEW           | 0.5F     | 0.02F    |
|       | 秋分   | AUTUMNAL_EQUINOX    | 0.4F     | 0.02F    |
|       | 寒露   | COLD_DEW            | 0.3F     | 0.01F    |
|       | 霜降   | FIRST_FROST         | 0.25F    | 0.01F    |
| **冬** | -    | -                   | -        | -        |

#### 恒温型群系

恒温型群系并不意味着没有变化，但是通常来说，他们的变化较为平缓。

| 类型 | 英文名称     | 降雨概率（相对） | 雷击概率（相对） |
|:---|:---------|:---------|:---------|
| 无雨 | RAINLESS | -        | -        |
| 干旱 | ARID     | 0.01F    | -        |
| 干燥 | DROUGHTY | 0.1F     | 0.001F   |
| 温和 | SOFT     | 0.3F     | 0.005F   |
| 多雨 | RAINY    | 0.9F     | 0.01F    |

### 雪期

节气中设计下雪时间的长度与群系基础温度息息相关。一般来说，满足下表。

| 温度名称 | 最低界限   | 最高界限   | 下雪开始时期 | 下雪结束时期 |
|:-----|:-------|:-------|:-------|:-------|
| T1   | > 0.95 | -      | 无      | 无      |
| T08  | > 0.8  | ≤ 0.95 | 冬至     | 小寒     |
| T06  | > 0.6  | ≤ 0.8  | 小雪     | 大寒     |
| T05  | > 0.5  | ≤ 0.6  | 立冬     | 大寒     |
| T04  | > 0.4  | ≤ 0.5  | 霜降     | 大寒     |
| T03  | > 0.3  | ≤ 0.4  | 寒露     | 次年立春   |
| T02  | > 0.2  | ≤ 0.3  | 秋分     | 次年雨水   |
| T015 | > 0.15 | ≤ 0.2  | 白露     | 次年惊蛰   |
| T01  | > 0.1  | ≤ 0.15 | 立秋     | 次年清明   |
| T005 | > 0.05 | ≤ 0.1  | 大暑     | 次年谷雨   |
| T001 | > 0.01 | ≤ 0.05 | 小暑     | 次年立夏   |
| T0   | -      | ≤ 0    | 全年     |        |

### 湿润度

此外，群系湿润度则取决于群系实时温度与基础降雨量。

温度类型计算表如下所示：

| 类型 | 英文名称     | 最低界限  | 最高界限  |
|----|:---------|:------|:------|
| 冰冻 | FREEZING | -     | 0.15F |
| 寒冷 | COLD     | 0.15F | 0.4F  |
| 凉爽 | COOL     | 0.4F  | 0.65F |
| 温暖 | WARM     | 0.65F | 0.9F  |
| 炎热 | HOT      | 0.9F  | 1.25F |
| 炙热 | HEAT     | 1.25F | -     |

降雨类型计算表如下：

| 类型 | 英文名称     | 最低界限 | 最高界限 |
|:---|:---------|:-----|:-----|
| 罕见 | RARE     | -    | 0.1F |
| 稀少 | SCARCE   | 0.1F | 0.3F |
| 适中 | MODERATE | 0.3F | 0.6F |
| 充足 | ADEQUATE | 0.6F | 0.8F |
| 丰富 | ABUNDANT | 0.8F | -    |

湿度有如下计算公式：
```湿度等级 = max(0, 降雨量等级 - |降雨量等级 - 温度等级| / 2)```

| 类型 | 英文名称    |
|:---|:--------|
| 干旱 | ARID    |
| 干燥 | DRY     |
| 一般 | AVERAGE |
| 湿润 | MOIST   |
| 潮湿 | HUMID   |

群系初始状态可以参考如下表格：

#### Minecraft群系湿润度

| 群系      | id                                 | 湿润度等级 | 英文名称    |
|---------|------------------------------------|-------|---------|
| 竹林      | minecraft:bamboo_jungle            | 潮湿    | HUMID   |
| 丛林      | minecraft:jungle                   | 潮湿    | HUMID   |
| 红树林沼泽   | minecraft:mangrove_swamp           | 潮湿    | HUMID   |
| 蘑菇岛     | minecraft:mushroom_fields          | 潮湿    | HUMID   |
| 沼泽      | minecraft:swamp                    | 潮湿    | HUMID   |
| 樱花树林    | minecraft:cherry_grove             | 湿润    | MOIST   |
| 黑森林     | minecraft:dark_forest              | 湿润    | MOIST   |
| 繁花森林    | minecraft:flower_forest            | 湿润    | MOIST   |
| 森林      | minecraft:forest                   | 湿润    | MOIST   |
| 草甸      | minecraft:meadow                   | 湿润    | MOIST   |
| 稀疏丛林    | minecraft:sparse_jungle            | 湿润    | MOIST   |
| 沙滩      | minecraft:beach                    | 一般    | AVERAGE |
| 桦木森林    | minecraft:birch_forest             | 一般    | AVERAGE |
| 冷水海洋    | minecraft:cold_ocean               | 一般    | AVERAGE |
| 冷水深海    | minecraft:deep_cold_ocean          | 一般    | AVERAGE |
| 深暗之域    | minecraft:deep_dark                | 一般    | AVERAGE |
| 冰冻深海    | minecraft:deep_frozen_ocean        | 一般    | AVERAGE |
| 温水深海    | minecraft:deep_lukewarm_ocean      | 一般    | AVERAGE |
| 深海      | minecraft:deep_ocean               | 一般    | AVERAGE |
| 溶洞      | minecraft:dripstone_caves          | 一般    | AVERAGE |
| 冰封山峰    | minecraft:frozen_peaks             | 一般    | AVERAGE |
| 雪林      | minecraft:grove                    | 一般    | AVERAGE |
| 尖峭山峰    | minecraft:jagged_peaks             | 一般    | AVERAGE |
| 温水海洋    | minecraft:lukewarm_ocean           | 一般    | AVERAGE |
| 繁茂洞穴    | minecraft:lush_caves               | 一般    | AVERAGE |
| 海洋      | minecraft:ocean                    | 一般    | AVERAGE |
| 原始桦木森林  | minecraft:old_growth_birch_forest  | 一般    | AVERAGE |
| 原始松木针叶林 | minecraft:old_growth_pine_taiga    | 一般    | AVERAGE |
| 原始云杉针叶林 | minecraft:old_growth_spruce_taiga  | 一般    | AVERAGE |
| 平原      | minecraft:plains                   | 一般    | AVERAGE |
| 河流      | minecraft:river                    | 一般    | AVERAGE |
| 积雪山坡    | minecraft:snowy_slopes             | 一般    | AVERAGE |
| 向日葵平原   | minecraft:sunflower_plains         | 一般    | AVERAGE |
| 针叶林     | minecraft:taiga                    | 一般    | AVERAGE |
| 暖水海洋    | minecraft:warm_ocean               | 一般    | AVERAGE |
| 冻洋      | minecraft:frozen_ocean             | 干燥    | DRY     |
| 冻河      | minecraft:frozen_river             | 干燥    | DRY     |
| 冰刺之地    | minecraft:ice_spikes               | 干燥    | DRY     |
| 积雪沙滩    | minecraft:snowy_beach              | 干燥    | DRY     |
| 雪原      | minecraft:snowy_plains             | 干燥    | DRY     |
| 积雪针叶林   | minecraft:snowy_taiga              | 干燥    | DRY     |
| 石岸      | minecraft:stony_shore              | 干燥    | DRY     |
| 风袭森林    | minecraft:windswept_forest         | 干燥    | DRY     |
| 风袭沙砾丘陵  | minecraft:windswept_gravelly_hills | 干燥    | DRY     |
| 风袭丘陵    | minecraft:windswept_hills          | 干燥    | DRY     |
| 恶地      | minecraft:badlands                 | 干旱    | ARID    |
| 沙漠      | minecraft:desert                   | 干旱    | ARID    |
| 风蚀恶地    | minecraft:eroded_badlands          | 干旱    | ARID    |
| 热带草原    | minecraft:savanna                  | 干旱    | ARID    |
| 热带高原    | minecraft:savanna_plateau          | 干旱    | ARID    |
| 裸岩山峰    | minecraft:stony_peaks              | 干旱    | ARID    |
| 风袭热带草原  | minecraft:windswept_savanna        | 干旱    | ARID    |
| 疏林恶地    | minecraft:wooded_badlands          | 干旱    | ARID    |

#### BOP群系湿润度

| 群系                   | id                                    | 湿润度等级 | 英文名称    |
|----------------------|---------------------------------------|-------|---------|
| 长沼                   | biomesoplenty:bayou                   | 潮湿    | HUMID   |
| 泛滥平原                 | biomesoplenty:floodplain              | 潮湿    | HUMID   |
| 真菌丛林                 | biomesoplenty:fungal_jungle           | 潮湿    | HUMID   |
| 玉石悬崖                 | biomesoplenty:jade_cliffs             | 潮湿    | HUMID   |
| 雨林                   | biomesoplenty:rainforest              | 潮湿    | HUMID   |
| 裸岩雨林                 | biomesoplenty:rocky_rainforest        | 潮湿    | HUMID   |
| 热带地区                 | biomesoplenty:tropics                 | 潮湿    | HUMID   |
| 草原                   | biomesoplenty:grassland               | 湿润    | MOIST   |
| Jacaranda Glade      | biomesoplenty:jacaranda_glade         | 湿润    | MOIST   |
| 薰衣草花田                | biomesoplenty:lavender_field          | 湿润    | MOIST   |
| 草沼                   | biomesoplenty:marsh                   | 湿润    | MOIST   |
| 神秘森林                 | biomesoplenty:mystic_grove            | 湿润    | MOIST   |
| Overgrown Greens     | biomesoplenty:overgrown_greens        | 湿润    | MOIST   |
| 湿地                   | biomesoplenty:wetland                 | 湿润    | MOIST   |
| Aspen Glade          | biomesoplenty:aspen_glade             | 一般    | AVERAGE |
| 泥潭                   | biomesoplenty:bog                     | 一般    | AVERAGE |
| 冷杉针叶林                | biomesoplenty:coniferous_forest       | 一般    | AVERAGE |
| 峭壁                   | biomesoplenty:crag                    | 一般    | AVERAGE |
| 沙丘海滩                 | biomesoplenty:dune_beach              | 一般    | AVERAGE |
| 原野                   | biomesoplenty:field                   | 一般    | AVERAGE |
| 冷杉林空地                | biomesoplenty:fir_clearing            | 一般    | AVERAGE |
| 原野森林                 | biomesoplenty:forested_field          | 一般    | AVERAGE |
| 夜光石窟                 | biomesoplenty:glowing_grotto          | 一般    | AVERAGE |
| Gravel Beach         | biomesoplenty:gravel_beach            | 一般    | AVERAGE |
| 高地                   | biomesoplenty:highland                | 一般    | AVERAGE |
| Hot Springs          | biomesoplenty:hot_springs             | 一般    | AVERAGE |
| 繁茂沙漠                 | biomesoplenty:lush_desert             | 一般    | AVERAGE |
| 繁茂的热带草原              | biomesoplenty:lush_savanna            | 一般    | AVERAGE |
| 枫树林                  | biomesoplenty:maple_woods             | 一般    | AVERAGE |
| Moor                 | biomesoplenty:moor                    | 一般    | AVERAGE |
| 原始林地                 | biomesoplenty:old_growth_woodland     | 一般    | AVERAGE |
| 不祥树林                 | biomesoplenty:ominous_woods           | 一般    | AVERAGE |
| 果园                   | biomesoplenty:orchard                 | 一般    | AVERAGE |
| 根源山谷                 | biomesoplenty:origin_valley           | 一般    | AVERAGE |
| 南瓜园                  | biomesoplenty:pumpkin_patch           | 一般    | AVERAGE |
| 红木森林                 | biomesoplenty:redwood_forest          | 一般    | AVERAGE |
| 季节性森林                | biomesoplenty:seasonal_forest         | 一般    | AVERAGE |
| Snowblossom Grove    | biomesoplenty:snowblossom_grove       | 一般    | AVERAGE |
| 蜘蛛巢穴                 | biomesoplenty:spider_nest             | 一般    | AVERAGE |
| 苔原                   | biomesoplenty:tundra                  | 一般    | AVERAGE |
| 林地                   | biomesoplenty:woodland                | 一般    | AVERAGE |
| 极光花园                 | biomesoplenty:auroral_garden          | 干燥    | DRY     |
| 死寂森林                 | biomesoplenty:dead_forest             | 干燥    | DRY     |
| 苔沼                   | biomesoplenty:muskeg                  | 干燥    | DRY     |
| 原始死寂森林               | biomesoplenty:old_growth_dead_forest  | 干燥    | DRY     |
| 积雪的冷杉针叶林             | biomesoplenty:snowy_coniferous_forest | 干燥    | DRY     |
| 积雪的冷杉林空地             | biomesoplenty:snowy_fir_clearing      | 干燥    | DRY     |
| 积雪的枫树林               | biomesoplenty:snowy_maple_woods       | 干燥    | DRY     |
| Wintry Origin Valley | biomesoplenty:wintry_origin_valley    | 干燥    | DRY     |
| 寒漠                   | biomesoplenty:cold_desert             | 干旱    | ARID    |
| 旱地                   | biomesoplenty:dryland                 | 干旱    | ARID    |
| 地中海森林                | biomesoplenty:mediterranean_forest    | 干旱    | ARID    |
| 牧场                   | biomesoplenty:pasture                 | 干旱    | ARID    |
| 北美草原                 | biomesoplenty:prairie                 | 干旱    | ARID    |
| 裸岩灌丛地                | biomesoplenty:rocky_shrubland         | 干旱    | ARID    |
| 灌木丛林地                | biomesoplenty:scrubland               | 干旱    | ARID    |
| 灌丛                   | biomesoplenty:shrubland               | 干旱    | ARID    |
| 火山平原                 | biomesoplenty:volcanic_plains         | 干旱    | ARID    |
| 火山                   | biomesoplenty:volcano                 | 干旱    | ARID    |
| 荒地                   | biomesoplenty:wasteland               | 干旱    | ARID    |
| Wasteland Steppe     | biomesoplenty:wasteland_steppe        | 干旱    | ARID    |

#### BWG群系湿润度

| 群系        | id                                       | 湿润度等级 | 英文名称    |
|-----------|------------------------------------------|-------|---------|
| 长沼        | biomeswevegone:bayou                     | 潮湿    | HUMID   |
| 峭壁花园      | biomeswevegone:crag_gardens              | 潮湿    | HUMID   |
| 柏木沼泽      | biomeswevegone:cypress_swamplands        | 潮湿    | HUMID   |
| 繁茂林地      | biomeswevegone:overgrowth_woodlands      | 潮湿    | HUMID   |
| 苍白沼泽      | biomeswevegone:pale_bog                  | 潮湿    | HUMID   |
| 红杉木丛      | biomeswevegone:redwood_thicket           | 潮湿    | HUMID   |
| 热带雨林      | biomeswevegone:tropical_rainforest       | 潮湿    | HUMID   |
| 白色红树林沼泽   | biomeswevegone:white_mangrove_marshes    | 潮湿    | HUMID   |
| 玄武岩岩障     | biomeswevegone:basalt_barrera            | 湿润    | MOIST   |
| 黑森林       | biomeswevegone:black_forest              | 湿润    | MOIST   |
| 可可尼诺草甸    | biomeswevegone:coconino_meadow           | 湿润    | MOIST   |
| 乌木森林      | biomeswevegone:ebony_woods               | 湿润    | MOIST   |
| 封魔缠怨之境    | biomeswevegone:enchanted_tangle          | 湿润    | MOIST   |
| 遗忘之森      | biomeswevegone:forgotten_forest          | 湿润    | MOIST   |
| 繁茂交错带     | biomeswevegone:lush_stacks               | 湿润    | MOIST   |
| 果园        | biomeswevegone:orchard                   | 湿润    | MOIST   |
| 彩虹海岸      | biomeswevegone:rainbow_beach             | 湿润    | MOIST   |
| 樱花树林      | biomeswevegone:sakura_grove              | 湿润    | MOIST   |
| 温带树林      | biomeswevegone:temperate_grove           | 湿润    | MOIST   |
| 绒球葱灌丛     | biomeswevegone:allium_shrubland          | 一般    | AVERAGE |
| 苋菜花原野     | biomeswevegone:amaranth_grassland        | 一般    | AVERAGE |
| 北境白杨森林    | biomeswevegone:aspen_boreal              | 一般    | AVERAGE |
| 加拿大地盾     | biomeswevegone:canadian_shield           | 一般    | AVERAGE |
| 锡卡灌丛      | biomeswevegone:cika_woods                | 一般    | AVERAGE |
| 针叶树林      | biomeswevegone:coniferous_forest         | 一般    | AVERAGE |
| 安英岩山岭     | biomeswevegone:dacite_ridges             | 一般    | AVERAGE |
| 安英岩海滨     | biomeswevegone:dacite_shore              | 一般    | AVERAGE |
| 破碎的雨林     | biomeswevegone:fragment_jungle           | 一般    | AVERAGE |
| 寒霜针叶林     | biomeswevegone:frosted_coniferous_forest | 一般    | AVERAGE |
| 覆雪的针叶林    | biomeswevegone:frosted_taiga             | 一般    | AVERAGE |
| 呼啸山巅      | biomeswevegone:howling_peaks             | 一般    | AVERAGE |
| 枫叶针叶林     | biomeswevegone:maple_taiga               | 一般    | AVERAGE |
| 南瓜山谷      | biomeswevegone:pumpkin_valley            | 一般    | AVERAGE |
| 玫瑰原野      | biomeswevegone:rose_fields               | 一般    | AVERAGE |
| 破碎的冰川     | biomeswevegone:shattered_glacier         | 一般    | AVERAGE |
| 天际山谷      | biomeswevegone:skyrise_vale              | 一般    | AVERAGE |
| 巫术垂柳之森    | biomeswevegone:weeping_witch_forest      | 一般    | AVERAGE |
| 榉木森林      | biomeswevegone:zelkova_forest            | 一般    | AVERAGE |
| 被侵蚀的北境    | biomeswevegone:eroded_borealis           | 干燥    | DRY     |
| 鸟尾花常绿灌木群落 | biomeswevegone:firecracker_chaparral     | 干燥    | DRY     |
| 蓝花楹丛林     | biomeswevegone:jacaranda_jungle          | 干燥    | DRY     |
| 热带南洋杉疏林草原 | biomeswevegone:araucaria_savanna         | 干旱    | ARID    |
| 阿塔卡玛旱地    | biomeswevegone:atacama_outback           | 干旱    | ARID    |
| 猴面包疏林草原   | biomeswevegone:baobab_savanna            | 干旱    | ARID    |
| 绯红苔原      | biomeswevegone:crimson_tundra            | 干旱    | ARID    |
| 死海        | biomeswevegone:dead_sea                  | 干旱    | ARID    |
| 铁色木蘑菇石交错带 | biomeswevegone:ironwood_gour             | 干旱    | ARID    |
| 莫哈韦沙漠     | biomeswevegone:mojave_desert             | 干旱    | ARID    |
| 原野        | biomeswevegone:prairie                   | 干旱    | ARID    |
| 红岩山谷      | biomeswevegone:red_rock_valley           | 干旱    | ARID    |
| 崎岖恶地      | biomeswevegone:rugged_badlands           | 干旱    | ARID    |
| 锯齿恶地      | biomeswevegone:sierra_badlands           | 干旱    | ARID    |
| 风袭沙漠      | biomeswevegone:windswept_desert          | 干旱    | ARID    |

#### Nature's Spirit自然之灵湿润度

| 群系      | id                                   | 湿润度等级 | 英文名称    |
|---------|--------------------------------------|-------|---------|
| 草本沼泽    | natures_spirit:marsh                 | 潮湿    | humid   |
| 竹林湿地    | natures_spirit:bamboo_wetlands       | 潮湿    | humid   |
| 紫藤树林    | natures_spirit:wisteria_forest       | 湿润    | moist   |
| 繁花山脊    | natures_spirit:floral_ridges         | 湿润    | moist   |
| 热带森林    | natures_spirit:tropical_woods        | 一般    | average |
| 热带海岸    | natures_spirit:tropical_shores       | 一般    | average |
| 热带盆地    | natures_spirit:tropical_basin        | 一般    | average |
| 雪顶红岩山峰  | natures_spirit:snowcapped_red_peaks  | 一般    | average |
| 积雪雪坡    | natures_spirit:sleeted_slopes        | 一般    | average |
| 红杉林     | natures_spirit:redwood_forest        | 一般    | average |
| 帚石楠花地   | natures_spirit:heather_fields        | 一般    | average |
| 静谧针叶林   | natures_spirit:coniferous_covert     | 一般    | average |
| 高山高地    | natures_spirit:alpine_highlands      | 一般    | average |
| 高山空地    | natures_spirit:alpine_clearings      | 一般    | average |
| 风蚀柳杉林   | natures_spirit:windswept_sugi_forest | 干燥    | dry     |
| 白崖      | natures_spirit:white_cliffs          | 干燥    | dry     |
| 苔原      | natures_spirit:tundra                | 干燥    | dry     |
| 柳杉树林    | natures_spirit:sugi_forest           | 干燥    | dry     |
| 稀疏桃花心森林 | natures_spirit:sparse_tropical_woods | 干燥    | dry     |
| 积雪冷杉林   | natures_spirit:snowy_fir_forest      | 干燥    | dry     |
| 大草原     | natures_spirit:prairie               | 干燥    | dry     |
| 万寿菊草甸   | natures_spirit:marigold_meadows      | 干燥    | dry     |
| 枫树林     | natures_spirit:maple_woodlands       | 干燥    | dry     |
| 金色荒野    | natures_spirit:golden_wilds          | 干燥    | dry     |
| 冷杉树林    | natures_spirit:fir_forest            | 干燥    | dry     |
| 凌寒叶针林   | natures_spirit:boreal_taiga          | 干燥    | dry     |
| 樱花柳杉林   | natures_spirit:blooming_sugi_forest  | 干燥    | dry     |
| 白杨树林    | natures_spirit:aspen_forest          | 干燥    | dry     |
| 干燥疏灌丛平原 | natures_spirit:xeric_plains          | 干旱    | arid    |
| 高原森林    | natures_spirit:woody_highlands       | 干旱    | arid    |
| 疏林旱地    | natures_spirit:wooded_drylands       | 干旱    | arid    |
| 层蚀沙漠    | natures_spirit:stratified_desert     | 干旱    | arid    |
| 灌木林     | natures_spirit:shrubland             | 干旱    | arid    |
| 高原灌木林   | natures_spirit:shrubby_highlands     | 干旱    | arid    |
| 焦草丘陵    | natures_spirit:scorched_dunes        | 干旱    | arid    |
| 红岩山峰    | natures_spirit:red_peaks             | 干旱    | arid    |
| 橡木热带草原  | natures_spirit:oak_savanna           | 干旱    | arid    |
| 生机沙丘    | natures_spirit:lively_dunes          | 干旱    | arid    |
| 薰衣草原野   | natures_spirit:lavender_fields       | 干旱    | arid    |
| 繁花灌木林   | natures_spirit:flowering_shrubland   | 干旱    | arid    |
| 岩土山坡    | natures_spirit:dusty_slopes          | 干旱    | arid    |
| 旱地      | natures_spirit:drylands              | 干旱    | arid    |
| 柏树原野    | natures_spirit:cypress_fields        | 干旱    | arid    |
| 灌木地     | natures_spirit:chaparral             | 干旱    | arid    |
| 雪松灌木林   | natures_spirit:cedar_thicket         | 干旱    | arid    |
| 康乃馨原野   | natures_spirit:carnation_fields      | 干旱    | arid    |
| 繁花山丘    | natures_spirit:blooming_highlands    | 干旱    | arid    |
| 繁花沙丘    | natures_spirit:blooming_dunes        | 干旱    | arid    |
| 热带草原旱地  | natures_spirit:arid_savanna          | 干旱    | arid    |
| 高原旱地    | natures_spirit:arid_highlands        | 干旱    | arid    |

### 群系颜色

节气将影响群系的实际草木颜色表现，主要是影响四季群系和干湿季群系。
此外，也会对桦树、云杉、红树林的颜色做一些调整，但由于机制不同，这些方块对群系过渡支持有限。



