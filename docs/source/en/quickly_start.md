# **Ecliptic Seasons Player Guide**

*A Player Guide to Seasonal Worlds*

------

## 1. Introduction

The world of Minecraft is normally an environment that changes very little over time.
Day and night repeat in a constant cycle, weather occasionally shifts, but the environment itself rarely evolves in a
noticeable way as time passes.

**Ecliptic Seasons** aims to change this.

The mod introduces a seasonal system based on the **Twenty-Four Solar Terms**, allowing the world to evolve in a way
that more closely resembles a natural climate cycle. As time progresses, players will gradually observe environmental
changes such as:

- Day and night lengths changing with the seasons
- Climate gradually becoming colder or warmer
- Rain and snow probabilities changing according to solar terms
- Different plant and crop growth conditions
- Seasonal adjustments in animal behavior and environmental atmosphere

Unlike many seasonal mods that focus primarily on visual changes, **Ecliptic Seasons** is designed to build a **climate
system**.

Within this system, solar terms influence not only how the environment looks but also gradually affect:

- Weather patterns
- Agricultural conditions
- Temperature environment
- Biological behavior

------

## 2. A Solar-Term World

Ecliptic Seasons uses the **Twenty-Four Solar Terms** as its primary unit of time.

In this model, the time structure of the world is:

```
1 Year = 4 Seasons
1 Season = 6 Solar Terms
```

By default, each solar term lasts **7 Minecraft days**.

Therefore, a full annual cycle becomes:

```
24 Solar Terms × 7 Days = 168 Days
```

This cycle length can be adjusted in the configuration file to suit different servers or modpacks. For long-running
servers, it is recommended to set each solar term to **36–72 days** so that the seasonal progression better aligns with
real-world time, since one Minecraft day lasts only 20 minutes.

------

### Day–Night Changes

As seasons progress, the length of day and night will also change.

During summer, daytime becomes longer, while in winter the nights become longer. This effect is achieved through dynamic
adjustments to the daylight cycle.

For example:

| Season | Day Length |
|--------|------------|
| Summer | Longer     |
| Winter | Shorter    |

------

### Solar Term Notifications

When the world enters a new solar term, the system automatically displays a notification in the chat.

This helps players understand the current seasonal phase of the world. Example solar terms include:

- Spring Equinox
- Summer Solstice
- Autumn Equinox
- Winter Solstice

Players can use these indicators to anticipate environmental changes, such as when snowfall begins or when certain crops
become suitable for planting.

------

### Real-Time Synchronization (Optional)

Ecliptic Seasons also provides an optional feature that synchronizes in-game solar terms with real-world calendar dates.

When enabled:

```
Real-world date → Corresponding in-game solar term
```

For example:

- Real-world **Beginning of Spring** → In-game Beginning of Spring
- Real-world **Winter Solstice** → In-game Winter Solstice

This feature can enhance immersion on long-running servers. However, it may conflict with other time-related mods, so it
is disabled by default.

------

## 3. Weather and Climate System

The weather system in Ecliptic Seasons differs significantly from vanilla Minecraft.

In vanilla Minecraft, weather is **globally uniform**: once rain begins, the entire world experiences rain
simultaneously.

Ecliptic Seasons instead uses a **seasonal biome-based climate model**.

------

### Localized Weather

Because of the biome climate system, different regions of the world may experience different weather conditions at the
same time.

For example:

| Biome     | Weather          |
|-----------|------------------|
| Plains    | Rain             |
| Mountains | Snow             |
| Desert    | No precipitation |

Players may observe situations such as:

- Snow falling on one side of a mountain
- Rain occurring in a nearby forest
- Clear skies over the desert

This behavior is part of the climate system and is not an error.

------

### Precipitation Probability

Precipitation probability changes with solar terms.

For example, rainfall may become more frequent in plains during summer.

These changes are controlled through configuration multipliers such as:

```
RainChancePercentMultiplier
ThunderChancePercentMultiplier
```

It should be noted that these values only modify **global probability multipliers** and do not directly determine the
final weather outcome.

Final weather conditions are influenced by multiple factors:

