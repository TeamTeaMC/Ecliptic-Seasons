# [Ecliptic Seasons](https://legacy.curseforge.com/minecraft/mc-mods/ecliptic-seasons)

![Ecliptic Seasons](https://github.com/user-attachments/assets/549d6626-d78e-4901-8b96-f420a6c2d3ea)

Ecliptic Seasons is a seasonal environment mod inspired by the traditional 24 Solar Terms, bringing dynamic seasons,
weather, agriculture, ecology, and atmosphere to Minecraft.

### LICENSE

* For code: BSD-3
* For resources: CC BY-NC-SA 4.0
* Please do not port arbitrarily, communication can make the community better, and please respect our work.
* For disc *Snowless Homeland*: authorized by Beishanwei & Orangesoda for distribution and instrumental adaptation.

### 1.Main Features

If summarized in one sentence, the goal of Ecliptic Seasons is:

**To bring a seasonal atmosphere and environmental simulation system to Minecraft, inspired by the traditional 24
Solar Terms.**

Unlike many seasonal mods that simply divide the year into four seasons or focus only on foliage colors, Ecliptic
Seasons divides the year into four seasons and twenty-four solar terms. As time progresses, weather, temperature,
daylight length, vegetation, agriculture, wildlife, sounds, particles, models, and environmental visuals can all
change alongside the seasons, creating a more immersive and dynamic world.

#### Seasons and Solar Terms

Ecliptic Seasons introduces a complete cycle of four seasons and twenty-four solar terms.

Each solar term can have its own environmental characteristics, allowing the world to transition gradually rather than
abruptly switching between spring, summer, autumn, and winter.

The mod also supports seasonal daylight variation, bringing longer summer days and longer winter nights to enhance the
feeling of time passing throughout the year.

#### Weather, Temperature, and Snow

Ecliptic Seasons expands Minecraft's vanilla weather system with seasonal climate patterns.

Different seasons have different weather tendencies. Rainy periods may bring more frequent rainfall, while colder
terms can increase the chance of snowfall.

Unlike vanilla Minecraft, where rain is always followed by clear weather, Ecliptic Seasons uses probabilistic weather
transitions. This allows for more natural weather patterns, including continuous rain, repeated snowfall, and
longer-lasting weather events.

Temperature affects snowfall, snow accumulation, and melting. Snow no longer appears instantly when winter arrives;
instead, it forms and disappears gradually based on weather and environmental conditions.

Players can choose between vanilla snow layers and the built-in snow scenery system provided by the mod.

#### Agriculture, Plants, and Ecology

Crop growth can be affected by both season and humidity.

During unfavorable seasons, crops may grow more slowly or stop growing altogether. Players can build greenhouses to
protect crops and continue farming throughout the year.

Trees, grass, and many plants can also change their appearance with the seasons. Autumn foliage colors, seasonal
vegetation states, and seasonal model variations help make the world feel more alive.

Animal breeding behavior can also be influenced by seasonal changes, creating a stronger connection between wildlife
and the environment.

#### Seasonal Atmosphere

Ecliptic Seasons is not only about gameplay mechanics — it is also about atmosphere.

The seasonal timeline can drive a wide range of environmental effects, including seasonal sounds, particles, models,
and block appearance changes.

Spring birdsong, summer insects, autumn winds, and winter ambience can all contribute to a more immersive experience.
Resource pack creators can also build seasonal music packs, allowing different seasons or solar terms to have their
own background music.

The same system can be used to support falling leaves, winter visual effects, seasonal decorations, custom models, and
many other seasonal visual features.

#### Compatibility, Customization, and Ecosystem

Ecliptic Seasons provides extensive configuration options, allowing players and server owners to customize season
length, weather behavior, crop mechanics, snow systems, visual effects, and many other environmental rules.

The mod is primarily developed for NeoForge and also provides a native Fabric implementation to support a wider range
of players and modpacks. Due to differences between loader ecosystems, some features, compatibility modules, and
add-ons may vary between platforms.

- NeoForge / Forge: [Ecliptic Seasons](https://www.curseforge.com/minecraft/mc-mods/ecliptic-seasons)
- Fabric: [Ecliptic Seasons : Fabricated](https://www.curseforge.com/minecraft/mc-mods/ecliptic-seasons-fabricated)

To improve compatibility with the existing seasonal mod ecosystem, Ecliptic Seasons supports many Serene Seasons tags,
making it easier for other mods and modpacks to integrate seasonal crop and biome logic.

Several companion projects are also available:

- [Serene Seasons API Stub Bridge](https://www.curseforge.com/minecraft/mc-mods/serene-seasons-api-stub-ecliptic-seasons-bridge) –
      Provides API compatibility for mods that depend on the Serene Seasons API.
- [Ecliptic Seasons: MultiMod Patch](https://www.curseforge.com/minecraft/mc-mods/ecliptic-seasons-multimod-patch) –
  Provides additional compatibility patches and integrations for supported mods.
- [Ecliptic Seasons: Bundles](https://www.curseforge.com/minecraft/mc-mods/ecliptic-seasons-bundles) – Provides
  seasonal datapacks, resource packs, and optional integration content.

Overall, Ecliptic Seasons is more than a foliage or weather mod.

It serves as a seasonal framework that connects weather, temperature, daylight, vegetation, agriculture, wildlife,
sounds, particles, models, and environmental changes into a unified seasonal timeline, creating a richer and more
immersive world throughout the year.

### 2.Quick support for Ecliptic Seasons data packs.

For **overworld agro biomes**, the following special tags are provided:

* `eclipticseasons:agro/warm` – applied to **warm regions**
* `eclipticseasons:agro/cold` – applied to **cold regions**
* `eclipticseasons:agro/hot` – applied to **hot regions**

For **biome rain** tags, check the existing tags in the data folder.

* `eclipticseasons:rain/rainless` prevents rainfall in the biome.
* `eclipticseasons:rain/monsoonal` indicates that the biome has seasonal wet and dry periods.
* `eclipticseasons:rain/seasonal`, `eclipticseasons:rain/seasonal/hot`, `eclipticseasons:rain/seasonal/cold`,
  `eclipticseasons:rain/arid`, `eclipticseasons:rain/droughty`, `eclipticseasons:rain/soft`, and
  `eclipticseasons:rain/rainy` mainly provide minor adjustments to humidity calculations.
* Note that if `NotRainInDesert` is enabled, vanilla rainless biomes will remain rainless regardless of these tags.
* The `eclipticseasons:is_small` tag is a special tag for marking small biomes, and generally doesn’t need to be used.

For **biome color** types, it's similar. And it's actually recommended to use resource packs to achieve more customized
colors.

* `eclipticseasons:color/seasonal`, `eclipticseasons:color/seasonal/hot`, `eclipticseasons:color/seasonal/cold` —
  represent seasonal colors for normal, hot, and cold biomes respectively.
* `eclipticseasons:color/monsoonal` — represents colors for (tropical) monsoonal climate biomes.
* `eclipticseasons:color/stable`, `eclipticseasons:color/slightly` — represent areas with stable and slight color
  changes.

For crops, it’s more complex. In addition to using various tags, you can assign categories to tag item or block if they
haven't a bind item.
A crop can only have one season growth requirement tag and one humidity growth requirement tag. When grown in the wrong
environment, its growth rate will slow significantly, and vice versa.

* For seasons, there are 15 preset types to choose from based on your needs, each word indicates a suitable season:
  `eclipticseasons:crops/spring`, `eclipticseasons:crops/summer`, `eclipticseasons:crops/autumn`,
  `eclipticseasons:crops/winter`, `eclipticseasons:crops/spring_summer`, `eclipticseasons:crops/spring_autumn`,
  `eclipticseasons:crops/spring_winter`, `eclipticseasons:crops/summer_autumn`, `eclipticseasons:crops/summer_winter`,
  `eclipticseasons:crops/autumn_winter`, `eclipticseasons:crops/spring_summer_autumn`,
  `eclipticseasons:crops/spring_summer_winter`, `eclipticseasons:crops/spring_autumn_winter`,
  `eclipticseasons:crops/summer_autumn_winter`, `eclipticseasons:crops/all_seasons`
* For humidity, there are 15 preset types as well, the two words mean the lowest and the highest suitable humidity:
  `eclipticseasons:crops/arid_arid`, `eclipticseasons:crops/arid_dry`, `eclipticseasons:crops/arid_average`,
  `eclipticseasons:crops/arid_moist`, `eclipticseasons:crops/arid_humid`, `eclipticseasons:crops/dry_dry`,
  `eclipticseasons:crops/dry_average`, `eclipticseasons:crops/dry_moist`, `eclipticseasons:crops/dry_humid`,
  `eclipticseasons:crops/average_average`, `eclipticseasons:crops/average_moist`, `eclipticseasons:crops/average_humid`,
  `eclipticseasons:crops/moist_moist`, `eclipticseasons:crops/moist_humid`, `eclipticseasons:crops/humid_humid`
