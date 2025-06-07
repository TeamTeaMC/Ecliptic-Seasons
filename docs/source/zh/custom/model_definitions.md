## 基本说明

如果需要创建覆雪模型资源包或者四季模型资源包，那么必须首先创建模型文件映射。

其为json文件，在资源包的放置根目录路径为`assets/<命名空间>/eclipticseasons/model_definitions`。

## 文件内容

基本上，模型文件映射与创建方块模型所使用的blockstates.json很像,区别在于一些额外属性追加，以及不需要对应方块命名。

所以，您可以直接使用Multi-Variant或者Multi-Part系统。如下所示，他们与标准无差异，这里的id为模型文件夹里的id：

### 模型变种
```json
{
  "variants": {
    "": {
      "model": "eclipticseasons:block/overlay"
    }
  }
}
```
我们还可以使用权重来进行调节出现概率，注意这里也可以加上方块属性判断，如`facing=west,half=bottom,shape=straight`。
```json
{
  "variants": {
    "": [
      {
        "model": "eclipticseasons:block/flower_1"
      },
      {
        "weight": 120,
        "model": "minecraft:block/air"
      }
    ]
  }
}
```
### 模型组合
通过组合属性，我们可以轻易组合模型，减少模型注册数量。注意这种方块会增加性能消耗。应该避免频繁使用。
```json
{
  "multipart": [
    {
      "when": {
        "snowy": "false"
      },
      "apply": [
        {
          "model": "minecraft:block/grass_block_snow"
        },
        {
          "y": 90,
          "model": "minecraft:block/grass_block_snow"
        },
        {
          "y": 180,
          "model": "minecraft:block/grass_block_snow"
        },
        {
          "y": 270,
          "model": "minecraft:block/grass_block_snow"
        }
      ]
    },
    {
      "apply": [
        {
          "model": "eclipticseasons:block/grass_block_overlay"
        },
        {
          "y": 90,
          "model": "eclipticseasons:block/grass_block_overlay"
        },
        {
          "y": 180,
          "model": "eclipticseasons:block/grass_block_overlay"
        },
        {
          "y": 270,
          "model": "eclipticseasons:block/grass_block_overlay"
        }
      ]
    }
  ]
}
```
## 追加设置

显然有时候我们需要让模型完全替换原有模型，而不是简单的叠加，因此此时需要使用replace属性来实现替换。
```json
{
  "....": "....",
  "replace": true
}
```