- Global multipliers
- Biome probabilities (rainfall or thunder likelihood)
- Biome distribution in the region

------

### Unified Weather (Optional)

Some servers or modpacks may prefer the traditional **global weather** behavior.

Ecliptic Seasons provides an optional configuration that allows all overworld biomes to share the same precipitation
event.

Enable the following settings:

```
RainTogether = true
SnowTogether = true
```

In this mode, weather behavior becomes closer to vanilla Minecraft. Proper biome tagging and precipitation-enabled
biomes are required for this to work correctly.

------

### Weather Transitions

To prevent weather changes from feeling abrupt, Ecliptic Seasons introduces a **weather transition system**.

When players move between regions with different climates, weather transitions gradually rather than changing instantly.
For example, when entering a rainy region from a clear area, rainfall will gradually intensify instead of beginning
immediately.

This behavior is controlled by:

```
WeatherBufferDistance
WeatherTransitionSpeed
```

These parameters define the transition range and speed.

------

### Weather Voting Mechanism

In some cases, other mods may bypass the Ecliptic Seasons weather API and read global precipitation data directly.

To maintain consistent weather logic, the system includes a **weather voting mechanism**.

This mechanism evaluates weather conditions around player positions and determines the global weather state accordingly.

Configuration parameter:

```
WeatherVotePercent
```

This helps avoid weather logic conflicts in complex mod environments.

------

## 4. Agriculture and Humidity System

Ecliptic Seasons significantly expands the agricultural mechanics.

In vanilla Minecraft, most crops grow as long as two conditions are satisfied:

- Sufficient light
- Suitable planting blocks

In Ecliptic Seasons, crop growth is additionally affected by **season and environmental humidity**.

This design simulates real agricultural conditions and introduces more strategic farming decisions.

------

### Seasonal Crops

When seasonal agriculture is enabled, crops no longer grow equally in every season.

Different crops have preferred seasons. For example:

| Crop Type | Preferred Season |
|-----------|------------------|
| Wheat     | Spring / Summer  |
| Pumpkin   | Summer           |
| Beetroot  | Autumn           |

If crops are planted in unsuitable seasons, the following may occur:

- Significantly reduced growth speed
- Bone meal fails to accelerate growth
- Growth may stop entirely

Controlled by:

```
EnableSeasonalCrop = true
```

If disabled, crops revert to vanilla behavior.

Unlike typical seasonal mods, Ecliptic Seasons also allows **different growth probabilities across both biomes and solar
terms**. For instance, a crop suited for spring may grow differently during *Beginning of Spring* versus *Spring
Equinox*.

------

### Humidity Environment

In addition to seasonality, crops are influenced by **environmental humidity**.

Humidity sources include:

- Nearby water bodies (bubble columns provide unstable humidification)
- Biome climate conditions
- Rainfall
- Greenhouse structures

If humidity is insufficient, crops may fail to grow properly even during the correct season.

Controlled by:

```
EnableCropHumidityControl = true
```

When enabled, the system continuously monitors chunk environments and calculates humidity levels.

------

### Humidity Transition

To prevent sudden humidity changes from destabilizing farming behavior, the system provides a humidity transition
mechanism.

```
CropHumidityTransition = true
```

Biome humidity in Ecliptic Seasons is influenced by several factors and may fluctuate seasonally.

This option allows humidity values to change **gradually rather than discretely**, improving planting stability in
certain biomes.

------

### Bone Meal Restrictions

When seasonal agriculture is enabled, bone meal does not always succeed.

If a crop is in the wrong season or lacks sufficient humidity, bone meal may fail.

------

### Forced Compatibility Mode

Many crop-adding mods in Minecraft do not support the NeoForge/Forge event pipeline.

To address this issue, Ecliptic Seasons provides a **forced compatibility mode**.

```
ForceCompatMode = true
```

When enabled, all plants registered with the appropriate tags will follow the basic agricultural rules.

------

## 5. Greenhouse System

The **greenhouse** is one of the key agricultural mechanics in Ecliptic Seasons.

This system allows players to grow crops even during unsuitable seasons or humidity conditions by simulating a
controlled agricultural environment.

The greenhouse system consists of several components:

