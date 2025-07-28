> 群系气候设定数据包的意义在于无需修改群系数据包中编码的值，且实现季节性波动变化。
> 最重要的是，这个值不会影响世界生成。

## 基本说明

群系气候设定是一个附加数据包，主要是为了调节部分群系的原生气候设定。
原始群系的温度和降水值非常影响世界生成的概率计算。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/biome_climate_setting`。

## 文件内容

本数据包主要可以设置群系、降水、温度及二者变化。变化的部分使用SolarTermMap作为数据结构。

### 定义示例

下方展示了一个为热带草原在雨季增加降水参数，进而改善湿润度的写法。

```json
{
  "biomes": "#minecraft:is_savanna",
  "downfall_changes": {
    "solar_terms": {
      "beginning_of_summer": 0.2,
      "lesser_fullness": 0.333,
      "grain_in_ear": 0.467,
      "summer_solstice": 0.533,
      "lesser_heat": 0.633,
      "greater_heat": 0.533,
      "beginning_of_autumn": 0.467,
      "end_of_heat": 0.4,
      "white_dew": 0.333,
      "autumnal_equinox": 0.267,
      "cold_dew": 0.2,
      "first_frost": 0.167
    }
  }
}
```

有时候如果原始群系为了世界生成将群系参数调整的比较奇怪，也可以用这个参数调整回来。
注意如果在雪期数据包中未指定的话，基础温度的调整也将影响雪期。

```json
{
  "biomes": "badlands",
  "downfall": 0.0,
  "temperature": 2.0
}
```