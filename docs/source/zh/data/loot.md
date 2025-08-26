## 基本说明

节气也为战利品表提供了季节条件，和一般条件的写法类似。

## 文件内容

### 定义示例

下方是一个常规战利品表，但添加了额外的季节控制条件。请特别关注 `require` 字段的用法。

```json
{
  "type": "minecraft:gift",
  "pools": [
    {
      "bonus_rolls": 0.0,
      "conditions": [
        {
          "condition": "eclipticseasons:season",
          "require": {
            "end": "beginning_of_winter",
            "start": "autumnal_equinox"
          }
        }
      ],
      "entries": [
        {
          "type": "minecraft:item",
          "name": "eclipticseasons:spring_greenhouse_essence",
          "weight": 10
        }
      ],
      "rolls": 1.0
    }
  ],
  "random_sequence": "eclipticseasons:gifts/autumn_greenhouse_essence"
}
```

#### 可选子参数（节气方式）：

* `start`：开始节气
* `end`：结束节气
* `solar_term`：指定唯一节气

使用节气划分时，请填写小写的节气ID，具体名称请参阅“气候”章节。`start` 和 `end` 表示时间范围，`solar_term` 表示仅匹配某个节气。

#### 可选子参数（季节方式）：

* `start_season`：开始季节
* `end_season`：结束季节
* `season`：指定唯一季节
* `climate`：锚定农业气候区，如果提供则不会试图查询基于群系位置

如更倾向于按季节划分，也可使用上述参数，同样应填写小写ID。
需要注意的是，由于生物群系的季节变化以“节气”为最小单位，这里的季节参数适用于维度季节。
在某些特殊群系中，季节区分可能无效（如果您对此较为敏感，请使用节气方式代替）。