- Greenhouse structure
- Season Core
- Humidity regulation devices

------

### Greenhouse Detection

The core mechanism of the greenhouse system is **enclosed structure detection**.

The system checks building structures within a certain range around crops to determine whether a greenhouse environment
exists. The detection logic is simple: it verifies whether the space forms a closed structure.

Detection range is controlled by:

```
GreenHouseMaxDiameter
GreenHouseMaxHeight
```

Default values:

| Parameter        | Default |
|------------------|---------|
| Maximum Diameter | 32      |
| Maximum Height   | 10      |

If the structure meets the enclosure conditions, the area is treated as a greenhouse.

------

### Light Limitations

Greenhouses cannot completely ignore lighting conditions.

If the interior light level is too low, most crops may still stop growing.

This probability is controlled by:

```
LowLightGreenhouseFailChance
```

Higher values increase the failure probability in low-light environments.

Proper lighting design is therefore still necessary.

------

### Simplified Greenhouse Mode

For players who prefer less complex farming mechanics, a simplified mode is available.

```
SimpleGreenHouseMode = true
```

When enabled:

- No special core blocks are required
- No humidity devices are needed
- Only an enclosed structure is necessary

This mode is suitable for lightweight modpacks.

------

### Season Core

The **Season Core** is the central component of the greenhouse system.

It provides environmental adjustments within a certain radius, such as:

- Improving seasonal crop conditions
- Providing agricultural bonuses
- Expanding greenhouse influence range

Controlled by:

```
SeasonCoreRange = 15
```

Players can place multiple cores to cover large farms.

------

### Seasonal Prayer Ritual

The Season Core also supports a special mechanic: the **Seasonal Prayer Ritual**.

This ritual allows a greenhouse container without a core to generate a Season Core through a ritual process.

------

### Humidity Devices

Humidity devices regulate the internal environment of the greenhouse.

When enabled, they can significantly improve crop growth stability. Details can be viewed through JEI.

------

## 6. Temperature and Survival Environment

In addition to weather and agriculture systems, Ecliptic Seasons introduces **environmental temperature effects**.

The goal is not to fully replace survival-temperature mods but to provide more realistic environmental feedback so that
seasonal changes influence gameplay rather than being purely visual.

------

### Seasonal Temperature Changes

As seasons progress, the overall world temperature changes.

The system does not directly modify vanilla biome temperature values. Instead, it simulates climate shifts through *
*seasonal modifiers**.

Snowfall depends on biome temperature or specific configuration rules.

If snowfall timing is not explicitly configured, it will follow temperature distribution.

For example:

- Cold biomes such as **taiga** may still experience snow during spring.
- Warm biomes such as **plains** may only see snowfall late in winter.

------

### Seasonal Ice and Snow

During colder seasons, certain areas may accumulate snow or freeze water.

By default, this feature is disabled and replaced with **winter atmosphere effects**, which provide better server
performance.

Configuration:

```
IceAndSnow = false
IceAndSnowMelt = false
```

When enabled:

- Snow and ice appear more frequently during winter
- They gradually melt during warmer seasons

------

### Cold Biomes

Some biomes are considered **permanently cold regions**.

Even if seasonal ice and snow are enabled and the current season is warm, these biomes may still exhibit the following:

- Snow may not completely melt
- Water may still freeze

Configuration:

```
SnowKeepInSnowyBiomes = true
```

This ensures that environments such as high mountains or ice plains remain cold.

------

### Heatstroke Mechanic

During hot seasons, players may experience **heatstroke**.

If players remain too long in high-temperature biomes—especially around midday in summer—the system may apply negative
effects.

Configuration:

```
HeatStroke = true
```

This mechanic encourages simple environmental strategies such as:

- Resting in shaded areas
- Avoiding prolonged exposure during midday
- Staying near forests or water

The system is not intended to create extreme survival pressure but rather to reinforce seasonal atmosphere.

----

## 7. Environmental Effects

To make seasonal changes more visible, Ecliptic Seasons introduces a wide range of environmental visual effects.

These effects are primarily **decorative environmental feedback** and do not alter the world’s block structure.

------

