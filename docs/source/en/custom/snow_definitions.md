## Basic Explanation

The snow-covered model is quite special. Because it involves game content interaction, it requires defining server-side
data when necessary.

It is a JSON file placed in the resource pack root path at `assets/<namespace>/eclipticseasons/snow_definitions`.  
In datapacks, it is placed at `data/<namespace>/eclipticseasons/snow_definitions`.

## File Content

If the file is placed in a datapack, it can be used for data interaction, such as whether snow sweeping or skiing (mod
compatibility) is allowed.  
If placed in a resource asset pack, some properties cannot be changed, but the model can still be modified.  
For models, generally, snow-covered models can be universal. For example, snowy azalea half slabs and snowy oak half
slabs models should not differ much.

### Definition Example

Below is an example of a snow-covered model for pink petals.

```json
{
  "blocks": "minecraft:pink_petals",
  "mid": "eclipticseasons:snowy_pink_petals",
  "flag": 1001,
  "offset": 1
}
```

### Parameter Explanation

#### Parameter: blocks【String|Object】

Defines the block target object, which can be an ID, a tag (starting with `#`), or a HolderSet.
HolderSet is quite complex, so it is generally recommended to use tags or IDs.

#### Parameter: mid【String】

Points to the ID in the model mapping.

#### Optional Parameter: mid2【String】

Points to the ID in the model mapping for blocks like leaves, where this can specify a non-top-level model mapping ID.

#### Optional Parameter: flag【Int】

Some special blocks need this property.

| Flag           | Meaning                                                        |
|----------------|----------------------------------------------------------------|
| 1000 (default) | Base state; block type changes when using shaders.             |
| 1001           | Snow-covered grass, flowers, etc.                              |
| 1100           | Snow-covered leaves (top layer).                               |
| 1101           | Snow-covered leaves (bottom layer).                            |
| 1200           | Snow-covered vines, etc., no height offset calculation needed. |

This may change in the future. If you want to be a bit lazy with some magic, you can set the flag to 999, 998 (with AO) or other
built-in values, and we will automatically generate snow-covered models without you writing them by hand.

Of course, the best way is to provide your own model, which looks best.

#### Optional Parameter: offset【Int】

Used to set the offset from plant-type blocks to the ground block, usually a positive number.

#### Pending Parameter: ignore_offset【Bool】

Planned to control whether to use the offset value.

#### Pending Parameter: snow_passable【Bool】

Planned to define whether snow can pass through this block. This will help extend capabilities, possibly making it
easier to create snow-covered bamboo, bottom fences, and similar models in the future.

#### Pending Parameter: properties【Object Array】 (Coming Soon)

Generally intended for datapacks. For normal BlockStates, model mapping files are sufficient to differentiate.
This is used to set some block states not to be snow-covered.
The list means full match is required.

**Sub-parameter: name【String】**

Block property name.

**Optional Sub-parameter: reverse【Bool】**

Whether to invert the matching result.

**Sub-parameter: matcher【Object】**

Used for matching, with two types: exact match and range match.

**Sub-object: Exact Match**

Provide a single result.

```json
{
  "value": "..."
}
```

**Sub-object: Range Match**

* Optional sub-parameters: ignore_min, ignore_max
* Optional sub-parameters: min, max

Used for numerical properties.
