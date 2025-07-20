## 基本说明

群系季节颜色是一种仅客户端的资源包构建，用于不满意默认提供的四季群系颜色变化时。

其为json文件，在资源包的放置根目录路径为`assets/<命名空间>/eclipticseasons/biome_colors`。

## 文件内容

### 定义示例

下方展示了一个用于修改平原群系四季颜色的测试性写法。当然，为了四季之间有更好的过渡效果，推荐使用二十四节气写法。此外，也可以考虑使用mix参数与原群系颜色进行比例混合，这样可以不必每个群系都定义颜色，且保持一些差别。

```json
{
  "biomes": "minecraft:plains",
  "grass_colors": {
    "seasons": {
      "spring": {
        "color": -20561
      },
      "summer": {
        "color": -16711936
      },
      "autumn": {
        "color": -14336
      },
      "winter": {
        "color": -16776961
      }
    }
  }
}
```

### 参数说明

#### 参数：biomes【String|Object】

用于检测群系对应，支持标签，或者是群系id字符串，或者是HolderSet。

#### 参数：grass_colors【Solar Term Map】、foliage_colors【Solar Term Map】

此处定义草地和树叶颜色。

**可选子参数：default【Color】、seasons【Map->Color】、solar_terms【Map->Color】**

可以选择使用节气还是季节来区分颜色。其对象除了color字段外，还可以使用mix（浮点数，0.0-1.0）、color_string（颜色字符串，如#5bae23）


