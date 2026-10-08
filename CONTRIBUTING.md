# Contributing to Arkea

Thank you for your interest in contributing to Arkea.

## Building from Source

### Prerequisites
- Java 25 JDK
- NeoForge 26.3 environment

### Build Command

```bash
./gradlew build
```

The compiled mod jar is written to `build/libs/`.

To build and publish Maven artifacts locally for dependent projects:
```bash
./gradlew publish
```
The Maven repository is created at `build/repo/`.

## Running the Client in Development

```bash
./gradlew runClient
```

## Guidelines
- Read [docs/API.md](docs/API.md) to understand public APIs versus internal engine packages.
- Read [docs/CODEMAP.md](docs/CODEMAP.md) for class responsibilities. Keep this document updated whenever classes are added, moved, or deleted.
- Code style: clean, intention-revealing, no dead code, and no comments in code files.

## Pull Requests and License Agreement
All contributions submitted via Pull Requests are subject to the [Aryston Source-Available License](LICENSE.md). By submitting a pull request, you agree that your contribution may be licensed and distributed under these terms.

