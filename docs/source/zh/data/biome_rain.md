> 群系降水计算是一个复合概率，您还需要考虑群系气候设定中的降水值。

## 基本说明

群系雨主要用于调节一些群系的天气参数设定，一般无需关心，特别是Solar Weather关闭时，注意降雨情况也会对湿度有一定影响。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/biome_rain`。

## 文件内容

### 定义示例

下方展示了平原地区设置群系雨的文件，这里biomes是HolderSet，所以也可以使用标签字符串等。
weathers字段基于SolarTermMap结构，可以写solar_terms、seasons或者default。
具体到天气参数方面，可以使用rain_chance、thunder_chance、rain、rain_delay、thunder、time_periods等参数。
如果想设置时间段，time_periods参数是一个时段字符串的列表，并注意将天气对象改为列表以便覆盖全时段。
一般而言，提供两个概率参数即可,rain_chance是必须提供的，thunder_chance有一个缺省值0。

```json
{
  "biomes": "minecraft:plains",
  "weathers": {
    "solar_terms": {
      "beginning_of_spring": {
        "rain_chance": 0.3,
        "thunder_chance": 0.0
      },
      "spring_equinox": {
        "rain_chance": 0.5,
        "thunder_chance": 0.1,
        "rain": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": 12000,
            "max_inclusive": 24000
          }
        },
        "rain_delay": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": 12000,
            "max_inclusive": 180000
          }
        },
        "thunder": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": 3600,
            "max_inclusive": 15600
          }
        }
      }
    }
  }
}
```

### 天气效果

有时候可能需要设置特殊天气类型。
其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/biome_rain_effect`。

我们可以这样定义一个暴风雪。

```json
{
  "contents": [
    {
      "type": "snow"
    },
    {
      "density": 0.52,
      "type": "fog"
    }
  ],
  "type": "composite"
}
```

也可以设置一个类型为起雾，可以设置replace来取消降雨。

```json
{
  "density": 0.2,
  "replace": true,
  "type": "fog"
}
```

应用需要放入biome_rain数据包。以下是一个id为`example:colder_plains`的群系雨天气类型。
按照冷群系安排，在冬天会起暴风雪。

```json
{
  "biomes": "minecraft:plains",
  "weathers": {
    "climate": "eclipticseasons:cold",
    "seasons": {
      "winter": {
        "rain_chance": 0.5,
        "special_effect": "eclipticseasons:snow_storm"
      }
    }
  }
}
```