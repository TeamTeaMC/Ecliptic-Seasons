## 基本说明

季节贴图是一种仅客户端的资源包构建，用于当不想让方块实际产生四季变化时。

其为json文件，在资源包的放置根目录路径为`assets/<命名空间>/eclipticseasons/season_textures`。

## 文件内容

季节贴图为季节模型的简化设计，可以对一般的Minecraft模型进行季节性贴图置换，从而免去创建繁杂的模型文件。当然功能也有所简化。

### 定义示例

下方展示了一个用于把春季橡树树叶模型替换为其他贴图的写法。

```json
{
  "target": "minecraft:block/oak_leaves",
  "biomes": "#eclipticseasons:seasonal",
  "slices": [
    {
      "textures": [
        {
          "all": "minecraft:block/cherry_leaves"
        },
        {
          "all": "minecraft:block/spruce_leaves"
        }
      ],
      "tint": {
        "#all": -1
      },
      "season": "spring"
    },
    {
      "transition_textures": [
        {
          "all": "minecraft:block/cherry_leaves"
        },
        {
          "all": "minecraft:block/spruce_leaves"
        }
      ],
      "tint": {
        "#all": -1
      },
      "season": "summer"
    }
  ]
}
```

### 参数说明

#### 可选参数：target【String|List】

用于定义应用的模型对象，可以是模型id或者id字符串列表。如果不写那么会根据当前id进行自动映射。如当前在资源包中位置为
`minecraft\eclipticseasons\season_textures\oak_leaves.json`，那么会自动应用到模型``minecraft:block/oak_leaves`上。

#### 参数：biomes【String|Array】

用于检测群系对应，支持标签，或者是群系id字符串列表。

#### 参数：slices【Object Array】

此意思为切片，用于匹配季节片段使用的模型，对应对象应该为数组。

**可选子参数：start【String】、end【String】、solar_term【String】**

如果划分时间为节气，那么可以使用这三个子参数。分别表示开始，结束，唯一。节气请使用小写id，具体名称可以参考气候一节。

**可选子参数：start_season【String】、end_season【String】、season【String】**

如果您更喜欢季节区分，那么可以使用这三个参数。请使用小写id。请注意区别，由于群系季节最小区间为节气，这里提供的季节为维度季节，所以对于部分特殊群系，并不适用季节参数（如果您在意季节区分的话）。

**可选子参数：textures【Array|Object】、transition_textures【Array】**

如果在该季节内方块外观不变，应该使用textures，否则使用transition_textures。注意transition_textures仅会应用于区间内的第一个节气产生变化。
必须注意的是，如果这里想做变化，那么需要给加一个[]变成数组，否则只需要写一个Texture
Map进行映射覆盖即可。对于transition_textures，最好写为双列表，即[[xx,xx]]这样的形式。

**可选子参数：tint【Object】**

这是一个特殊的参数，用于修改染色id，设置为-1取消对应参数的面的染色。
