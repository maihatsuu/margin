# Margin

A Fabric mod for Minecraft 1.21.1 that writes on your maps. Hold a filled map and your coordinates
and current biome appear in its margins, like notes in ink: an in-world way to know where you are
without opening F3. It also lets you zoom out a map before you start drawing it.

There's no HUD and there are no new items.

## Features

### Coordinates and biome on held maps
- Your coordinates are written along the bottom edge of any filled map you hold, in either hand, and
  the biome you're standing in is written along the top.
- They always describe where you are, even if the map shows somewhere else.
- The text is drawn on the map itself, so it moves and sways with the map, and updates as you walk.
- Maps in item frames stay as they are.

### Zooming empty maps
In vanilla, zooming out a map wipes what it had drawn, so the zoomed map has to be explored again.
With Margin you can zoom the empty map first:

1. Put an empty map in the top slot of a cartography table, and paper in the bottom slot.
2. Take the result. Each paper adds one level, up to level 4 (2048×2048 blocks), and the map's
   tooltip shows its level.
3. Use it as normal, and the filled map starts at that level.

Plain empty maps still make vanilla level 0 maps, and filled maps zoom as usual.

## Installation

Requires Minecraft 1.21.1, Fabric Loader 0.20.1 or newer, Fabric API, and Java 21 or newer.

Install it on both the client and the server. The writing on maps only needs the client, and
zooming empty maps needs the server.

[Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config)
are optional, and together add an in-game config screen.

## Configuration

Options are in `config/margin.json`, or on the config screen if you have the optional mods.

| Option | Default | Description |
| --- | --- | --- |
| `showCoordinates` | `true` | Write your coordinates on held maps. |
| `showY` | `false` | Include the Y coordinate. |
| `showBiome` | `true` | Write your current biome on held maps. |

## Building

```sh
./gradlew build
```

The jar is written to `build/libs/`. Use `./gradlew runClient` to launch a development client.

## License

[CC0 1.0](LICENSE)