### Snow Cover System

One of the most noticeable changes is the **snow cover effect**.

When winter arrives, a visual layer of snow may gradually appear on the ground surface.

Configuration:

```
SnowyWinter = true
```

This system differs from traditional snow layers. It does **not generate actual snow blocks**, but instead renders a
visual snow overlay on the terrain.

This design provides two major advantages:

1. It does not modify world block structures
2. It avoids large-scale block updates that could affect performance

Because of this, players cannot collect these snow layers using a shovel.

------

### Snow on Trees

Trees can also display snow-covered visuals during winter, especially on leaves.

Configuration:

```
SnowyTree = true
```

When enabled:

- Leaves display frost and snow effects
- Forests gain a distinctive winter appearance

This feature is mainly intended to enhance visual atmosphere.

------

### Lighting Influence

To maintain a natural appearance, snow does not accumulate near strong light sources.

Configuration:

```
NotSnowyNearGlowingBlock = true
```

For example, snow usually will not appear near:

- Torches
- Glowstone
- Areas with strong light levels

------

### Grass and Leaf Color Changes

Vegetation colors change gradually with the seasons.

Configuration:

```
SeasonalGrassColorChange = true
SeasonalColorChangeExtend = true
```

Example seasonal color transitions:

| Season | Vegetation Color   |
|--------|--------------------|
| Spring | Bright green       |
| Summer | Deep green         |
| Autumn | Yellowish or brown |
| Winter | Grayish            |

These gradual changes help the world appear more natural.

------

### Spring Flowers

During spring, random flowers may appear across grassy areas.

Configuration:

```
FlowerOnGrass = true
```

This effect simulates natural spring and summer vegetation growth.

------

### Seasonal Particles

The mod also adds several environmental particles to enhance atmosphere.

Examples include:

- Butterflies in spring
- Fireflies in summer
- Falling leaves in autumn
- Migrating birds

Configuration:

```
SeasonalParticles = true
```

These particles are primarily decorative and generally have minimal performance impact.

------

### Natural Ambient Sounds

Different seasons also introduce different ambient environmental sounds.

Configuration:

```
NaturalSound = true
```

Examples:

- Bird calls during spring
- Insect sounds in summer
- Wind sounds in autumn

These sounds change automatically with the season.

------

## 8. Configuration Guide

### Common Configuration Options

Ecliptic Seasons provides a wide range of configuration options that allow players and server administrators to
customize how the seasonal system behaves.

In most situations, the default configuration provides a stable and balanced experience. However, in certain server
environments or modpacks, adjusting some parameters can significantly improve gameplay.

The following are the most commonly modified configuration options.

------

### Season Length

The duration of each solar term determines the speed of seasonal progression.

```
LastingDaysOfEachTerm = 7
```

By default:

- One solar term = 7 days
- One year = 168 days

If slower seasonal changes are preferred, the value can be increased, for example:

```
LastingDaysOfEachTerm = 10
```

This extends the year to **240 Minecraft days**.

In server environments, longer seasonal cycles are often preferred. A common recommendation is allowing one solar term
to change every **one or two real-world days**, such as setting it to **72 Minecraft days**, so players have more time
to experience each season.

------

### Initial Solar Term

When a new world is created, the system begins from a specified solar term.

```
InitialSolarTermIndex = 4
```

Solar term indices range from:

```
1 ~ 24
```

Examples:

| Index | Solar Term          |
|-------|---------------------|
| 1     | Beginning of Spring |
| 7     | Beginning of Summer |
| 13    | Beginning of Autumn |
| 19    | Beginning of Winter |

Changing this value allows a world to begin in a different season.

------

### Weather Synchronization

By default, weather follows the **localized climate system**.

If more vanilla-like behavior is preferred, enable:

```
RainTogether = true
SnowTogether = true
```

When enabled:

- All overworld biomes share the same precipitation event
- Weather becomes more predictable

This mode may be easier to manage on some servers.

------

### Rain Probability

Rain frequency can be adjusted through multipliers.

```
RainChancePercentMultiplier = 40
ThunderChancePercentMultiplier = 20
```

Higher values result in:

