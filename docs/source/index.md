![Ecliptic Seasons](https://github.com/user-attachments/assets/549d6626-d78e-4901-8b96-f420a6c2d3ea)

“节气”是Minecraft的一个中国风模组，关于四季。

Ecliptic Seasons is a Chinese mod about seasons and weather.

### LICENSE
*   For code: BSD-3
*   For resources: CC BY-NC-SA 4.0
*   Please do not port arbitrarily, communication can make the community better, and please respect our work.

## 1.What is Ecliptic Seasons?

As the name suggests, this mod brings a new seasonal experience to Minecraft, also known as "Solar Terms."
It is an ancient Chinese calendar that divides the year into 24 periods, each with its unique characteristics.
By understanding the right time and following the seasons, one can achieve harmony between humans and nature, the so-called "Unity of Heaven and Man."

## 2.Does Ecliptic Seasons overlap with mods like Serene Seasons, or can they work together?

As a seasonal mod, Ecliptic Seasons shares many similarities with Serene Seasons.
For example, both represent the Minecraft year through four seasons and change the colors of vegetation and control seasonal crop growth.
However, Minecraft has been around for 15 years, and we aim to go further.

## 3.What new features in Ecliptic Seasons are worth looking forward to?

One significant change is winter, which is where this mod began.
When it’s cold enough in the Minecraft world, the sky will snow instead of rain, and snow layers will gradually accumulate on the ground.
While this fits our understanding of the real world, the block update mechanism in Minecraft makes this a disaster.

Although it looks beautiful, every time a new snow block is placed, Minecraft has to search for the highest Y point at that XZ position, send an update to the client, and request a rendering update.
In single-player worlds, this might not be a problem, but in multiplayer servers where many chunks are loaded, this search behavior will consume a lot of resources, causing server lag.
The situation worsens when the snow melts. When chunks are loaded, traversing the chunks is equally time-consuming, and it’s hard to determine whether the snow should melt.
Additionally, Minecraft’s neighbor update mechanism may be triggered, leading to even more server strain.
And that’s just when chunks aren’t loaded and unloaded.
In fact, for unloaded chunks, no processing occurs, which can be even more confusing.
For example, you might see snow all over the plains in the heat of summer, or arrive in a winter area and see no snow at all.

Ecliptic Seasons changes this with a brand new snow-covering mechanism. Now, when the base temperature of a biome isn't cold enough, snow may still fall in winter but won't trigger block update mechanics.
Blocks will gradually be covered by a thin layer of snow, whether it's dirt, grass, leaves, or even your rooftop. In the unseen, unloaded chunks, snowflakes will fall in a special way.
You won’t find snow in summer plains, and when arriving at your new home in winter, there’s no need to wait for snow to refresh.
Since fewer chunk calculations are required at the server level, server resource consumption will be greatly reduced, improving performance and avoiding fragmented snowfall experiences.

![A Show](https://github.com/user-attachments/assets/e0d3c694-128c-427f-8d15-34910694f866)

## 4.What else can Ecliptic Seasons offer me?

* Daylight length changes with the seasons – long winter nights and long summer days.
* More detailed rainfall variations, integrated with biomes.
* A localized rainfall system based on biomes, so you can enjoy clear skies in dry biomes without enduring overcast weather.
* An extended agricultural growth control system based on seasonal temperatures and biome humidity. You won’t get the same growing experience in a desert as in a swamp.
* The sounds of the seasons – feel the changes of time.
* Additional visual touches, like fireflies flying out of low grass in summer evenings or leaves fluttering down from trees.
* All these features can be configured via the config file – the choice is yours!
* If you're a server admin, you can use `/ecliptic` to set the time or weather.
* If you're a mod pack creator or another modder, you can access the current state via the API or create data packs. More customization options are on the way.

## 5.What are the future plans for Ecliptic Seasons?

As you can see, although a lot of work has already been done on this mod, there’s still room for improvement beyond just adding more details.

The first priority is compatibility. While we’ve made significant efforts towards compatibility,
supporting Optifine in 1.20.1 and even mods like Distant Horizons in the development version of 1.21, there is still a long road ahead.
In terms of seasonal agriculture, the mod comes with built-in data pack support for Farmer's Delight and more, supporting compatibility as a seasonal provider for Dynamic Trees in 1.20.1.
However, some mods only support Serene Seasons as the sole source of seasonal systems by default, while others require manual configuration.

Next is developer support, where you can check out the API package, which will be expanded in the future. Please note that the API package structure differs between versions 1.20 and 1.21.
Due to time constraints, the data pack section will be explained in detail later.

## 6.Quick support for Ecliptic Seasons data packs.

Currently, two types of data packs are supported: biome climate type tags and crop block growth control tags.

For biome climate types, check the existing tags in the data folder.
* `eclipticseasons:seasonal` indicates that the biome has distinct seasonal changes.
* `eclipticseasons:monsoonal` indicates that the biome has seasonal wet and dry periods.
* The `eclipticseasons:is_small` tag is a special tag for marking small biomes, and generally doesn’t need to be used.
* The remaining tags, `eclipticseasons:rainless`, `eclipticseasons:arid`, `eclipticseasons:droughty`, `eclipticseasons:soft`, `eclipticseasons:rainy`, indicate biomes with only slight seasonal changes in rainfall, with only differences in precipitation amounts.

For crops, it’s more complex. In addition to using various tags, you can assign categories to tag item or block if they haven't a bind item.
A crop can only have one season growth requirement tag and one humidity growth requirement tag. When grown in the wrong environment, its growth rate will slow significantly, and vice versa.
* For seasons, there are 15 preset types to choose from based on your needs, each word indicates a suitable season: `eclipticseasons:crops/spring`, `eclipticseasons:crops/summer`, `eclipticseasons:crops/autumn`, `eclipticseasons:crops/winter`, `eclipticseasons:crops/spring_summer`, `eclipticseasons:crops/spring_autumn`, `eclipticseasons:crops/spring_winter`, `eclipticseasons:crops/summer_autumn`, `eclipticseasons:crops/summer_winter`, `eclipticseasons:crops/autumn_winter`, `eclipticseasons:crops/spring_summer_autumn`, `eclipticseasons:crops/spring_summer_winter`, `eclipticseasons:crops/spring_autumn_winter`, `eclipticseasons:crops/summer_autumn_winter`, `eclipticseasons:crops/all_seasons`
* For humidity, there are 15 preset types as well, the two words mean the lowest and the highest suitable humidity: `eclipticseasons:crops/arid_arid`, `eclipticseasons:crops/arid_dry`, `eclipticseasons:crops/arid_average`, `eclipticseasons:crops/arid_moist`, `eclipticseasons:crops/arid_humid`, `eclipticseasons:crops/dry_dry`, `eclipticseasons:crops/dry_average`, `eclipticseasons:crops/dry_moist`, `eclipticseasons:crops/dry_humid`, `eclipticseasons:crops/average_average`, `eclipticseasons:crops/average_moist`, `eclipticseasons:crops/average_humid`, `eclipticseasons:crops/moist_moist`, `eclipticseasons:crops/moist_humid`, `eclipticseasons:crops/humid_humid`
