# [Ecliptic Seasons](https://legacy.curseforge.com/minecraft/mc-mods/ecliptic-seasons)

![Ecliptic Seasons](https://github.com/user-attachments/assets/549d6626-d78e-4901-8b96-f420a6c2d3ea)

“节气”是Minecraft的一个中国风模组，关于四季。

Ecliptic Seasons is a Chinese mod about seasons and weather.

### LICENSE
*   For code: BSD-3
*   For resources: CC BY-NC-SA 4.0
*   Please do not port arbitrarily, communication can make the community better, and please respect our work.
*   For disc *Snowless Homeland*: authorized by Beishanwei & Orangesoda for distribution and instrumental adaptation.

## 1.What is Ecliptic Seasons?

As the name suggests, this mod brings a new seasonal experience to Minecraft, also known as "Solar Terms."
It is an ancient Chinese calendar that divides the year into 24 periods, each with its unique characteristics.
By understanding the right time and following the seasons, one can achieve harmony between humans and nature, the so-called "Unity of Heaven and Man."

## 2.Does Ecliptic Seasons overlap with mods like Serene Seasons, or can they work together?

Ecliptic Seasons is a completely new and standalone season provider mod.
Our goal is not to replace existing mods or duplicate their features, but to push Minecraft’s seasonal experience further, after more than a decade of its development.
It’s not just about changing biome colors, but also about evolving other plants; not only snow in winter, but transforming the appearance of blocks throughout the world.
It goes beyond simple seasonal restrictions, requiring crops to grow under suitable humidity.
And it adds richer visual and auditory details to create a truly immersive seasonal atmosphere.
At the same time, we strive to optimize performance as much as possible and welcome your active feedback.

![A Show](https://github.com/user-attachments/assets/e0d3c694-128c-427f-8d15-34910694f866)

## 3.What else can Ecliptic Seasons offer me?

* Changing day length with the solar terms — long winter nights and extended summer days.
* A localized rainfall system based on biomes and solar terms, so you won't have to endure endless overcast skies — enjoy clear blue weather in dry biomes.
* Crop growth that considers both seasonal temperature and biome humidity, plus a simple new greenhouse system.
* Animal breeding conditions change with the seasons (optional feature).
* Seasonal ambient sounds, biome colors, and even block models — feel time flowing through every detail.
* Extra visual touches, like fireflies rising from the grass on summer evenings or leaves drifting from trees in autumn.
* All of these features are configurable via config files, data packs, or resource packs — the choice is yours!
* Server operators can use `/ecliptic` to set time and weather directly.
* Mod pack creators and mod developers can access current seasonal states through the API, or create datapacks — more customization options are on the way.

## 4.Quick support for Ecliptic Seasons data packs.

For **overworld agro biomes**, the following special tags are provided:
* `eclipticseasons:agro/warm` – applied to **warm regions**
* `eclipticseasons:agro/cold` – applied to **cold regions**
* `eclipticseasons:agro/hot` – applied to **hot regions**

For **biome rain** types, check the existing tags in the data folder.
* `eclipticseasons:rain/seasonal`,`eclipticseasons:rain/seasonal/hot`,`eclipticseasons:rain/seasonal/cold` indicates that the biome has distinct seasonal changes.
* `eclipticseasons:rain/monsoonal` indicates that the biome has seasonal wet and dry periods.
* The `eclipticseasons:is_small` tag is a special tag for marking small biomes, and generally doesn’t need to be used.
* The remaining tags, `eclipticseasons:rain/rainless`, `eclipticseasons:rain/arid`, `eclipticseasons:rain/droughty`, `eclipticseasons:rain/soft`, `eclipticseasons:rain/rainy`, indicate biomes with only slight seasonal changes in rainfall, with only differences in precipitation amounts.

For **biome color** types, it's similar. And it's actually recommended to use resource packs to achieve more customized colors.
* `eclipticseasons:color/seasonal`, `eclipticseasons:color/seasonal/hot`, `eclipticseasons:color/seasonal/cold` — represent seasonal colors for normal, hot, and cold biomes respectively.
* `eclipticseasons:color/monsoonal` — represents colors for (tropical) monsoonal climate biomes.
* `eclipticseasons:color/stable`, `eclipticseasons:color/slightly` — represent areas with stable and slight color changes.

For crops, it’s more complex. In addition to using various tags, you can assign categories to tag item or block if they haven't a bind item.
A crop can only have one season growth requirement tag and one humidity growth requirement tag. When grown in the wrong environment, its growth rate will slow significantly, and vice versa.
* For seasons, there are 15 preset types to choose from based on your needs, each word indicates a suitable season: `eclipticseasons:crops/spring`, `eclipticseasons:crops/summer`, `eclipticseasons:crops/autumn`, `eclipticseasons:crops/winter`, `eclipticseasons:crops/spring_summer`, `eclipticseasons:crops/spring_autumn`, `eclipticseasons:crops/spring_winter`, `eclipticseasons:crops/summer_autumn`, `eclipticseasons:crops/summer_winter`, `eclipticseasons:crops/autumn_winter`, `eclipticseasons:crops/spring_summer_autumn`, `eclipticseasons:crops/spring_summer_winter`, `eclipticseasons:crops/spring_autumn_winter`, `eclipticseasons:crops/summer_autumn_winter`, `eclipticseasons:crops/all_seasons`
* For humidity, there are 15 preset types as well, the two words mean the lowest and the highest suitable humidity: `eclipticseasons:crops/arid_arid`, `eclipticseasons:crops/arid_dry`, `eclipticseasons:crops/arid_average`, `eclipticseasons:crops/arid_moist`, `eclipticseasons:crops/arid_humid`, `eclipticseasons:crops/dry_dry`, `eclipticseasons:crops/dry_average`, `eclipticseasons:crops/dry_moist`, `eclipticseasons:crops/dry_humid`, `eclipticseasons:crops/average_average`, `eclipticseasons:crops/average_moist`, `eclipticseasons:crops/average_humid`, `eclipticseasons:crops/moist_moist`, `eclipticseasons:crops/moist_humid`, `eclipticseasons:crops/humid_humid`
