
---

I have a Minecraft battlepass plugin that supports PlaceholderAPI for requirements. The plugin includes a command, `/addreward`, to add rewards with specific requirements. However, when I use the command, the reward details are incorrectly formatted in the configuration file. Additionally, the PlaceholderAPI requirement check is not working correctly. 

For example, when I run the following command:

```
/addreward 1 DiamondReward DIAMOND 12 "&6Diamond Reward" "&bThis is a diamond reward\n&bClaim it if you can!" "say %player% claimed a diamond reward!" "say %player% right-clicked the diamond reward!" "%statistic_mob_kills% 5 %statistic_player_kills% 10"
```

It results in this incorrect configuration:

```yaml
DiamondReward:
  itemDisplayName: '"&6Diamond'
  itemLore: Reward"
  requirements: {}
  material: DIAMOND
  leftClickCommands:
  - '"&bThis'
  rightClickCommands:
  - is
  slot: 12
```

The expected correct format should be similar to:

```
DiamondReward:
  itemDisplayName: '&6Diamond Reward'
  itemLore: |-
    &bThis is a diamond reward
    &bClaim it if you can!
    &bRequirement: %statistic_mob_kills%/5
    &bRequirement: %statistic_player_kills%/10
  requirements:
    '%statistic_mob_kills%': 5
    '%statistic_player_kills%': 10
  material: DIAMOND
  leftClickCommands:
  - 'say %player% claimed a diamond reward!'
  rightClickCommands:
  - 'say %player% right-clicked the diamond reward!'
  slot: 12
```

Additionally, the PlaceholderAPI requirement check needs to correctly evaluate the player's statistics to determine if they meet the reward's requirements. 





```
[16:23:24 INFO]: Checking requirement: %statistic_deaths% required: 3 actual: 0
[16:23:24 INFO]: xAlphabet_ did not meet the requirements for ExampleReward
```

When I have 44 deaths.
