## 基本说明

节气下雪主要由时间决定，如果没提供设置，那么会根据群系温度自动分配雪期。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/snow_term`。

## 文件内容

### 定义示例

下方展示了平原地区设置雪期的文件，这里biomes是HolderSet，所以也可以使用标签字符串等。

```json
{
  "biomes": "minecraft:plains",
  "start": "light_snow",
  "end": "greater_cold"
}
```
