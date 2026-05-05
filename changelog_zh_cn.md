- Amid the Turning Seasons / 风轻轻，雨淋铃

> Between the seasons I walk,
> where spring has not yet ended,
> and summer is already near.
>
> Rain comes before its name,
> grass grows before it is seen.
>
> And in passing through them,
> I come to know the year.

> 春意未尽暑先渐，
> 雨声无觉草相连。
>
> 岁岁行过未曾念，
> 明朝风物又如前。
>

也许是节气模组历史上第二重要的一次更新！

这也是我们第一次在预览版本最后发布候选更新测试，包含了大约3700行代码，是的，本来这次更新应该在月底进行。
由于实际上所有版本新增了大约1.5w行代码，需要更多测试来确定稳定性。
一旦没有被报告问题，它将最终被标记为正式版本。

#### 新功能

- 新增了配置界面，无需其他前置模组
    - 常用配置将放在前面
    - 不常用的配置聚合在最后一个页面
    - 可以通过选项卡调整当前想调整的配置类型
    - 支持配置Mixins选项
    - 部分配置需要重启游戏（如Mixin选项修改）
    - 支持ModMenu（26.1-Fabric）和Configured（1.20.1-Forge）
    - 可以通过高级按钮回到默认配置界面
    - 为1.20.1增加了配置按钮和多语言配置
    - 部分选项需要游戏内调整
        - 季节有效的维度
        - 取消覆雪的方块
    - 支持部分选项特化配置，如季节选择和日照时间调整
        - 季节有效的维度
        - 季节日照时间调整
        - 应用季节的动物/蜜蜂/钓鱼行为
        - 取消覆雪的方块
    - 模组的配置同步是单向且默认还原的，因此不建议多人游戏时修改Common类型配置
    - 不支持不适合原版布局的复杂选项
        - 雪线
        - 自定义方块季节颜色覆盖

