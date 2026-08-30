# Fog Delete (v1.0.0)

Client-side fog-of-war and darkness removal mod for Mindustry (`v146+`).

---

## Overview

This mod forcibly removes fog-of-war and lighting/darkness overlays on every map load, revealing the entire map immediately regardless of the map's original rules or previously-baked static fog data.

---

## Removal Matrix (3 Layers)

| Layer | Target | Strategy | Description |
|---|---|---|---|
| 1 | `Rules.fog` / `Rules.staticFog` | Rule Override | Disables the fog-of-war system entirely so it never re-applies on future frames. |
| 2 | `Rules.lighting` / `Rules.ambientLight` | Rule Override | Disables ambient darkness rendering (used on cave/dark maps) by zeroing out ambient light alpha. |
| 3 | `FogControl.getDiscovered(team)` | Live Bitmap Patch | Forces the already-baked static "undiscovered" bitmap for every team to fully "discovered", then re-syncs it to the GPU texture. |

---

## Applied Rule Changes

| Rule | Value | Effect |
|---|---|---|
| `fog` | `false` | Fog-of-war system disabled |
| `staticFog` | `false` | Static (black, undiscovered) fog disabled |
| `lighting` | `false` | Ambient darkness system disabled |
| `ambientLight` | `(1,1,1,0)` | Ambient light fully transparent (no visual effect even if re-enabled) |

---

## Installation

```
Mindustry's mod menu can install directly from a GitHub repository, without manually downloading the jar.

1. In-game: Settings → Mods → Import Mod
2. Paste the repository link (or `username/repo` shorthand), e.g. `https://github.com/YOUR-USERNAME/fog-delete` or `YOUR-USERNAME/fog-delete`
3. Mindustry automatically fetches the latest GitHub Release's jar and installs it
4. Restart Mindustry

This only works because the repository publishes proper GitHub Releases with the built jar attached (see CI/CD below). If a release is missing or the jar name doesn't match, this method will fail — use Option 2 instead.
```

or 

```
1. Grab the latest `fog-delete.jar` from Releases
2. In-game: Settings → Mods → Import Mod → select the downloaded jar file
3. Restart Mindustry
```

---

## Known Limitations

- Depends on internal engine classes (`FogControl`, `Bits`, `Rules`) that may change between Mindustry versions and break this mod.
- `getDiscovered()` returning `null` for a team that hasn't loaded yet is handled gracefully (skipped), but very early-frame edge cases haven't been exhaustively tested.

---

## License

MIT.
