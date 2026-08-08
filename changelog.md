* Optimized memory usage in Distant Horizons and Embeddium compatibility code, contributed by @zerastos (#185).

  * Reduced temporary allocations during Distant Horizons winter LOD processing.
  * Reduced callback and local-reference allocations during Embeddium chunk meshing.
  * Updated biome color hooks to use primitive return-value modifiers, reducing callback and boxing overhead.
