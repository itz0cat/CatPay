# CatPay v1.0.0 — Release & Patch Notes

CatPay is a client-side payment and economy message simulator for **Minecraft Java 1.21.11** (*Mounts of Mayhem*) built for the **Fabric** mod loader.

## What's New in v1.0.0

### Key Features & Updates
- **Client-Side Simulation Only**:
  - Simulates payment sent and payment received messages strictly on the local client.
  - Zero fake packets sent to server; zero server-side balance modification.
- **BananaSMP Unicode Currency Glyph Support (`\uE058`)**:
  - Default profile configured for **BananaSMP.net**.
  - Renders BananaSMP's exact custom font glyph (`\uE058`) in place of `{icon}` without requiring external image textures.
- **Custom Icon Boolean Toggle**:
  - Easily toggle custom glyphs on/off via Cloth Config for servers that do not use custom resource pack font glyphs.
- **Client-Side `/pay` Command**:
  - `/pay <player> <amount>` is intercepted client-side when CatPay is enabled.
  - Supports numeric inputs and shorthands (`1k`, `10k`, `1.5m`, `2b`).
  - Formats numbers with comma grouping (`500,000,000`).
  - Configurable maximum fake payment limit (default: **$1,000,000,000,000 / 1 Trillion**).
  - Player tab-completion suggestions populated from client player list.
  - When CatPay is disabled, passes transparently to the server.
- **Silent Mod Toggle Keybind**:
  - Keybinding to toggle the entire mod on/off with **zero chat or HUD feedback**.
- **Simulate Receive Keybind**:
  - Keybinding to simulate incoming payments from a configured player and amount.
- **Remote Profile Synchronization**:
  - Automatically checks and syncs profile definitions asynchronously from GitHub (`itz0cat/CatPay`).
  - Caches profiles locally with complete offline fallback.
- **Cloth Config & ModMenu**:
  - Comprehensive in-game configuration screen with categories: General, Payment, Receive, and Profiles.

### Compatibility
- **Minecraft Version**: `1.21.11`
- **Fabric Loader**: `0.19.4+`
- **Fabric API**: `0.141.6+1.21.11`
- **Cloth Config**: `15.0.140+`
- **Environment**: Client-side only (`client`)
