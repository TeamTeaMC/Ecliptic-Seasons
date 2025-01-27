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

由此得到枚举表格：

| 温度等级     | 降水量等级    | 湿润度等级   |
|----------|----------|---------|
| HEAT     | ABUNDANT | HUMID   |
| HOT      | ABUNDANT | HUMID   |
| WARM     | ABUNDANT | HUMID   |
| HOT      | ADEQUATE | MOIST   |
| WARM     | ADEQUATE | MOIST   |
| COOL     | ADEQUATE | MOIST   |
| COOL     | ABUNDANT | MOIST   |
| COLD     | ABUNDANT | MOIST   |
| HEAT     | ADEQUATE | AVERAGE |
| WARM     | MODERATE | AVERAGE |
| COOL     | MODERATE | AVERAGE |
| COLD     | MODERATE | AVERAGE |
| COLD     | ADEQUATE | AVERAGE |
| FREEZING | ADEQUATE | AVERAGE |
| FREEZING | ABUNDANT | AVERAGE |
| HEAT     | MODERATE | DRY     |
| HOT      | MODERATE | DRY     |
| COOL     | SCARCE   | DRY     |
| COLD     | SCARCE   | DRY     |
| FREEZING | SCARCE   | DRY     |
| FREEZING | MODERATE | DRY     |
| HEAT     | RARE     | ARID    |
| HEAT     | SCARCE   | ARID    |
| HOT      | RARE     | ARID    |
| HOT      | SCARCE   | ARID    |
| WARM     | RARE     | ARID    |
| WARM     | SCARCE   | ARID    |
| COOL     | RARE     | ARID    |
| COLD     | RARE     | ARID    |
| FREEZING | RARE     | ARID    |

| 群系      | id                                 | 湿润度等级 | 中文      |
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

### 群系颜色

节气将影响群系的实际草木颜色表现，主要是影响四季群系和干湿季群系。
此外，也会对桦树、云杉、红树林的颜色做一些调整，但由于机制不同，这些方块对群系过渡支持有限。



