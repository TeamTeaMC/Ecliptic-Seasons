> 需要启用`EnableSeasonDefinition`。


![bfxes4.gif](../../_static/image/bfxes4.gif)

## 基本说明

本配置用于定义在特定群系（不写则为忽略群系判定）中，随季节变化而发生的方块替换、掉落或生成规则。
整合包作者可以通过编写类似的配置文件，为不同方块添加季节变化效果。

该数据包需要配合有随机刻的方块使用，以减少原子操作，如果没有则可以参考数据包基本一节强制方块启用随机刻。

其为json文件，在资源包的放置根目录路径为`data/<命名空间>/eclipticseasons/season_definitions`。

## 文件内容

### 定义示例

这里为模组丰饶食记的苹果树添加了简单的春秋两季节变化。
春季：将自然苹果叶（非玩家放置）16% 概率变为开花叶，且通过固定种子避免过多的变化。在开花叶下方生成悬挂苹果（不重复生成）。
秋季：开花叶重新变回普通苹果叶。悬挂苹果替换为对应掉落物（模拟果实成熟）。

注意此处changes为SolarTermValueMap结构，因此还可以按节气字段进行更精细化调整。

此处还支持自定义条件与放置方法扩展，通过代码可以查看更多。

```json
{
  "biomes": "minecraft:plains",
  "changes": {
    "seasons": {
      "spring": [
        {
          "target": {
            "blocks": "bountifulfares:apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "fixed_seed": true,
          "chance": 0.16,
          "place": {
            "block": {
              "Name": "bountifulfares:flowering_apple_leaves"
            },
            "copy_state": true
          }
        }
      ],
      "summer": [
        {
          "target": {
            "blocks": "bountifulfares:flowering_apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "fixed_seed": true,
          "chance": 0.6,
          "place": {
            "block": {
              "Name": "bountifulfares:hanging_apple"
            },
            "conditions": [
              {
                "type": "empty_above",
                "above": false
              }
            ],
            "replace": false,
            "offset": [
              0,
              -1,
              0
            ]
          }
        }
      ],
      "autumn": [
        {
          "target": {
            "blocks": "bountifulfares:flowering_apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "fixed_seed": true,
          "chance": 0.4,
          "place": {
            "block": {
              "Name": "bountifulfares:hanging_apple"
            },
            "conditions": [
              {
                "type": "empty_above",
                "above": false
              }
            ],
            "replace": false,
            "offset": [
              0,
              -1,
              0
            ]
          }
        },
        {
          "target": {
            "blocks": "bountifulfares:flowering_apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "chance": 0.1,
          "place": {
            "block": {
              "Name": "bountifulfares:apple_leaves"
            },
            "copy_state": true
          }
        },
        {
          "target": {
            "blocks": "bountifulfares:hanging_apple"
          },
          "chance": 0.01,
          "place": {
            "loot": "bountifulfares:blocks/hanging_apple"
          }
        }
      ],
      "winter": [
        {
          "target": {
            "blocks": "bountifulfares:flowering_apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "chance": 0.5,
          "place": {
            "block": {
              "Name": "bountifulfares:apple_leaves"
            },
            "copy_state": true
          }
        },
        {
          "target": {
            "blocks": "bountifulfares:hanging_apple"
          },
          "chance": 0.1,
          "place": {
            "loot": "bountifulfares:blocks/hanging_apple"
          }
        }
      ]
    }
  }
}
```

## 使用 Ecliptic Seasons 创建季节性植物行为

### 示例：Bountiful Fares 苹果树季节规则

本教程介绍如何使用 **Ecliptic Seasons 的季节定义系统** 来为模组植物创建季节变化行为。

我们将以 **Bountiful Fares 的苹果树** 为例，解释一个完整的季节生命周期是如何实现的。

目标效果：

* **春季**：树叶开花
* **夏季**：花朵结成果实
* **秋季**：继续结果并逐渐回退
* **冬季**：清理残留花叶和果实

这些行为都通过 **season definition JSON 文件** 定义。

---

### 1 文件位置

季节定义文件需要放在数据包或资源包中的以下路径：

```
data/<namespace>/eclipticseasons/season_definitions/
```

例如：

```
data/es_dp_x_bountifulfares_extra/eclipticseasons/season_definitions/apple.json
```

通常 **一个 JSON 文件负责一种植物或一组行为**。

---

### 2 基础结构

一个典型的季节定义文件结构如下：

```json
{
  "biomes": "minecraft:plains",
  "changes": {
    "seasons": {
      "spring": [
        ...
      ],
      "summer": [
        ...
      ],
      "autumn": [
        ...
      ],
      "winter": [
        ...
      ]
    }
  }
}
```

字段说明：

| 字段        | 作用        |
|-----------|-----------|
| `biomes`  | 限定规则生效的群系 |
| `changes` | 季节变化定义    |
| `seasons` | 按季节分类的规则  |

当世界进入对应季节时，系统会执行这些规则。

---

### 3 基本规则结构

每一条规则都由以下几个部分组成：

```json
{
  "target": {
    ...
  },
  "chance": 0.16,
  "place": {
    ...
  }
}
```

