## Basic Description

Season phases are mainly used to modify the display of the calendar and term hints.

They are JSON files placed in the resource pack root directory at `data/<namespace>/eclipticseasons/season_phase`.

## File Content

The files have some default values and use Bitmap fonts to add characters in text rendering.

### Definition Example

Below is an example file for a rainy season area, which borrows some elements from other files.

```json
{
  "color": "green",
  "icon": {
    "texture": "eclipticseasons:season_phase/wet_middle"
  },
  "font": {
    "id": "eclipticseasons:monsoon_icons",
    "label": "h"
  },
  "season": "summer",
  "name": "eclipticseasons:wet"
}
````

You also need to add some lang translation entries:

```
    "info.eclipticseasons.environment.season_phase.wet": "Wet",
    "info.eclipticseasons.environment.season_phase.pattern.wet": "%s (All Year)",
    "info.eclipticseasons.environment.season_phase.alternation.wet": "Oh, never so wet."
```

If you don't want defaults, consider a full completion; all these values need to be prepared in advance.
It is recommended to provide a standalone icon with size 30 to omit these parameters, for example:

```
"icon": {
    "i": 1,
    "j": 3,
    "texture": "eclipticseasons:font/seasons_icons",
    "width": 180,
    "height": 120,
    "size": 30
}
```