![配置界面显示](https://media.forgecdn.net/attachments/1665/963/config.png)

- 新增了节日系统`eclipticseasons:special_days`
    - 内置了部分节日
        - flower_festival
        - spring_festival
        - spring_outing
        - easter
        - chinese_valentines_day
        - mid_autumn
        - christmas
        - new_year
    - 可以通过数据包覆盖调整或`eclipticseasons:extra_info`数据包取消这些节日
    - 支持通过API或者命令查询当前节日

![节日与音乐](https://media.forgecdn.net/attachments/1665/964/specialdays.png)

- 新增了季节音乐系统`eclipticseasons:background_music`
    - 支持仅在指定节日播放
    - 不会中断当前正在播放的音乐，仅在无额外选择时提供
    - 内置了部分示例
    - 受到版权和模组大小限制，暂时不提供内置季节音乐
    - 示例如下所示，也支持`eclipticseasons:ambient`资源包的`season`等选项

```json
{
  "special_days": "eclipticseasons:christmas",
  "ignore_time": false,
  "day": false,
  "biomes": "#eclipticseasons:misc/ambient/spring",
  "ignored_biomes": "#eclipticseasons:misc/ambient/spring_negate",
  "music": {
    "default": {
      "sound": "eclipticseasons:music.gacha_bells",
      "min_delay": 1000,
      "max_delay": 25000
    }
  }
}
```

- 更新日历内容显示
    - 增加了子季节
        - 子季节包括
            - early_spring
            - mid_spring
            - late_spring
            - early_summer
            - mid_summer
            - late_summer
            - early_autumn
            - mid_autumn
            - late_autumn
            - early_winter
            - mid_winter
            - late_winter
            - none
        - 子季节支持在日历和调试界面以及命令查询中显示
        - 子季节支持API查询
        - 子季节维度相关，不包含群系差异
        - 子季节目前支持`eclipticseasons:crop`数据包，示例如下
      ```json
      {
        "climate": "eclipticseasons:temperate",
        "apply_target": {
          "blocks": "#eclipticseasons:crops/spring"
        },
        "sub_seasons": {
          "early_spring": {
            "grow_chance": 0.83
          },
          "mid_spring": {
            "grow_chance": 1.125
          },
          "late_spring": {
            "grow_chance": 0.9
          },
          "early_summer": {
            "grow_chance": 0.25
          },
          "mid_summer": {
            "grow_chance": 0.15
          },
          "late_summer": {
            "grow_chance": 0.05
          },
          "early_autumn": {
            "grow_chance": 0.2
          },
          "mid_autumn": {
            "grow_chance": 0.1
          },
          "late_autumn": {
            "grow_chance": 0.05
          },
          "early_winter": {
            "grow_chance": 0.05
          },
          "mid_winter": {
            "grow_chance": 0.0
          },
          "late_winter": {
            "grow_chance": 0.0
          }
        }
      }
      ```
        - 和所有含有SolarTermValueMap的数据或资源包
            - `eclipticseasons:biome_climate_setting` (Data)
            - `eclipticseasons:biome_rain` (Data)
            - `eclipticseasons:season_definitions` (Data)
            - `eclipticseasons:biome_colors` (Asset)
            - `eclipticseasons:particles/fallen_leaves` (Asset)
              -其中`eclipticseasons:biome_colors`的示例如下：
        ```json
        {
          "biomes": "minecraft:plains",
          "foliage_colors": {
            "sub_seasons": {
              "early_spring": {
                "color": -12012264,
                "mix": 0.0
              },
              "mid_spring": {
                "color": -12012264,
                "mix": 0.16
              },
              "late_spring": {
                "color": -12012264,
                "mix": 0.32
              }
            }
          },
          "grass_colors": {
            "sub_seasons": {
              "early_spring": {
                "color": -12012264,
                "mix": 0.0
              },
              "mid_spring": {
                "color": -12012264,
                "mix": 0.16
              },
              "late_spring": {
                "color": -12012264,
                "mix": 0.32
              }
            }
          }
        }
        ```

  - 增加了月份与月内日期
    - 一年有十二个月份，与现实世界一致
    - 一个月份基本包含两个节气或者一个子季节长度
    - 如果单一节气时间较长，也会保持月份与节气数量的对应关系
    - 月份与节气或季节不对齐，不参与数据包控制
    - 注意区分公历年与节气年，初始设置一年从2月3日开始，该日为立春日


- 现在可以给方块模型添加季节颜色支持，目前仅支持单一tint模型
```toml
[Renderer]
    #Custom seasonal colors for single-tint blocks only.
    #Format: "block_id@color1,color2,...,colorN,placeholder_color,base_color"
    #The number of colors (N) must be a factor of 24 (e.g., 4, 12, or 24).
    #- 4 colors: Seasonal (6 terms each)
    #- 12 colors: Monthly (2 terms each)
    #The 'placeholder_color' maps to index 24; 'base_color' is the final reference hex.
	SeasonalColorOverrides = ["minecraft:spruce_leaves@#96C24E|0.024,#96C24E|0.096,#96C24E|0.18,#96C24E|0.21600002,#96C24E|0.14400001,#96C24E|0.120000005,#5BAE23|0.120000005,#5BAE23|0.072000004,#000000|0.0,#5BAE23|0.120000005,#5BAE23|0.060000002,#000000|0.0,#BEC936|0.060000002,#BEC936|0.120000005,#BEC936|0.14400001,#BEC936|0.18,#BEC936|0.21600002,#BEC936|0.14400001,#253D24|0.21600002,#253D24|0.24000001,#253D24|0.3,#253D24|0.18,#253D24|0.096,#253D24|0.060000002,#000000|0.0,#619961"]
```

#### 重要变化

- 基于群系的局部天气系统被移除
  - 现在会代理维度天气变化
  - 相关Mixins代码被移除
  - 原本的天气API接口保持不变以保持向下兼容能力
  - 天气系统的基本参数与平原群系一致
- 天气系统重构
  - 使用内置数据包替代原有默认硬编码天气逻辑
  - 降雨机制已全面重做
    - 不同节气下的降雨时长、频率与强度存在明显差异
    - 修复部分季节中降雨必定触发雷电的问题

![节气与雨](https://media.forgecdn.net/attachments/1665/964/specialdays.png)

#### 问题与优化

- 覆雪方块的地图颜色机制优化
  - 默认不再直接调整所有方块的地图颜色
  - 为手持地图提供单独的覆雪信息支持
  - 为原版下落方块如铁砧和混凝土粉尘提供灰尘粒子颜色支持
  - 可能有助于提升与部分地图模组一起使用时的地图初始化速度
- 修复`eclipticseasons:ambient`的`sound`字段只能加载代码注册的SoundEvent的问题
  - 现在支持通过原版`sounds.json`文件注册的声音
- 为`eclipticseasons:season_definitions`数据包新增`changes`内部字段`fixed_seed_chance`
  - 启用`fixed_seed`时有效，可以调整固定种子时的变化速度 
- 修复玻璃板默认情况下覆雪的bug
- 为新增的信息提供了命令和调试界面支持
- 可以通过指令打开配置
  - `/ecliptic config` (Neo/Forge)
  - `/eclipticc config` (Fabric)
- 可以通过组合键`LCtrl+D`打开调试信息显示
  - 按键可以在按键绑定中修改 
- 内置针对Sodium楼梯模型而改进的覆雪模型资源包
- 优化`Platform`类的函数设置，以避免过早加载类导致其他模组无法Mixin原版类

#### API变化
- 新增API接口
  - `getSeason` -> `Season`
  - `getSubSeason` -> `Season.Sub`
  - `getStandardMonth` -> `Month`
  - `getDayOfMonth` -> `int`
  - `getSpecialDays` -> `List<Holder<SpecialDays>>`
- 移除数据包
  - `eclipticseasons:weather_region`
- 移除`com.teamtea.eclipticseasons.compat.Platform`中的函数`getServer`

#### 兼容性调整
- Voxy
  - 由于许可证和更新方式问题，不再内置1.20.1和1.21.1的Voxy Port支持
  - 改为独立模组`Ecliptic Seasons : Voxy Compact`兼容
  - 26.1版本保持内置支持不变
- Distant Horizons
  - 不再要求Distant Horizons的版本
  - 可以在`config/eclipticseasons-mixins.toml`中关闭`compat.distanthorizons`的所有条目
  - 除了覆雪模型和季节变化时的颜色更新之外，无需特别使用Mixin兼容支持
  - 当检测到版本低于3.0.0-b时，不启用Mixin