核心组成：

| 字段           | 作用       |
|--------------|----------|
| `target`     | 指定要扫描的方块 |
| `chance`     | 触发概率     |
| `place`      | 命中后执行的操作 |
| `fixed_seed` | 是否固定随机分布 |

---

### 4 目标方块 (target)

示例：

```json
"target": {
"blocks": "bountifulfares:apple_leaves",
"state": {
"persistent": "false"
}
}
```

含义：

* `blocks` 指定方块 ID
* `state` 用于筛选方块状态

这里表示：

**只对自然生成的苹果树叶生效**。

这样可以避免修改玩家建造的树叶装饰。

---

### 5 概率控制 (chance)

示例：

```
chance: 0.16
```

表示 **16% 的概率触发**。

常见概率范围：

| 数值   | 含义    |
|------|-------|
| 0.01 | 很罕见   |
| 0.1  | 偶尔    |
| 0.3  | 较常见   |
| 0.6  | 很容易触发 |

季节系统通常使用 **较低概率** 来制造自然变化。

---

### 6 固定随机 (fixed_seed)

示例：

```
fixed_seed: true
```

作用：

使随机结果在同一位置保持稳定。

如果不使用：

* 花朵可能反复出现和消失
* 果实位置不断变化

使用后：

* 同一枝叶每年会稳定开花
* 树形更自然

推荐：

| 行为 | 是否使用  |
|----|-------|
| 开花 | 建议    |
| 挂果 | 建议    |
| 掉落 | 通常不需要 |

---

### 7 方块替换

示例：

```json
"place": {
"block": {
"Name": "bountifulfares:flowering_apple_leaves"
},
"copy_state": true
}
```

作用：

将普通苹果叶替换为 **开花苹果叶**。

`copy_state: true` 可以保留：

* distance
* persistent
* waterlogged

避免破坏树叶的自然衰减逻辑。

---

### 8 在附近生成方块

很多果实不是替换叶子，而是 **挂在叶子下方**。

示例：

```json
"place": {
"block": {
"Name": "bountifulfares:hanging_apple"
},
"offset": [0, -1, 0],
"replace": false
}
```

含义：

| 参数            | 说明      |
|---------------|---------|
| offset        | 新方块的位置  |
| [0,-1,0]      | 在叶子下方   |
| replace:false | 不覆盖已有方块 |

这会在树叶下面生成 **挂果**。

---

### 9 条件 (conditions)

示例：

```json
"conditions": [
{
"type": "empty_above",
"above": false
}
]
```

条件用于防止非法生成，例如：

* 果实生成在实体方块中
* 没有空间

条件系统可以扩展更多复杂限制。

---

### 10 掉落行为 (loot)

有时不需要放置方块，而是触发掉落。

示例：

```json
{
  "target": {
    "blocks": "bountifulfares:hanging_apple"
  },
  "chance": 0.01,
  "place": {
    "loot": "bountifulfares:blocks/hanging_apple"
  }
}
```

含义：

**果实成熟后自然掉落**。

掉落物由对应 **loot table** 控制。

---

### 11 苹果树季节生命周期

通过组合多条规则，可以实现完整生命周期：

#### 春季

普通叶子变成花叶

```
apple_leaves → flowering_apple_leaves
```

---

#### 夏季

花叶产生果实

```
flowering_apple_leaves → hanging_apple
```

---

### 秋季

同时发生三种变化：

1 花叶继续结果
2 部分花叶恢复普通叶
3 少量苹果掉落

形成 **自然收获阶段**。

---

#### 冬季

清理阶段：

* 花叶恢复普通叶
* 果实掉落概率增加

树恢复初始状态。

---

### 12 设计理念

季节系统围绕三个核心概念：

1 **目标方块 (target)**
2 **概率 (chance)**
3 **执行行为 (place)**

通过组合这些规则，可以实现：

* 开花
* 结果
* 果实掉落
* 植物枯萎
* 树叶季节变化

从而让模组植物融入 **Ecliptic Seasons 气候系统**。

---

### 13 seasons 与 default 的区别

季节规则分为两种：

#### seasons

```
changes → seasons
```

用于 **明显的季节性植物**

例如：

* 苹果
* 樱桃
* 李子
* 枫树

---

#### default

```
changes → default
```

用于 **全年型植物**

例如：

* 柠檬
* 橙子
* 椰子
* 常绿植物

这些植物全年循环，而不是按季节变化。

---

### 14 编写规则建议

推荐：

* 使用较低概率
* 替换方块时复制状态
* 果实使用 offset 生成
* 花和果使用 fixed_seed

避免：

* 在高频扫描中使用过大概率
* 忽略空间条件生成果实
* 不复制状态直接替换树叶

---

### 15 总结

通过简单的 JSON 规则，
Ecliptic Seasons 可以为模组植物提供完整的季节生态：

春季开花
夏季结果
秋季成熟
冬季回归

这样模组中的植物就能与世界气候系统产生自然互动。

本教程示例展示了 **Bountiful Fares 苹果树如何实现完整季节生命周期**。

---
