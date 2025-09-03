## Basic Description

This configuration is used to define block replacement, loot drops, or block generation rules that occur with seasonal changes in specific biomes (if omitted, biome checks are ignored).
Pack authors can write similar configuration files to add seasonal change effects to different blocks.

This datapack requires blocks that support **random ticks** in order to minimize atomic operations. If such blocks are not available, you can refer to the *Datapack Base* section of the datapack to force-enable random ticks for blocks.

It is a JSON file placed in the resource pack root directory at `data/<namespace>/eclipticseasons/season_definitions`.

## File Contents

### Example Definition

This example adds simple seasonal changes (spring and autumn) to the apple tree from the *Bountiful Fares* mod.

* **Spring**: Natural apple leaves (not player-placed) have a **16% chance** to turn into flowering leaves. A fixed seed is used to avoid excessive randomness. Additionally, a hanging apple is generated below flowering leaves (not duplicated).
* **Autumn**: Flowering leaves revert back to normal apple leaves. Hanging apples are replaced with their corresponding loot (simulating fruit ripening).

⚠️ Note: The `changes` field is structured as a **SolarTermValueMap**, which also allows more fine-grained adjustments based on **solar terms**.

Custom conditions and placement methods are also supported. See the codebase for more details on extensibility.


```json
{
  "biomes": "minecraft:plains",
  "changes": {
    "seasons": {
      "spring": [
        {
          "target": {
            "blocks": "bountifulfares:apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "fixed_seed": true,
          "chance": 0.16,
          "place": {
            "block": {
              "Name": "bountifulfares:flowering_apple_leaves"
            },
            "copy_state": true
          }
        },
        {
          "target": {
            "blocks": "bountifulfares:flowering_apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "place": {
            "block": {
              "Name": "bountifulfares:hanging_apple"
            },
            "replace": false,
            "offset": [
              0,
              -1,
              0
            ]
          }
        }
      ],
      "autumn": [
        {
          "target": {
            "blocks": "bountifulfares:flowering_apple_leaves",
            "state": {
              "persistent": "false"
            }
          },
          "place": {
            "block": {
              "Name": "bountifulfares:apple_leaves"
            },
            "copy_state": true
          }
        },
        {
          "target": {
            "blocks": "bountifulfares:hanging_apple"
          },
          "place": {
            "loot": "bountifulfares:blocks/hanging_apple"
          }
        }
      ]
    }
  }
}
```
