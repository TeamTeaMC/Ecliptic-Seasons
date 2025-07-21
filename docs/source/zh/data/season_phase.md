## 基本说明

季节阶段主要用来改变日历和节气提示显示。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/season_phase`。

## 文件内容

文件有一些缺省值，并使用Bitmap字体来在文字渲染中添加字符。

### 定义示例

下方一个雨季地区的示例文件，其借用了一些文件。

```json
{
  "color": "green",
  "icon": {
    "texture": "eclipticseasons:season_phase/wet_middle"
  },
  "font": {
    "id": "eclipticseasons:monsoon_icons",
    "label": "h"
  },
  "season": "summer",
  "name": "eclipticseasons:wet"
}
```

随后需要补充一些lang翻译文件。

```
"info.eclipticseasons.environment.season_phase.wet": "湿季",
"info.eclipticseasons.environment.season_phase.pattern.wet": "%s (全年)",
"info.eclipticseasons.environment.season_phase.alternation.wet": "水汽沉沉，湿热难耐。"
```

如果不需要缺省的话也可以看看完整的补全，这些值都需要预先提供好。
这里推荐提供大小为30的独立icon，以省略这些参数。

```
"icon": {
    "i": 1,
    "j": 3,
    "texture": "eclipticseasons:font/seasons_icons",
    "width": 180,
    "height": 120,
    "size": 30
}
```
