<p align="center">
  <img src="branding/logo.png" alt="Arkea logo" width="160">
</p>

<h1 align="center">Arkea</h1>

<p align="center">A client-side interface library for Minecraft mods with animated, consistent settings screens.</p>

## Requirements

| | |
|---|---|
| Minecraft | 26.3 |
| Loader | NeoForge 26.3.0.51-beta or newer |
| Java | 25 |
| Side | Client only |

## Features

See [docs/FEATURES.md](docs/FEATURES.md).

## For Mod Developers

Arkea is a library. Mods that use the NeoForge config get an Arkea settings window without any code, and mods can design their own windows or screens with the Arkea components. See [docs/API.md](docs/API.md).

```groovy
repositories {
    maven { url = uri("https://raw.githubusercontent.com/arystonmc/Arkea/maven") }
}

dependencies {
    implementation "com.aryston.arkea:arkea-neoforge-26.3:0.1"
}
```

## Changelog

See [docs/CHANGELOG.md](docs/CHANGELOG.md).

## Building

```
./gradlew build
```

The jar is written to `build/libs/`. `./gradlew publish` also writes the Maven files to `build/repo/`.

## Releasing

Push a tag `v<mod_version>` (for example `v0.1`). The Release workflow checks that the tag matches `mod_version`, builds the jar, adds the version to the `maven` branch (the Maven repository mod developers use) and to GitHub Packages, and creates a GitHub release with the jar, the sources jar and the changelog of that version (or of Unreleased).

Modrinth and CurseForge uploads run only when the repository has the secrets `MODRINTH_TOKEN` or `CURSEFORGE_TOKEN` and the variables `MODRINTH_ID` or `CURSEFORGE_ID`.

## License

[Aryston Source-Available License](LICENSE.md)