- More frequent rainfall
- Increased probability of thunderstorms

------

### Seasonal Agriculture

The seasonal crop system can be completely disabled.

```
EnableSeasonalCrop = true
```

If disabled:

- Crops are no longer restricted by seasons
- Gameplay becomes closer to vanilla

This option is useful for lightweight modpacks.

------

### Humidity Control

The humidity system governs agricultural environments.

```
EnableCropHumidityControl = true
```

If disabled:

- Crops no longer check environmental humidity
- Farming becomes significantly easier

This option is recommended only when simplified gameplay is desired.

------

### Simplified Greenhouse Mode

The greenhouse system can be simplified.

```
SimpleGreenHouseMode = true
```

When enabled:

- Season Core is not required
- Humidity devices are unnecessary
- Only an enclosed structure is needed

This makes greenhouse construction much easier.

------

### Dynamic Day–Night Cycle

Day and night length variation is controlled by:

```
DynamicDaylightDuration = true
```

If disabled:

- Day and night lengths revert to vanilla behavior
- Seasons only affect environmental visuals

This option may be useful in certain technical modpacks.

------

### Particles and Visual Effects

If players encounter performance issues, some visual effects can be disabled.

For example:

```
SeasonalParticles = false
NaturalSound = false
```

These features mainly enhance atmosphere and do not affect core gameplay mechanics.

------

## 9. Migrating from Serene Seasons

Many players first encounter seasonal mods through **Serene Seasons**.
Ecliptic Seasons is not intended as a simple replacement but rather as a more flexible and extensible seasonal system.

Understanding the differences between the two is important when migrating modpacks.

------

### Core Design Differences

Serene Seasons follows a more traditional seasonal model, focusing mainly on:

- Crop season restrictions
- Snow generation behavior
- Basic temperature variation

Ecliptic Seasons aims to create a **complete climate system**.

Major additions include:

- A Twenty-Four Solar Term calendar system
- Localized weather simulation
- Humidity-based agriculture
- Greenhouse mechanics
- Environmental visual changes

Because of this, the design philosophies differ significantly.

------

### Snow System Differences

Serene Seasons uses **actual snow layers**, meaning:

- Snow generates real blocks
- The world terrain is modified

Ecliptic Seasons instead uses a **visual snow overlay system** by default.

This approach:

- Does not generate real snow layers
- Does not modify world blocks

Advantages include:

- Better performance
- Compatibility with distant rendering mods
- No permanent snow accumulation

------

### Weather System Differences

Serene Seasons uses a **global weather system**.

Ecliptic Seasons uses a **localized climate system** by default.

This means:

- Different biomes can experience different weather simultaneously
- Weather transitions feel more natural

If players prefer traditional weather behavior, they can enable:

```
RainTogether = true
SnowTogether = true
```

------

### Crop System Differences

Serene Seasons mainly relies on crop tags to control seasonal growth.

Ecliptic Seasons expands this with:

- Humidity systems
- Greenhouse agriculture
- Environmental modifiers

This makes farming mechanics more complex but also more strategic.

------

### API Compatibility

Many existing mods still rely on the **Serene Seasons API**.

To maintain compatibility, a **Serene API compatibility bridge** can be used.

This bridge mod will:

- Simulate the Serene Seasons API
- Forward seasonal data to Ecliptic Seasons

This allows mods that depend on Serene Seasons to continue functioning normally.

------

### Migration Suggestions

If players wish to migrate from Serene Seasons, the usual steps are:

1. Remove Serene Seasons
2. Install Ecliptic Seasons
3. Optionally install
    - **Serene Seasons API Stub (Ecliptic Seasons Bridge)**
    - **Ecliptic Seasons: Bundles**
    - **Ecliptic Seasons: MultiMod Patch**
4. Adjust agricultural configurations if necessary

In most cases, existing world saves remain unaffected, since Ecliptic Seasons does not modify vanilla block data.

------

### When Migration May Not Be Recommended

Migration should be considered carefully in the following situations:

- The modpack heavily depends on the Serene Seasons API
- A large number of Serene Seasons-based farming mods are installed
- The modpack already contains extensive Serene Seasons configuration

