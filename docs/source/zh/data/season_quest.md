## 基本说明

季节任务数据包目前较为简单，本质用途是让独立玩家在完成季节进度和对应季节种植后仍然可以获得温室核心。
温室核心推荐由整合包作者自定义获取方式，如挑战Boss、服务器商店或者FTB任务等。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/season_quest`。

## 文件内容

### 定义示例

下方展示了春季核心任务，注意在需求方面，其为Ingredient，因此可以使用一些特殊类型。

```json
{
  "weight": 10,
  "glowing": true,
  "color": 43520,
  "climate": "#eclipticseasons:all",
  "award": [
    {
      "id": "eclipticseasons:spring_greenhouse_essence",
      "Count": 1
    }
  ],
  "tittle": {
    "translate": "season_quest.eclipticseasons.spring_core"
  },
  "end": "beginning_of_summer",
  "start": "spring_equinox",
  "need": [
    {
      "items": "minecraft:wheat",
      "count": 640
    }
  ]
}
```
