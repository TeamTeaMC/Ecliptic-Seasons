- The configuration screen now changes its button theme based on the current season.
- Improved configuration screen support for add-ons.

#### Minecraft 26.1+ Only

- Improved rendering compatibility between snow-covered models and several CTM mods.
- Reworked the construction and caching of runtime snow-covered models.
- Improved shader material identification and shading for snow-covered blocks.
- Added the `"eclipticseasons:force_cutout"` model material property, allowing specified model faces to use the Cutout render pass and preserve the correct overlay order.
- Fixed large visual artifacts and patterns appearing in the sky when used with Voxy ([#174](https://github.com/TeamTeaMC/Ecliptic-Seasons/issues/174)).

##### NeoForge Only

- Adjusted compatibility with FFAPI on Minecraft 26.1, fixing startup failures with certain version combinations.
- Temporarily removed several deprecated JEI tooltips to maintain compatibility with the latest JEI versions.

##### Fabric Only

- Improved runtime snow-covered model support for Fabric custom models and CTM models.