{
  "parent": "eclipticseasons:quests/autumn_seed",
  "criteria": {
    "core_require": {
      "conditions": {
        "location": [
          {
            "condition": "minecraft:location_check",
            "predicate": {
              "block": {
                "blocks": "minecraft:carved_pumpkin"
              }
            }
          },
          {
            "condition": "minecraft:match_tool",
            "predicate": {
              "items": "#c:tools/shear"
            }
          }
        ]
      },
      "trigger": "minecraft:item_used_on_block"
    },
    "parent_need": {
      "conditions": {
        "parent": "eclipticseasons:quests/autumn_seed"
      },
      "trigger": "eclipticseasons:parent"
    }
  },
  "display": {
    "description": {
      "translate": "advancement.eclipticseasons.autumn_carved_pumpkin.desc"
    },
    "icon": {
      "id": "minecraft:carved_pumpkin"
    },
    "show_toast": false,
    "title": {
      "translate": "advancement.eclipticseasons.autumn_carved_pumpkin"
    }
  },
  "requirements": [
    [
      "parent_need"
    ],
    [
      "core_require"
    ]
  ],
  "sends_telemetry_event": true
}