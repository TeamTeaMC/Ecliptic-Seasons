# Ecliptic Seasons 0.14.0

## A New Approach to Winter Snow

Winter snow has always been one of the more complex parts of Ecliptic Seasons.

In version 0.13, we experimented with preventing rivers from receiving snow coverage to avoid unnatural situations, such
as rivers in desert regions appearing with the same snowy behavior as cold climates.

While this solved some visual inconsistencies, it also meant that rivers could no longer respond naturally to their
surrounding climate conditions.

Before that, we also explored another approach: suppressing river biomes and using nearby land biomes to determine
winter behavior. However, this required additional environmental checks and introduced unnecessary chunk processing
overhead.

In 0.14.0, we have redesigned this system.

Rivers are no longer excluded from winter behavior or replaced with surrounding terrain information. Instead, their
winter appearance is now determined by the climate conditions of the area they belong to:

- Rivers in cold climates can naturally develop winter snow effects.
- Rivers in warmer climates retain their corresponding environmental characteristics.
- Winter landscapes now better reflect the actual climate distribution of the world.

This change reduces unnecessary processing while making winter scenery more natural and consistent.

## Migration Notice

For players upgrading from earlier versions, old `surface_biome_cache` data may still affect existing chunks.

We recommend:

1. Disable:`Weather -> IndependentRiverWeather`

(in the `Resource` category in the configuration file)

2. Run: `/ecliptic debug reset surface_biome_cache`

to clear the old cached data.

After migration, the new winter river system will be applied.

