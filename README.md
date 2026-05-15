# Spout Dungeons

A Spout plugin adding dungeon content via custom blocks, a bundled datapack, and a bundled resource pack.

## Requirements

- [Spout](https://github.com/ModernSpout/Spout) server (Paper fork with custom block/item support)
- Spout enabled on the server

## Features

### Disenchantment Table

A custom block (`spout_dungeons:disenchantment_table`) that:

- Emits light level 7
- Opens a menu on right-click — place an enchanted item and lapis, receive an enchanted book with all stripped enchantments
- Drops itself when broken
- Has a custom block model and blockstate via the bundled resource pack

### Disenchantment Ruin

A worldgen structure (`spout_dungeons:disenchantment_ruin`) — a small stone-brick ruin with a Disenchantment Table at its center, generated in applicable biomes.

```mcfunction
/locate structure spout_dungeons:disenchantment_ruin
/give @s spout_dungeons:disenchantment_table
```

