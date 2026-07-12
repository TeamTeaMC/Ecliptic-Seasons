- Improved crop growth information under poor network conditions. If a crop is affected by the seasonal growth system,
  its name is now displayed immediately while detailed information is being retrieved from the server.
- Introduced `SeasonalLevelLoadEvent` to replace direct reliance on Neo/Forge's `LevelEvent.Load` for client world
  initialization. Unlike the original event, it is only fired for the active client world, avoiding issues caused by
  temporary or fake `ClientLevel` instances created by other mods.
- Removed the unused `BIOME_WEATHER_QUERY_LIST` cache from `WeatherManager`. This legacy cache was no longer used and
  could increase the risk of memory leaks in complex modded environments due to unexpected `ClientLevel` lifecycles.
