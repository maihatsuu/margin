# Margin (working name)

A Fabric mod for Minecraft 1.21.1. When you hold a filled map, your coordinates are written along
its bottom edge, like a note in the margin. An in-world way to know where you are, without F3,
inspired by Legacy Console Edition's maps.

That's all it does: no HUD, no new items, nothing on maps in item frames.

## Coordinates on held maps
- Shown on **every held filled map**, main hand or offhand, whether or not the map covers where you
  are.
- **Rounded X / Z** by default, as whole block numbers (e.g. `X 1204  Z -338`). Y can be added in
  the config (`X 1204  Y 64  Z -338`).
- Drawn onto the map itself, so it moves, tilts and sways with the map in your hand. Small, dark
  "ink" text with no shadow or background, so it reads like it's written on the paper.
- Live: updates as you move.

### How
Client-side mixin at the end of `ItemInHandRenderer.renderMap(PoseStack, MultiBufferSource, int,
ItemStack)`. That method has already set the pose stack up in map space (the map is 128×128 units)
and is used for both the two-handed and one-handed (offhand) map views, so one injection covers
both. Draw the text with `Font.drawInBatch` using the same pose stack, buffer and packed light, near
the bottom edge, scaled down slightly so it fits inside the map border.

## Default map size
- New maps (from using an empty map) start at a configurable scale instead of vanilla's closest
  zoom. Scale 0 is vanilla (1 block per pixel, 128×128 blocks); each step doubles the area, up to
  4 (2048×2048 blocks).
- Cartography tables can still zoom maps out further, up to the usual max of 4.
- Server-side: map creation happens on the server, via a mixin on the scale argument passed when
  `EmptyMapItem` creates the map (`MapItem.create(...)`).

## Sides
Install on both client and server (the pack always does). The coordinates are client-side and the
map size is server-side; no networking between them.

## Config (`config/margin.json`)
| Option | Default | Side | Description |
|---|---|---|---|
| `showCoordinates` | true | client | Write coordinates on held maps. |
| `showY` | false | client | Include the Y coordinate. |
| `defaultMapScale` | 0 | server | Scale of newly made maps, 0 (vanilla) to 4. |

Optional Mod Menu / Cloth Config screen, like Snout.

## Open questions
- Name: Margin? Atlas? Ink?
- Exact text placement and size: bottom edge vs. a corner; may need a small scale-down so long
  numbers (e.g. `X -29999984`) fit.
- Text colour that stays readable on both light (plains) and dark (ocean, Nether) map areas.
  Maybe a faint paper-coloured strip behind it if dark maps are a problem, but only if needed.
- Should Iris shader packs affect it? (It's drawn with the map, so it should behave like the map
  does.)

## Testing
- Hold a map in the main hand, then the offhand: coordinates show both ways and follow the map's
  sway.
- Walk around: numbers update; negative values and large values fit.
- `showY` on/off.
- Map from a different area: still shows your coordinates.
- Item frame maps: no text.
- `defaultMapScale` 0 vs 2: a new map covers 128 vs 512 blocks; cartography table zoom still works.
- Vanilla client on a server with the mod, and the mod on a client joining a vanilla server.
