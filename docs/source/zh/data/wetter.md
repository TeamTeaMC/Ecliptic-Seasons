## 基本说明

我们也可以定义一些多方块结构来调整周围湿度，注意这个机制依靠随机刻。对于不能使用随机刻的方块，需要强制开启。
可以使用标签`eclipticseasons:volatile`。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/wetter`。

## 文件内容

### 定义示例

```json
{
  "core": {
    "blocks": "minecraft:bubble_column"
  },
  "require": [
    {
      "offset": [
        0,
        -1,
        0
      ],
      "block": {
        "blocks": "minecraft:magma_block"
      }
    }
  ],
  "level": 0.75,
  "range": 4.0,
  "lasting_time": 600
}
```
