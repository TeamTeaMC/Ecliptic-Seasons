## 客户端配置补充说明

### Renderer

* `ForceChunkRenderUpdate`: 低性能影响。由于默认设置里，节气落雪是不直接存储为世界方块的。因此需要强制更新部分客户端区块渲染。
* `EnhancementChunkRenderUpdate`: 默认关闭。仅当电脑性能充足时开启，强制地表所有区块。

### Particle

* `SpawnWeight`: 注意这个值实际上是作为随机边界的，所以值越大，实际粒子概率越小。

### Weather

如果开启了Solar Weather，那么需要注意此处的配置是否符合自己的需求。
许多客户端模组和光影都依赖于全局设置，此处设置主要是为了更好的天气渲染美观度。
其主要逻辑是对玩家周围天气进行采样，综合之后得到一个新的全局天气数值。
如果觉得被奇怪的雨滴声影响，一般推荐使用较低的`WeatherBufferDistance`值。

## 一般配置补充说明

### Season

* `LastingDaysOfEachTerm`: 设定每个节气的长度，注意一个季节有六个节气。
* `InitialSolarTermIndex`: 设置初始的节气序号，默认为春分。
* `DynamicDaylightDuration`: 使得日照时间随季节变化。
* `ValidDimensions`: 应用季节的维度，注意在1.20该选项不会同步给客户端，需要服务器自行同步。

### Snow
* `SnowyWinter`: 可能会有玩家以为这个设置是客户端的，实际上覆雪计算需要考虑服务器端需求。

### Weather

* `UseSolarWeather`: 是否启用局部天气系统。如果碰到不兼容的情况，如有些模组使用一些全局天气参数，特别是服务器端模组，可以用它调整。
* `ShouldInitWeather`: 是否初始化天气参数，可以在世界创建时就下雨。此外，也会初始化群系的覆雪情况。

### Temperature

* `HeatStroke`: 是否启用中暑。
* `IceAndSnow`: 原版降雪和结冰，注意这会影响世界。
* `IceAndSnowMelt`: 原版雪和冰融化，注意这会影响世界。

### Map

* `ChangeMapColor`: 修改地图颜色，如MC地图上颜色，部分地图模组可能不支持。

### Resource

* `ExtraSnowDefinitions`: 额外覆雪方块和外观资源包，一般是植物，实际上包含了数据包和材质资源包资源。



