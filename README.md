![LCon](https://socialify.git.ci/VincentZyu233/lcon/image?description=1&font=JetBrains+Mono&forks=1&issues=1&language=1&name=1&owner=1&pulls=1&stargazers=1&theme=Light)

# LCon — WebSocket remote control for Minecraft client

> A Forge mod that runs a WebSocket server on the Minecraft **client** (single-player / LAN), allowing external applications to execute commands and interact with the game in real time.

[![Forge 1.20.1](https://img.shields.io/badge/Forge-1.20.1-FF6600?style=for-the-badge)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/temurin/releases/?version=17)
[![Gradle 8.1.1](https://img.shields.io/badge/Gradle-8.1.1-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org)

[![Last Commit](https://img.shields.io/github/last-commit/VincentZyu233/lcon?style=for-the-badge&label=Last%20Commit&color=blue)](https://github.com/VincentZyu233/lcon/commits/master)
[![CI Status](https://img.shields.io/github/actions/workflow/status/VincentZyu233/lcon/build.yml?style=for-the-badge&logo=githubactions&logoColor=white&label=CI%20Status&labelColor=2088FF)](https://github.com/VincentZyu233/lcon/actions)

## 🧩 What it does

LCon starts a **WebSocket server** inside your Minecraft client when you're in a world (single-player or LAN). You can connect to it from any WebSocket client — a Python script, a chatbot, a web dashboard — and:

| Prefix | Action |
|--------|--------|
| `[chat]<message>` | Send a chat message as the player |
| `[message]<message>` | Display a message to the player only |
| `[system]<message>` | Display a system message in chat |
| `[client]/<command>` | Execute a **client-side** command |
| `[server]/<command>` | Execute a **server-side** command |

No Mixin, no coremod, no overwrites — purely event-driven, safe for any modpack.

## 🔌 How to connect

```bash
# Example: connect with Python websocket-client
python -c "
import websocket
ws = websocket.create_connection('ws://localhost:8115')
print(ws.recv())     # welcome message
ws.send('[server]/tellraw @a awa!')
print(ws.recv())     # response
ws.close()
"
```

Or use any WebSocket client ([wscat](https://github.com/websockets/wscat), [koishi-plugin-ws-client](https://koishi.chat), browser DevTools, etc.).

## ⚙️ Configuration

File: `.minecraft/config/lcon-client.toml`

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `enable_mod` | boolean | `true` | Enable the WebSocket server |
| `port` | int | `8115` | WebSocket server port |
| `token` | string | `""` | Auth token. Clients pass `?token=xxx` on connect. Empty = disabled |

## 🏗 Build

### Local

```bash
./gradlew build
```

Output: `build/libs/lcon-*.jar`

### GitHub Actions CI

Push to any branch with specific keywords in the commit message:

| Commit message contains | What happens |
|------------------------|-------------|
| `build action` | Build + upload artifact |
| `build release` | Build + create GitHub Release |

```bash
git commit -m "fix: something; build action"
git commit -m "feat: something; build release"
```

## 📦 Tech Stack

| Dependency | Version | Description |
|:---|---:|:---|
| [![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/temurin/releases/?version=17) | 17 | Runtime |
| [![Forge](https://img.shields.io/badge/Forge-1.20.1--47.2.19-FF6600?style=flat-square)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html) | 47.2.19 | Mod loader |
| [![Gradle](https://img.shields.io/badge/Gradle-8.1.1-02303A?style=flat-square&logo=gradle&logoColor=white)](https://gradle.org) | 8.1.1 | Build tool |
| [![Shadow](https://img.shields.io/badge/Shadow-8.1.1-000000?style=flat-square)](https://imperceptiblethoughts.com/shadow/) | 7.1.0 | Fat-jar plugin |
| [![Java-WebSocket](https://img.shields.io/badge/Java--WebSocket-1.5.6-000000?style=flat-square)](https://github.com/TooTallNate/Java-WebSocket) | 1.5.6 | WebSocket server (fat-jarred) |
| [![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)](https://github.com/VincentZyu233/lcon/actions) | — | CI/CD |
