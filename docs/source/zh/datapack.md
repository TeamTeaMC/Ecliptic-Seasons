支持数据包是Minecraft模组的基础性工作，对于节气，这也不例外。
本章主要介绍节气的一些标签和进度系统。

## 群系天气（局部天气）

群系数据基础支持为群系设定分类，如果有进一步需求可以看后面的内容（仅参考，暂不支持）。

### 群系分类

对于群系，最根本的工作是根据其特性分类别，该系统基于Biome
Tag平台运行，目前主要分为三种。四季型地区，干湿季地区，恒温地区。此外还有一类特殊群系被标记为小群系，用于附着临近群系气候状态，一般无需考虑。

| 标签                          | 名称   | 是否支持自动分配   | 覆盖优先级 | 降雨概率（相对） |
|-----------------------------|------|------------|-------|----------|
| `eclipticseasons:seasonal`  | 四季型  | 按是否为主世界群系  | 最低    | 随节气变化    |
| `eclipticseasons:monsoonal` | 干湿季型 | 按是否为热带草原群系 | 中     | 雨季旱季分明   |
| `eclipticseasons:rainless`  | 无雨   | 按有无预测      | 高     | 不下雨      |
| `eclipticseasons:arid`      | 干旱   | 按雨量        | 高     | 0.01F    |
| `eclipticseasons:droughty`  | 干燥   | 按雨量        | 高     | 0.1F     |
| `eclipticseasons:soft`      | 温和   | 按雨量        | 高     | 0.3F     |
| `eclipticseasons:rainy`     | 多雨   | 按雨量        | 高     | 0.9F     |
| `eclipticseasons:is_small`  | 小群系  | 否          | 最高    | 根据附近群系   |

默认状态下，四季型标签将分配给所有主世界群系，如果设定了其他类别将应用其他特性特性。

干湿季默认状态下仅分配给热带草原。

如果未设定标签，群系将按现有降雨概率自动分配恒温型标签，因此并不需要刻意设计这一点。

## 季节性作物与湿度条件

这个地方在农业一章有具体的描述，因此不再阐述。
默认状态下，不会为未注册的作物提供控制标签，可以看做`eclipticseasons:crops/all_seasons`和
`eclipticseasons:crops/arid_humid`。
如果打开相关配置`Crop.RegisterCropDefaultValue`，则默认为所有无标记的CropBlock的子类设置为`春+夏+秋`以及`中等-潮湿`的生长条件。

### 季节类型

作物季节生长控制现在有更丰富的数据包选项取代了简单配置，但是您仍然可以用此处系统。
默认情况下，非应季作物有可能在类似季节以低概率生长。如夏秋型作物可以在春天缓慢生长。但是在冬天则不会生长。

| 类型名称                                         | 适用季节         |
|:---------------------------------------------|:-------------|
| `eclipticseasons:crops/spring`               | 春季           |
| `eclipticseasons:crops/summer`               | 夏季           |
| `eclipticseasons:crops/autumn`               | 秋季           |
| `eclipticseasons:crops/winter`               | 冬季           |
| `eclipticseasons:crops/spring_summer`        | 春季 + 夏季      |
| `eclipticseasons:crops/spring_autumn`        | 春季 + 秋季      |
| `eclipticseasons:crops/spring_winter`        | 春季 + 冬季      |
| `eclipticseasons:crops/summer_autumn`        | 夏季 + 秋季      |
| `eclipticseasons:crops/summer_winter`        | 夏季 + 冬季      |
| `eclipticseasons:crops/autumn_winter`        | 秋季 + 冬季      |
| `eclipticseasons:crops/spring_summer_autumn` | 春季 + 夏季 + 秋季 |
| `eclipticseasons:crops/spring_summer_winter` | 春季 + 夏季 + 冬季 |
| `eclipticseasons:crops/spring_autumn_winter` | 春季 + 秋季 + 冬季 |
| `eclipticseasons:crops/summer_autumn_winter` | 夏季 + 秋季 + 冬季 |
| `eclipticseasons:crops/all_seasons`          | 所有季节（全年）     |

------

### 湿度类型

对于湿度而言仅有五个等级，默认设置为作物在临近要求的湿度等级下也可以缓慢生长，过大则不可。

| 类型名称                                    | 最低湿度 | 最高湿度 |
|:----------------------------------------|:-----|:-----|
| `eclipticseasons:crops/arid_arid`       | 干旱   | 干旱   |
| `eclipticseasons:crops/arid_dry`        | 干旱   | 干燥   |
| `eclipticseasons:crops/arid_average`    | 干旱   | 平均   |
| `eclipticseasons:crops/arid_moist`      | 干旱   | 湿润   |
| `eclipticseasons:crops/arid_humid`      | 干旱   | 潮湿   |
| `eclipticseasons:crops/dry_dry`         | 干燥   | 干燥   |
| `eclipticseasons:crops/dry_average`     | 干燥   | 平均   |
| `eclipticseasons:crops/dry_moist`       | 干燥   | 湿润   |
| `eclipticseasons:crops/dry_humid`       | 干燥   | 潮湿   |
| `eclipticseasons:crops/average_average` | 平均   | 平均   |
| `eclipticseasons:crops/average_moist`   | 平均   | 湿润   |
| `eclipticseasons:crops/average_humid`   | 平均   | 潮湿   |
| `eclipticseasons:crops/moist_moist`     | 湿润   | 湿润   |
| `eclipticseasons:crops/moist_humid`     | 湿润   | 潮湿   |
| `eclipticseasons:crops/humid_humid`     | 潮湿   | 潮湿   |

### 额外控制

| 标签名                                | 用途              |
|:-----------------------------------|:----------------|
| `eclipticseasons:natural_plants`   | 强制方块兼容节气的生长控制系统 |
| `eclipticseasons:volatile_plants`  | 强制方块添加随机刻       | 
| `eclipticseasons:dark_grow_plants` | 适应弱光温室的植物       | 

### 农业气候区

| 标签名                         | 说明     |
|:----------------------------|:-------|
| `eclipticseasons:all`       | 所有     |
| `eclipticseasons:overworld` | 主世界气候区 | 

## 粒子效果

| 标签名                                  | 用途        |
|:-------------------------------------|:----------|
| `eclipticseasons:habitat/butterfly`  | 蝴蝶粒子源     |
| `eclipticseasons:habitat/firefly`    | 萤火虫粒子源    | 
| `eclipticseasons:none_fallen_leaves` | 不应该有落叶的方块 |

## 中暑抵抗

| 标签名                                       | 类型    |
|:------------------------------------------|-------|
| `eclipticseasons:heatstroke_resistant`    | 头盔附魔  |
| `eclipticseasons:cooling_items`           | 物品栏物品 | 
| `eclipticseasons:heat_protective_helmets` | 头盔物品  |
| `heatstroke_resistant`                    | 生物状态  | 

## 杂项

| 标签名                                              | 用途              |
|:-------------------------------------------------|:----------------|
| `eclipticseasons:snow_overlay_cannot_survive_on` | 用于快速禁用某些方块的覆雪效果 |

## 进度系统

节气的进度系统主要结合Minecraft的一些设置，因此受到数据驱动限制，进行修改必须要覆写或者考虑一些魔改模组。

这里提及主要是节气的一些特殊控制参数。在完成季节进度之后，会奖励一下心髓物品。

如果不想让玩家获得，可以覆盖`eclipticseasons:gifts`这个目录下的战利品表或者调整进度。

此外，注意节气进度使用了父亲函数来锁进度，即`eclipticseasons:parent`。
