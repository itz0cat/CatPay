# CatPay — Fabric 1.21.11

**CatPay** is a high-performance, client-side Minecraft Fabric mod designed to simulate economy transactions locally with custom server glyphs and remote profile synchronization.

---

## Features

- **Client-Side Simulation Only**: Zero fake server packets, zero balance modification, 100% client-side.
- **BananaSMP Unicode Currency Glyph**: Pre-configured with BananaSMP's exact font glyph (`\uE058`), providing authentic server message visuals out of the box.
- **Icon Boolean Toggle**: Easily enable or disable custom glyphs for servers that don't use custom resource pack fonts.
- **Client-Side `/pay` Interception**:
  - Automatically suggests player names from client tab-list.
  - Supports amounts with shorthand notation (`1k`, `10m`, `1.5b`).
  - Formats numbers with comma grouping (`500,000,000`).
  - Configurable maximum fake payment limit (default: **$1,000,000,000,000 / 1 Trillion**).
  - Transparently yields to server `/pay` when CatPay is disabled.
- **Simulate Receive Keybind**: Keybinding to simulate incoming payments from a configured player and amount.
- **Silent Toggle Keybind**: Dedicated keybinding to toggle the entire mod on/off with **zero chat or HUD feedback**.
- **Remote Profile Sync**:
  - Asynchronously downloads and validates profiles from GitHub (`itz0cat/CatPay`).
  - Caches profiles locally with complete offline fallback support.
- **Cloth Config & ModMenu Integration**: Comprehensive in-game settings screen with clear categories.

---

## Configuration

Settings can be accessed in-game via **ModMenu** or directly edited in `.minecraft/config/catpay/config.json`:

| Option | Type | Default | Description |
|---|---|---|---|
| `enabled` | Boolean | `false` | Master switch. When OFF, `/pay` passes directly to server. |
| `enableFakePay` | Boolean | `true` | Intercepts `/pay <player> <amount>` when enabled. |
| `enableFakeReceive` | Boolean | `true` | Enables incoming payment simulation via keybind. |
| `selectedProfile` | String | `"bananasmp"` | Active profile ID (`bananasmp`, `default`). |
| `maximumFakePayment` | Long | `1000000000000` | Maximum allowed simulated transaction amount (1 Trillion). |
| `useCustomIcon` | Boolean | `true` | Toggles rendering of custom unicode currency glyph (`\uE058`). |
| `receiveUsername` | String | `"JustGhastlyyy"` | Target username for simulated incoming payment. |
| `receiveAmount` | String | `"500,000,000"` | Amount for simulated incoming payment. |
| `autoUpdateProfiles` | Boolean | `true` | Automatically checks GitHub for profile updates on launch. |
| `autoSelectProfileByServer`| Boolean | `true` | Automatically activates profile matching the connected server. |
| `gitHubRepo` | String | `"itz0cat/CatPay"` | Remote GitHub repository for profiles. |

---

## Profile Format

Profiles are stored in `.minecraft/config/catpay/profiles/<id>.json` or served from GitHub:

```json
{
  "id": "bananasmp",
  "name": "BananaSMP.net",
  "serverMatchers": [
    "bananasmp.net",
    "*.bananasmp.net"
  ],
  "icon": {
    "enabled": true,
    "type": "unicode",
    "glyph": "\uE058"
  },
  "payment": {
    "sent": "{icon} ${amount} has been sent to {user}.",
    "received": "{icon} ${amount} has been received from {user}."
  }
}
```

---

## Building from Source

```bash
./gradlew build
```

The built mod JAR will be located in `build/libs/catpay-1.0.0.jar`.
