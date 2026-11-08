# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Margin is a Fabric mod (mod id `margin`) for Minecraft 1.21.1. Options are read once at startup from `config/margin.json` (`MarginConfig`, loaded from the main entrypoint).

The design for the mod is in `Margin.md`: coordinates drawn on held maps (client) and a configurable default scale for new maps (server). Read it before implementing features, and treat its "Open questions" section as undecided.

## Commands

Run from this directory (`margin-1.21.1/`), which is the Gradle root.

```sh
./gradlew build       # compile and package; jars land in build/libs/
./gradlew runClient   # launch a dev Minecraft client with the mod
./gradlew runServer   # launch a dev dedicated server with the mod
./gradlew genSources  # decompile Minecraft for browsing/searching vanilla code
```

No tests or linters are configured. `./gradlew build` is the only CI check (`.github/workflows/build.yml`, on JDK 25). Compilation targets Java 21 regardless of the JDK running Gradle.

Minecraft, Fabric Loader, Loom, and Fabric API versions are set in `gradle.properties`.

## Architecture

**Split source sets.** Loom's `splitEnvironmentSourceSets()` separates the code by environment:

- `src/main` holds common code that runs on both client and dedicated server. It cannot reference client-only classes such as `net.minecraft.client.*`.
- `src/client` holds client-only code and can reference `src/main`, not the other way round.

Each side has its own entrypoint, registered in `src/main/resources/fabric.mod.json`: `maihatsuu.margin.Margin` (main) and `maihatsuu.margin.client.MarginClient` (client).

**Mixins.** Each source set has its own config, and new mixins must be listed in the one matching their source set:

| Source set | Config | Package | JSON array |
| --- | --- | --- | --- |
| `main` | `src/main/resources/margin.mixins.json` | `maihatsuu.margin.mixin` | `mixins` |
| `client` | `src/client/resources/margin.client.mixins.json` | `maihatsuu.margin.client.mixin` | `client` |

Both configs set `injectors.defaultRequire: 1`, so an injector that matches no target fails at runtime rather than being silently skipped. `build` does not catch this; a mixin change needs `runClient` or `runServer` to verify.

**Mappings.** The project uses official Mojang mappings (`loom.officialMojangMappings()`), so vanilla names are Mojang's (`ResourceLocation`, `Minecraft`), not Yarn's (`Identifier`, `MinecraftClient`). Fabric docs and examples written against Yarn need translating.

**Helpers.** Use `Margin.id(path)` for namespaced `ResourceLocation`s, and `Margin.LOGGER` for logging.
