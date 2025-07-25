## 基本说明

落叶粒子是一种仅客户端的资源包构建，用于自定义一些方块的落叶粒子效果。

其为json文件，在资源包的放置根目录路径为`assets/<命名空间>/eclipticseasons/particles/fallen_leaves`。

## 文件内容

### 定义示例

下方展示了一个用于修改樱花落叶粒子的测试性写法。
如果不需要取消原粒子，那么不使用replace字段即可。
由于我们也不需要染色，用default设置为-1，并将颜色来源设置为custom。
如果需要染色，只需要按规范填入即可。
本json中colors、weights、sprites都支持SolarTermMap格式，
即default全年，seasons按季节，solar_terms按节气。

```json
{
  "block": {
    "blocks": "minecraft:cherry_leaves"
  },
  "location": {
    "biomes": "#eclipticseasons:seasonal"
  },
  "replace": true,
  "colors": {
    "default": {
      "color": -1
    }
  },
  "sprites": {
    "solar_terms": {
      "beginning_of_autumn": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "end_of_heat": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "white_dew": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "autumnal_equinox": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "cold_dew": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ],
      "first_frost": [
        "eclipticseasons:fallen_cherry_leaves_0",
        "eclipticseasons:fallen_cherry_leaves_1",
        "eclipticseasons:fallen_cherry_leaves_2",
        "eclipticseasons:fallen_cherry_leaves_3",
        "eclipticseasons:fallen_cherry_leaves_4",
        "eclipticseasons:fallen_cherry_leaves_5"
      ]
    }
  },
  "weights": {
    "solar_terms": {
      "beginning_of_autumn": 6,
      "end_of_heat": 7,
      "white_dew": 8,
      "autumnal_equinox": 10,
      "cold_dew": 12,
      "first_frost": 16
    }
  },
  "source": "custom"
}
```

另一个示例：

部分参数可省略，source 可设为 map（地图色）、block（方块色，部分树叶可染色）、texture（提取纹理色）或 custom（自定义色）。

注意这里用了标签来简化展示，实际应用请保持针对性。

```json
{
  "block": {
    "blocks": "#minecraft:leaves"
  },
  "location": {
    "biomes": "#eclipticseasons:seasonal"
  },
  "sprites": {
    "default": [
      "eclipticseasons:fallen_leaves/leaf_0",
      "eclipticseasons:fallen_leaves/leaf_1",
      "eclipticseasons:fallen_leaves/leaf_2",
      "eclipticseasons:fallen_leaves/leaf_3",
      "eclipticseasons:fallen_leaves/leaf_4",
      "eclipticseasons:fallen_leaves/leaf_5",
      "eclipticseasons:fallen_leaves/leaf_6",
      "eclipticseasons:fallen_leaves/leaf_7",
      "eclipticseasons:fallen_leaves/leaf_8",
      "eclipticseasons:fallen_leaves/leaf_9",
      "eclipticseasons:fallen_leaves/leaf_10",
      "eclipticseasons:fallen_leaves/leaf_11",
      "eclipticseasons:fallen_leaves/leaf_12",
      "eclipticseasons:fallen_leaves/leaf_13",
      "eclipticseasons:fallen_leaves/leaf_14",
      "eclipticseasons:fallen_leaves/leaf_15"
    ]
  },
  "source": "block"
}
```


