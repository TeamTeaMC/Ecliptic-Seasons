## 基本说明

湿度调节主要是作用于格栅方块，调整范围内湿度。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/humidity_control`。

## 文件内容

### 定义示例

注意检查可以忽略，要素和结果中的count也不需要考虑，
同时如果不设置infinity，需要设置lasting_time字段，其为int类型。
对于范围，目前实际上暂不支持设置为大于15。

```json
{
  "level": 1,
  "checks": [
    {
      "offset": [
        0,
        -1,
        0
      ],
      "block": {
        "blocks": "#eclipticseasons:soft_heat_sources"
      }
    }
  ],
  "infinity": true,
  "ingredient": {
    "item": "minecraft:wet_sponge",
    "count": 1
  },
  "result": {
    "id": "minecraft:sponge",
    "Count": 1
  },
  "range": 5
}
```
