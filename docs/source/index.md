![Ecliptic Seasons](https://github.com/user-attachments/assets/549d6626-d78e-4901-8b96-f420a6c2d3ea)

“节气”是Minecraft的一个中国风模组，关于四季。

Ecliptic Seasons is a Chinese mod about seasons and weather.

### LICENSE
*   For code: BSD-3
*   For resources: CC BY-NC-SA 4.0
*   Please do not port arbitrarily, communication can make the community better, and please respect our work.

1. Ecliptic Seasons是什么？

正如其名字一样，这个模组为了Minecraft带来了新的季节体验，也可以叫它Solar Terms。
这是一种古老的中国历法，它将一年分为四季二十四段时期，每一段节气都有其独特之处。
知晓时候，顺应时节，可以达到人与自然的和谐，所谓“天人合一”。

2. Ecliptic Seasons的功能是否与静谧四季等模组重复了，或者是否可以一起工作。

作为一个季节模组，Ecliptic Seasons与Serene Seasons有许多共同点。
如用四季来表示Minecraft的一年，如都改变了四季的草木颜色，雨雪变化，季节性的作物生长控制。
但是Minecraft已经推出了15年了，我们会尝试走的更远。

3. Ecliptic Seasons中有哪些让人期待的新特性？

一个显著的改变是冬天，这正是这个模组的起点。当在Minecraft的世界中足够寒冷时，天空将降雪而不是降雨，同时地上将逐渐积累雪层。
这符合我们对现实世界的认知，然而由于Minecraft的方块更新机制，这简直是灾难。

尽管看起来很美，但是当每放置一个新的雪方块时，Minecraft就需要逐个查找该XZ位置的最高Y点，同时向客户端发送更新，并要求更新渲染。
在单人世界中这或许不是个问题，但是在多人服务器中由于加载了大量的区块，这将消耗大量的资源去完成检索行为，导致服务器卡顿。
更糟糕的问题是在雪融之时，假设区块加载中，遍历区块的过程同样非常耗时，而且很难确定雪是否需要融化，同时容易触发Minecraft的临近更新机制，造成更大的服务器压力。
以上仅仅是当区块没有发生加载和卸载的情形，实际上未加载的区块由于不会进行查询处理，这将会更加让人困惑。
比如在炎热的夏季看到草原上全是雪方块。 或者是在冬季到达了应该下雪的区块却没有看到任何雪。

Ecliptic Seasons用全新的覆雪机制改变了这个局面。现在，当群系的基础温度不够寒冷时，虽然在冬季仍然会降雪，却不会触发方块更新机制。
同时方块将换上新装，薄薄的浅雪将逐渐覆盖这个世界，无论是泥土、草、树叶或者是您的屋顶。在您看不见的未加载区块，雪花也会以一种特别的方式落下。
您不会在夏天的草原看到不该看到的积雪，或者在冬天到达新家时仍然需要等待积雪刷新。
由于不需要在服务器层面进行过多的区块计算，这将大大减少服务器资源消耗，提高服务器性能，同时避免割裂的降雪体验。

4. Ecliptic Seasons还能让我获得什么？

* 随节气改变的昼夜长短变化，冬夜漫漫夏日长。
* 更丰富细致的降雨变化，且与群系结合。
* 基于群系的局部降雨系统，因此无需忍受阴天，在干旱的群系里纵享晴空万里。
* 兼顾季节温度与群系湿润度的扩展农业生长控制系统，您不会在沙漠里获得与沼泽相当的种植体验。
* 四季之声，感受四季变化。
* 一些视觉上的额外点缀，如夏日傍晚低矮花草中飞出的萤火虫或者是树叶的落叶飘飞。
* 以上这些功能都可以通过配置文件进行配置，选择权在您！
* 如果您是服务器管理者，可以用`/ecliptic`来设置时间或者是天气。
* 如果您是整合包制作者或者其他模组制作者，您可以通过API获取当前状态，或者是制作数据包，更多自定义内容正在路上。

5. Ecliptic Seasons的未来计划是什么？

如您所见，这个模组虽然已经做了相当的工作，除了更多细节之外，这里仍然有完善的空间。
首先是兼容性，尽管我们已经为了兼容性付出了巨大努力，
在1.20.1支持了Optfine，甚至在1.21的开发版本中能够支持Distant Horizons等模组，但是这里还有很长的路要走。
季节农业方面，模组内置了支持Farmer's Delight等模组的数据包且可扩展更多，在1.20.1中支持作为Dynamic Trees的季节提供商。
但是有些模组默认只支持Serene Seasons作为唯一季节系统来源，而有些模组需要进行手动配置。
再者是开发者支持，可以查看API这个包，未来也会进行扩展，注意1.20和1.21的API包内结构不一样。
由于时间问题，数据包这一部分将稍后详细说明。

6. Ecliptic Seasons的数据包快速支持。

目前支持两种类型数据包，分别是群系气候类型标签和作物物品方块生长控制标签。
对于群系气候类型，可以查看现有data文件夹下的标签。
* 其中`eclipticseasons:seasonal`表示这个群系具有明显的四季变化
* `eclipticseasons:monsoonal`表示这个群系具有干湿季节变化
* `eclipticseasons:is_small`标签是用于标记小群系的特别标签，一般无需使用
* 其余标签表示一年降水变化较为平缓，仅有降水量的变化。

对于作物这将复杂的多，除了使用多种不同的标签类型外，您还可以为物品或者无物品的方块指定分类。
一种作物目前只能有一种季节生长要求标签和一种湿润度生长要求标签。当在错误的环境下生长时，其生长速度会明显放缓，反之则加快。
* 对于季节，分为15种预设类型，您可以根据需要选择，分别为：`eclipticseasons:crops/spring`,`eclipticseasons:crops/summer`,`eclipticseasons:crops/autumn`,`eclipticseasons:crops/winter`,`eclipticseasons:crops/spring_summer`,`eclipticseasons:crops/spring_autumn`,`eclipticseasons:crops/spring_winter`,`eclipticseasons:crops/summer_autumn`,`eclipticseasons:crops/summer_winter`,`eclipticseasons:crops/autumn_winter`,`eclipticseasons:crops/spring_summer_autumn`,`eclipticseasons:crops/spring_summer_winter`,`eclipticseasons:crops/spring_autumn_winter`,`eclipticseasons:crops/summer_autumn_winter`,`eclipticseasons:crops/all_seasons`
* 对于湿润度，分为15种预设类型，分别为：`eclipticseasons:crops/arid_arid`,`eclipticseasons:crops/arid_dry`,`eclipticseasons:crops/arid_average`,`eclipticseasons:crops/arid_moist`,`eclipticseasons:crops/arid_humid`,`eclipticseasons:crops/dry_dry`,`eclipticseasons:crops/dry_average`,`eclipticseasons:crops/dry_moist`,`eclipticseasons:crops/dry_humid`,`eclipticseasons:crops/average_average`,`eclipticseasons:crops/average_moist`,`eclipticseasons:crops/average_humid`,`eclipticseasons:crops/moist_moist`,`eclipticseasons:crops/moist_humid`,`eclipticseasons:crops/humid_humid`




