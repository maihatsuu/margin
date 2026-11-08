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

## Zooming empty maps
- Maps stay vanilla by default: a plain empty map makes a level 0 map (1 block per pixel, 128×128
  blocks).
- An empty map can be zoomed at a cartography table, the same way as a filled map: empty map in the
  top slot, paper in the bottom, one level per paper, up to 4 (2048×2048 blocks). The empty map
  shows its level in its tooltip, and the filled map made from it starts at that level.
- This replaces zooming after the fact, which throws away what the map had drawn.
- The level is stored in vanilla's `custom_data` component, so no custom item or networking is
  needed. The server does the work; the client mod supplies the tooltip.

## Sides
Install on both client and server (the pack always does). The coordinates are client-side and empty
map zooming is mostly server-side; no networking between them.

## Config (`config/margin.json`)
| Option | Default | Side | Description |
|---|---|---|---|
| `showCoordinates` | true | client | Write coordinates on held maps. |
| `showY` | false | client | Include the Y coordinate. |

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
- Empty map + paper at a cartography table: level rises to 4 and stops; the tooltip shows it; the
  filled map covers the right area. Plain empty maps stay vanilla; filled map zoom still works.
- Using the last empty map in a stack keeps its level.
- Vanilla client on a server with the mod, and the mod on a client joining a vanilla server.
