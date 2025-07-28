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
      "rain_water": {
        "rain_chance": 0.5,
        "thunder_chance": 0.08
      },
      "insects_awakening": {
        "rain_chance": 0.55,
        "thunder_chance": 0.15
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
      },
      "fresh_green": {
        "rain_chance": 0.65,
        "thunder_chance": 0.05
      },
      "grain_rain": {
        "rain_chance": 0.75,
        "thunder_chance": 0.0
      },
      "beginning_of_summer": {
        "rain_chance": 0.9,
        "thunder_chance": 0.0
      },
      "lesser_fullness": {
        "rain_chance": 0.7,
        "thunder_chance": 0.1
      },
      "grain_in_ear": {
        "rain_chance": 0.6,
        "thunder_chance": 0.15
      },
      "summer_solstice": {
        "rain_chance": 0.7,
        "thunder_chance": 0.25
      },
      "lesser_heat": {
        "rain_chance": 0.65,
        "thunder_chance": 0.2
      },
      "greater_heat": {
        "rain_chance": 0.5,
        "thunder_chance": 0.05
      },
      "beginning_of_autumn": {
        "rain_chance": 0.42,
        "thunder_chance": 0.0
      },
      "end_of_heat": {
        "rain_chance": 0.4,
        "thunder_chance": 0.0
      },
      "white_dew": {
        "rain_chance": 0.35,
        "thunder_chance": 0.0
      },
      "autumnal_equinox": {
        "rain_chance": 0.32,
        "thunder_chance": 0.0
      },
      "cold_dew": {
        "rain_chance": 0.3,
        "thunder_chance": 0.0
      },
      "first_frost": {
        "rain_chance": 0.25,
        "thunder_chance": 0.0
      },
      "beginning_of_winter": {
        "rain_chance": 0.3,
        "thunder_chance": 0.0
      },
      "light_snow": {
        "rain_chance": 0.4,
        "thunder_chance": 0.05
      },
      "heavy_snow": {
        "rain_chance": 0.5,
        "thunder_chance": 0.0
      },
      "winter_solstice": {
        "rain_chance": 0.45,
        "thunder_chance": 0.0
      },
      "lesser_cold": {
        "rain_chance": 0.4,
        "thunder_chance": 0.0
      },
      "greater_cold": {
        "rain_chance": 0.2,
        "thunder_chance": 0.0
      },
      "none": {
        "rain_chance": 0.0,
        "thunder_chance": 0.0
      }
    }
  }
}
```
