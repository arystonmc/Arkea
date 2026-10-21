# Arkea Code Map: Build Files

Part of the [Arkea code map](../CODEMAP.md).

## Build Files

| File | Purpose |
|---|---|
| `build.gradle` | ModDevGradle setup, JCodec embedded with Jar-in-Jar for video import, JUnit 5 for `src/test/java` with the Minecraft classpath of `main`, `client` run forced to Vulkan, metadata expansion, logo packing. Maven publication `com.aryston.arkea:arkea-neoforge-<minecraft_version>` with the sources jar, published to `build/repo` and, when `GITHUB_TOKEN` is set, to GitHub Packages. |
| `gradle.properties` | Single place for versions and mod metadata. |
| `settings.gradle` | Plugin repositories, Java toolchain resolver, project name. |
| `.github/workflows/build.yml` | CI on every push and pull request: JDK 25, `./gradlew build`, uploads the jar. |
| `.github/workflows/release.yml` | On a `v*` tag: checks the tag against `mod_version`, merges the `maven` branch into `build/repo`, builds and publishes, pushes the new files to the `maven` branch, creates the GitHub release with the changelog section, and uploads to Modrinth and CurseForge when their secrets exist. |
