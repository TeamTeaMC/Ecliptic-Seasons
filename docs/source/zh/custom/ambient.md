## 基本说明

季节环境音效是一种仅客户端的资源包构建，用于自定义季节环境背景音。

其为json文件，在资源包的放置根目录路径为`assets/<命名空间>/eclipticseasons/ambient`。

## 文件内容

### 定义示例

下方展示了一个夏季夜晚的音效设置方式。注意默认情况下rain为false。
如果需要白天或者夜晚变化，则需要设置ignore_time。
相比于一般的实时混合音效模组，本模组提供的季节音效主要是作为一个整体背景，以弥补Minecraft有时过于安静的问题。

```json
{
  "sound": "eclipticseasons:ambient.night_river",
  "biomes": "#minecraft:is_savanna",
  "ignore_time": false,
  "day": false,
  "rain": false,
  "season": "summer"
}
```




