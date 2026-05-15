# Spout Dungeons

A Spout plugin adding dungeon content via custom blocks, a bundled datapack, and a bundled resource pack.

## Requirements

- [Spout](https://github.com/ModernSpout/Spout) server (Paper fork with custom block/item support)
- Spout enabled on the server

## Features

### Disenchantment Table

A custom block (`spout_dungeons:disenchantment_table`) that:

- Emits light level 7
- Opens a menu on right-click; place an enchanted item and lapis, receive an enchanted book with all stripped enchantments
- Drops itself when broken
- Has a custom block model and blockstate via the bundled resource pack

### Disenchantment Ruin

A worldgen structure (`spout_dungeons:disenchantment_ruin`); a small stone-brick ruin with a Disenchantment Table at its center, generated in applicable biomes.

```mcfunction
/locate structure spout_dungeons:disenchantment_ruin
/give @s spout_dungeons:disenchantment_table
```

## Development

### Block and item pipeline

Adding a custom block/item requires files in three places:

**Data pack**; defines the block/item to Spout (server-side):
```
data_pack/data/<namespace>/block/<id>.json   ← block type, properties, vanilla mappings
data_pack/data/<namespace>/item/<id>.json    ← item type (minecraft:block → references the block)
data_pack/data/<namespace>/loot_table/blocks/<id>.json  ← what drops when broken
```

**Resource pack**; defines visuals to the client:
```
resource_pack/assets/<namespace>/blockstates/<id>.json  ← maps block states to models
resource_pack/assets/<namespace>/items/<id>.json        ← item model dispatch
resource_pack/assets/<namespace>/models/block/<id>.json ← geometry / texture references
resource_pack/assets/<namespace>/textures/block/<id>.png (or in minecraft/ if reusing vanilla)
```

**Plugin code**; opt-in, only needed for custom behaviour:
```java
// Resolve the Material at startup; null-safe, works on Paper too
Material mat = Material.matchMaterial("spout_dungeons:disenchantment_table", false);

// Listen for interactions / drops / etc. via standard Bukkit events
```

Spout wires the data pack and resource pack together at server startup via the bootstrapper (`SpoutDungeonsBootstrap`). Vanilla clients without the resource pack see the `mappings.fallback` block/item instead.

### Worldgen pipeline

The ruin spawns via a standard Minecraft jigsaw worldgen chain:

```
structure_set  →  random_spread placement (spacing: 12, separation: 4) picks attempt locations
structure      →  jigsaw type checks biome (#minecraft:is_overworld), projects to surface
template_pool  →  selects the core NBT piece
core.nbt       →  the actual blocks, pasted at the resolved position
```

### Updating the structure (core.nbt)

`core.nbt` is a Minecraft [structure file](https://minecraft.wiki/w/Structure_file) saved manually in-game; it is **not** generated at build time. To update it:

1. Build or modify the structure in the dev world
2. Place a **Structure Block** (`/give @s structure_block`), set to *Save* mode
3. Set the structure name to `spout_dungeons:disenchantment_ruin/core`, define the bounding box, click Save
4. Copy `run/world/generated/spout_dungeons/structures/disenchantment_ruin/core.nbt` into `src/main/resources/data_pack/data/spout_dungeons/structure/disenchantment_ruin/core.nbt`
5. Rebuild and redeploy the jar

