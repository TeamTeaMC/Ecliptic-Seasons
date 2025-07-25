> 需要注意的是，Crop数据包有最高应用优先级，而我们还有Crop标签，其覆盖需要使用`replace`相对比较麻烦，可以考虑使用Crop数据包。

## 基本说明

作物生长用于调整方块的生长速度，主要基于Neo/Forge事件管线进行处理，支持一般作物方块生长、树苗生长或者催熟事件。
如果存在不支持Neo/Forge事件的方块，可以使用设置中的强制兼容模式，或者将其id放入`eclipticseasons:natural_plants`标签中。
将通过限制随机刻的方式来调整其生长速度。
其分为两部分，分别是季节条件和湿度条件。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/crop`。

## 文件内容

作物生长是较为早期的数据包结构，且考虑到维护问题，
因此不直接支持后续的SolarTermMap，结果会有所差异。但本质上区别不大。
此外，默认情况下，一般用标签系统即可。如果需要针对指定作物或者含有BlockState条件的方块进行设置，那么才应该考虑使用。

### 定义示例

下方展示了一个用于根据向日葵是否为上半部分设置生长控制的示例写法。
由于我们不需要实际修改生长参数，因此设置一下parent即可引用现有参数。
对于气候设置而言，默认应该给予温带参数，这是映射到其他农业气候区的需要。
如果有多余时间，也可以为每个气候区专门设置参数。

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

### 生长参数

如果您已经查看了`eclipticseasons:seasons/xx`等数据，或许需要自定义一些生长参数。
这里分别可以设置生长速度（默认1），施肥成功率（默认1），生长失败时的死亡概率（默认0），以及死亡状态（默认为死去灌木）。
如果你需要设置死亡状态，并且精确到BlockState，可以参考Minecraft反序列化一些写法。
因为历史问题，这里部分参数是大写的。
注意这些参数都是可以省略的。
生长参数如果设置为大于1f的数可能存在不支持情况，需要该方块支持Neo/Forge事件才能有促进效果。
但由于季节和湿度同时影响，即使方块不支持事件，也可以略微设置为大于1的值。

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