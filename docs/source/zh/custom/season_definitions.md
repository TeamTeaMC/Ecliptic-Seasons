## 基本说明

季节模型是一种仅客户端的资源包构建，用于当不想让方块实际产生四季变化时。

其为json文件，在资源包的放置根目录路径为`assets/<命名空间>/eclipticseasons/season_definitions`。

## 文件内容

在看此节之前，你需要考虑需要让方块有多少变化，比较常见的情形为季节性的叠加模型，或者是方块模型的切换。
请综合自己的需求进行设计，避免过多或者不平滑的变化。

### 定义示例

下方展示了一个用于夏季莲花开放的变化，莲花会随着夏季到来而逐渐长满，最后在秋季逐渐消失。

```json
{
  "blocks": "minecraft:lily_pad",
  "biomes": "#eclipticseasons:seasonal",
  "slices": [
    {
      "transition_models": [
        "eclipticseasons:empty",
        "eclipticseasons:lily_pad_common"
      ],
      "solar_term": "grain_rain"
    },
    {
      "mid": "eclipticseasons:lily_pad_common",
      "start": "beginning_of_summer",
      "end": "greater_heat"
    },
    {
      "transition_models": [
        "eclipticseasons:lily_pad_common",
        "eclipticseasons:empty"
      ],
      "solar_term": "beginning_of_autumn"
    }
  ]
}
```

### 参数说明

#### 参数：blocks【String|Object】

用于定义方块应用对象，可以是id，tag（#开头的字符），或者是HolderSet。但是HolderSet过于复杂，推荐一般使用标签或者id即可。

#### 参数：biomes【String|Object】

用于检测群系对应，同上支持3种形式。

#### 参数：slices【Object Array】

此意思为切片，用于匹配季节片段使用的模型，对应对象应该为数组。

**可选子参数：start【String】、end【String】、solar_term【String】**

如果划分时间为节气，那么可以使用这三个子参数。分别表示开始，结束，唯一。节气请使用小写id，具体名称可以参考气候一节。

**可选子参数：start_season【String】、end_season【String】、season【String】**

如果您更喜欢季节区分，那么可以使用这三个参数。请使用小写id。请注意区别，由于群系季节最小区间为节气，这里提供的季节为维度季节，所以对于部分特殊群系，并不适用季节参数（如果您在意季节区分的话）。

**可选子参数：mid【String】、transition_models【String Array】**

如果在该季节内方块外观不变，应该使用mid，否则使用transition_models。注意transition_models仅会应用于区间内的第一个节气。
随着时间推移，方块外观也会逐渐变化。目前transition_models仅支持双模型。

**可选子参数：empty_above【Bool】**

这是一个特殊的参数，用于检查方块上面是否空空如也，只有true/false可以选。