In such cases, testing with a new world before migration is recommended.

------

## Guide Summary

The core goal of **Ecliptic Seasons** is to introduce a more realistic sense of time and climate progression into
Minecraft.

Through the integration of solar terms, climate simulation, and environmental feedback, players experience a world that
evolves over time.

Whether it is:

- Spring flowers blooming
- Summer insects buzzing
- Autumn leaves falling
- Winter landscapes covered in snow

These seasonal changes make the Minecraft world feel more dynamic and alive.

----



----

## Ecliptic Seasons FAQ

### Frequently Asked Questions

------

### 1. Why do I hear thunder but it isn’t raining?

Ecliptic Seasons uses a **localized weather system**.

Whether precipitation occurs depends not only on the world’s weather state, but also on the climate rules of the biome
you are currently in. So situations like the following can happen:

- A nearby biome is experiencing a thunderstorm
- Your current biome does not meet the conditions for precipitation

In this case, you may still hear distant thunder.

If you want all biomes to share the same weather, enable:

```text
RainTogether = true
```

------

### 2. Why does it sometimes snow in spring?

The solar-term calendar is not a simple “four seasons switch.”

Snow can still happen under conditions such as:

- Low biome temperature
- High altitude
- Cold biomes

Examples include:

- Mountain biomes
- Ice plains
- Cold-climate forests

So seeing snow in early spring is normal.

------

### 3. Why aren’t my crops growing?

There are usually three reasons:

#### (1) The season is not suitable

Some crops grow well only in specific seasons.

#### (2) Humidity is too low

Crops require an appropriate **environmental humidity** level.

#### (3) Light level is too low

This is especially common inside greenhouses.

If bone meal does not work, it usually means the environmental conditions are not met.

------

### 4. Why isn’t my greenhouse working?

The greenhouse system requires certain structural conditions.

Common problems include:

- The greenhouse is not fully enclosed
- The structure exceeds the detection size limits
- Light level is too low

Default detection limits:

| Parameter | Default |
|-----------|---------|
| Diameter  | 32      |
| Height    | 10      |

If the structure is too large, some areas may not be recognized as part of the greenhouse.

------

### 5. Do I always need a Season Core for a greenhouse?

Not necessarily.

If you enable:

```text
SimpleGreenHouseMode = true
```

Then a greenhouse only needs to be an enclosed structure.

------

### 6. Why does it sometimes rain in winter instead of snow?

The precipitation type depends on:

- Biome temperature
- The current solar term

If the temperature is still high enough, it may rain instead of snow even during winter.

------

### 7. Why does the weather look strange near rivers?

The biomes on each side of a river can be different, so the weather can differ across the riverbanks as well.

------

### 8. Why do I get heatstroke in summer?

In hot biomes, if you stay exposed under the midday sun for too long during summer, **Heatstroke** may trigger.

You can disable it:

```text
HeatStroke = false
```

------

### 9. Why do some modded crops have no seasonal restrictions?

Not all modded crops register the required seasonal tags, so they may not be affected by seasonal crop rules.

------

### 10. Why does the weather change when I move around?

Weather includes a **transition system**.

When you enter a region with a different climate, the weather gradually shifts rather than switching instantly.

------

### 11. Why do modpacks recommend this mod?

Common reasons include:

- Better performance
- Does not modify world blocks
- More flexible configuration
- A more complex climate system
- Better compatibility with distant rendering mods

------

### 12. How can mods that depend on Serene Seasons continue to work?

Use **Serene Seasons API Stub (Ecliptic Seasons Bridge)**.

This bridge mod will:

- Simulate the Serene Seasons API
- Forward seasonal data to Ecliptic Seasons

This allows many mods that previously depended on Serene Seasons to keep working.

----

### 13. Snow-covered models may not work properly with some resource packs (e.g., FastGrass) and can cause Z-fighting

These resource packs modify the face geometry of vanilla block models, which may lead to rendering conflicts.

It is recommended to enable **`LegacySnowyBlock`** in the resource pack.
Additionally, enable **`CullTopFaceWithSnow`** in the client settings to cull the top faces of those models when snow is
present.

----
