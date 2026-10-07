# Arkea Code Map

Every class and source file of Arkea with its purpose. Find the right file here before opening any code. Keep this file in sync with the code in the same commit (rule D1).

## Quick Lookup

| I want to change | Go to |
|---|---|
| Mod id, logger, startup | `Arkea` |
| Mod name, version, loader versions, license, authors | `gradle.properties` |
| Mod list metadata and dependencies | `neoforge.mods.toml` |
| Mod logo | `branding/logo.png` |
| Build setup, run configurations | `build.gradle` |

## Overview

- Side: client only (`dist = Dist.CLIENT`). `displayTest="IGNORE_ALL_VERSION"` lets players join servers without Arkea.
- Root package: `com.aryston.arkea`.

## Classes

### `com.aryston.arkea`

#### Arkea
- Path: `src/main/java/com/aryston/arkea/Arkea.java`
- Role: Entry point annotated with `@Mod`.
- Members:
  - `MOD_ID`: the mod id.
  - `LOGGER`: shared logger for the whole mod.
- Depends on: nothing inside the mod.

## Source Files

| File | Purpose |
|---|---|
| `src/main/templates/META-INF/neoforge.mods.toml` | Mod metadata template filled from `gradle.properties`. |

## Build Files

| File | Purpose |
|---|---|
| `build.gradle` | ModDevGradle setup, run configurations, metadata expansion, logo packing. |
| `gradle.properties` | Single place for versions and mod metadata. |
| `settings.gradle` | Plugin repositories, Java toolchain resolver, project name. |
