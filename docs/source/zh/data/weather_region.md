## 基本说明

节气模组的默认设置为不同群系分配不同的天气，但是受限于破碎的群系分布，有时候可能需要合并一些群系天气效果以优化过渡问题。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/weather_region`。

## 文件内容

### 定义示例

下方展示了绑定森林天气区的文件，这里`core`为群系id，
`sub`是HolderSet，所以也可以使用标签字符串等，
`priority`参数可以省略。

```json
{
  "core": "minecraft:forest",
  "sub": [
    "minecraft:flower_forest",
    "minecraft:dark_forest",
    "minecraft:birch_forest"
  ],
  "priority": 1000
}
```
