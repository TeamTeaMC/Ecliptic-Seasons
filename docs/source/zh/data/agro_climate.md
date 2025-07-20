## 基本说明

农业气候区是一个特殊的数据包，主要是为了处理群系标签不能排除的缺陷以及一些季节生长条件映射处理，
以避免需要编写太多的数据包。对于湿度条件，则由群系的一些参数和季节波动变化影响控制。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/agro_climate`。

## 文件内容

一般不需要编辑此数据包，而是应该为作物添加其他气候区的生长条件设置。
如果需要调整，可以覆盖文件，并将biomes设置为[]空无效化当前，来自行添加自己喜欢的选项。

### 定义示例

下方展示了一个热带地区的气候条件映射，其主要是根据温带地区的立夏和夏至一些生长条件做调整。
因此作物如果缺乏夏季适应性，那么在热带会相对难以生长。
seasonal_signal_durations参数则是为了给季节温室核心调整红石信号强度使用，其和应该为24，
如果处于应该提示的季节，那么信号将会增加。

```json
{
  "mappings": {
    "spring": [
      {
        "beginning_of_summer": 1.0
      }
    ],
    "summer": [
      {
        "summer_solstice": 1.0
      }
    ],
    "autumn": [
      {
        "summer_solstice": 0.6
      },
      {
        "beginning_of_summer": 0.4
      }
    ],
    "winter": [
      {
        "beginning_of_summer": 1.0
      }
    ]
  },
  "seasonal_signal_durations": [
    {
      "summer": 24
    }
  ],
  "biomes": {
    "values": [
      "#forge:is_hot/overworld"
    ],
    "type": "forge:and"
  }
}
```
