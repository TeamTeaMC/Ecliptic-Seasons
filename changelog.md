## Amid the Turning Seasons / 风轻轻，雨淋铃

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

_Perhaps this is the second most important update in the history of the **Ecliptic Seasons** mod!_

This is also the first time we release a release-candidate update at the end of a preview version for testing. It includes approximately 3,700 lines of code. Yes, this update was originally planned for the end of the month.  
However, since all versions together introduced about 15,000 lines of new code, more testing is required to ensure stability.  
If no issues are reported, it will eventually be marked as a stable release.

#### New Features

- Added a configuration GUI, no additional prerequisite mods required
  - Frequently used settings are placed at the front
  - Less commonly used settings are grouped on the last page
  - Tabs allow switching between different configuration categories
  - Supports configuration of Mixins
  - Some settings require a game restart (e.g., Mixin changes)
  - Supports ModMenu (26.1-Fabric) and Configured (1.20.1-Forge)
  - Advanced button allows returning to the default config screen
  - Added config button and multilingual support for 1.20.1
  - Some options must be adjusted in-game:
    - Dimensions where seasons are valid
    - Blocks excluded from snow coverage
  - Supports specialized configuration for certain options:
    - Valid dimension selection
    - Daylight duration adjustment
    - Seasonal behaviors for animals / bees / fishing
    - Blocks excluded from snowy
  - Config synchronization is always server-to-client and resets when back to menu; modifying Common configs in multiplayer is not recommended
  - Does not support complex options unsuitable since vanilla layouts:
    - Snowline
    - Custom block seasonal color overrides

![GUI Shown](https://media.forgecdn.net/attachments/1665/963/config.png)

- Added festival system `eclipticseasons:special_days`
  - Includes built-in festivals:
    - flower_festival
    - spring_festival
    - spring_outing
    - easter
    - chinese_valentines_day
    - mid_autumn
    - christmas
    - new_year
  - Can be overridden via datapacks or disabled via `eclipticseasons:extra_info`
  - Supports querying current festivals via API or commands

![Specials Days and Music](https://media.forgecdn.net/attachments/1665/964/specialdays.png)

- Added seasonal music system `eclipticseasons:background_music`
  - Supports playback only during specific festivals
  - Does not interrupt currently playing music; only plays when no other track is selected
  - Includes example configurations
  - Due to copyright and mod size limitations, no built-in seasonal music is provided
  - Example:

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
````

* Updated calendar display

    * Added sub-seasons:

        * early_spring / mid_spring / late_spring
        * early_summer / mid_summer / late_summer
        * early_autumn / mid_autumn / late_autumn
        * early_winter / mid_winter / late_winter
        * none
    * Sub-seasons are visible in calendar, debug UI, and commands
    * Supported via API
    * Dimension-based (not biome-dependent)
    * Supported in `eclipticseasons:crop` datapack:

```json
{
  "climate": "eclipticseasons:temperate",
  "apply_target": {
    "blocks": "#eclipticseasons:crops/spring"
  },
  "sub_seasons": {
    "early_spring": { "grow_chance": 0.83 },
    "mid_spring": { "grow_chance": 1.125 },
    "late_spring": { "grow_chance": 0.9 },
    "early_summer": { "grow_chance": 0.25 },
    "mid_summer": { "grow_chance": 0.15 },
    "late_summer": { "grow_chance": 0.05 },
    "early_autumn": { "grow_chance": 0.2 },
    "mid_autumn": { "grow_chance": 0.1 },
    "late_autumn": { "grow_chance": 0.05 },
    "early_winter": { "grow_chance": 0.05 },
    "mid_winter": { "grow_chance": 0.0 },
    "late_winter": { "grow_chance": 0.0 }
  }
}
```

* Also applies to all data/resources using SolarTermValueMap:

    * `eclipticseasons:biome_climate_setting`
    * `eclipticseasons:biome_rain`
    * `eclipticseasons:season_definitions`
    * `eclipticseasons:biome_colors`
    * `eclipticseasons:particles/fallen_leaves`

Example:

```json
{
  "biomes": "minecraft:plains",
  "foliage_colors": {
    "sub_seasons": {
      "early_spring": { "color": -12012264, "mix": 0.0 },
      "mid_spring": { "color": -12012264, "mix": 0.16 },
      "late_spring": { "color": -12012264, "mix": 0.32 }
    }
  },
  "grass_colors": {
    "sub_seasons": {
      "early_spring": { "color": -12012264, "mix": 0.0 },
      "mid_spring": { "color": -12012264, "mix": 0.16 },
      "late_spring": { "color": -12012264, "mix": 0.32 }
    }
  }
}
```

* Added months and day-of-month

    * 12 months per year, aligned with real-world structure
    * Each month roughly corresponds to two solar terms or one sub-season
    * Long solar terms still maintain month alignment
    * Months are not aligned with solar terms/seasons and are not datapack-controlled
    * Note distinction between Gregorian year and solar-term year (default starts Feb 3, Start of Spring)

* Block model seasonal color support (single-tint only):

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

#### Major Changes

* Removed biome-based local weather system

    * Now proxies dimension-level weather
    * Related Mixins removed
    * API remains for backward compatibility
    * Default weather parameters align with plains biome

* Weather system overhaul

    * Replaced hardcoded logic with datapacks
    * Rainfall system fully redesigned:

        * Duration, frequency, and intensity vary by solar term
        * Fixed issue where thunderstorms always triggered in certain seasons

![Solar Terms and Rain](https://media.forgecdn.net/attachments/1665/966/weather.png)

#### Fixes and Optimizations

* Optimized snow-covered block map color system

    * No longer globally overrides map colors
    * Added dedicated snow info for held maps
    * Added dust particle colors for falling blocks (e.g., anvils, concrete powder)
    * May improve map initialization speed with map mods

* Fixed `eclipticseasons:ambient` sound field limitation

    * Now supports sounds registered via vanilla `sounds.json`
    * add field `ignored_biomes`

* Added `fixed_seed_chance` to `season_definitions`

    * Controls variation speed when `fixed_seed` is enabled

* Fixed snow-covered glass pane issue

* Added command and debug support for new features

* Commands:

    * `/ecliptic config` (Neo/Forge)
    * `/eclipticc config` (Fabric)

* Debug overlay toggle: `LCtrl + D`

* Built-in snow model improvements for Sodium stairs

* Optimized `Platform` class to avoid premature loading issues

#### API Changes

* New APIs:

    * `getSeason` → `Season`
    * `getSubSeason` → `Season.Sub`
    * `getStandardMonth` → `Month`
    * `getDayOfMonth` → `int`
    * `getSpecialDays` → `List<Holder<SpecialDays>>`

* Removed datapack:

    * `eclipticseasons:weather_region`

* Removed method:

    * `Platform.getServer`

#### Compatibility Adjustments

* Voxy

    * Removed built-in support for the port for 1.20.1 and 1.21.1
    * Now provided via standalone mod `Ecliptic Seasons : Voxy Compact`
    * for Voxy for Minecraft 26.1 retains built-in support

* Distant Horizons

    * No longer requires a specific version
    * Can disable via `config/eclipticseasons-mixins.toml`
    * Only snowy models and seasonal color updates require Mixins
    * Disabled automatically if version < 3.0.0-b

