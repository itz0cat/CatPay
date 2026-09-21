# CatPay v1.0.3 — Release & Patch Notes

CatPay is a client-side payment and economy message simulator for **Minecraft Java 1.21.11** (*Mounts of Mayhem*) built for the **Fabric** mod loader.

## What's New in v1.0.3

### Authentic BananaSMP MiniMessage / Hex Color Matching
- **Pixel-Perfect Color Matching from In-Game Screenshots**:
  - Analyzed the in-game chat comparison screenshot:
    - In the original server message, both the currency icon (`\uE058`) and amount (`$2,000`) share the exact same custom mint/sea-green hex color `<#55FFAA>` (`RGB(108, 212, 161)`), rather than standard Minecraft `&a` lime green.
    - Description text uses `<gray>` (`&7`).
    - Recipient name uses `<white>` (`&f`).
- **Added Full MiniMessage & Hex Color Support**:
  - `MessageRenderer` now supports:
    - MiniMessage hex tags (`<#RRGGBB>`, `<color:#RRGGBB>`)
    - Named color tags (`<green>`, `<gray>`, `<white>`, `<aqua>`, etc.)
    - Formatting tags (`<bold>`, `<italic>`, `<reset>`, etc.)
    - Spigot/Bungee hex (`&#RRGGBB`, `§#RRGGBB`)
    - Standard Minecraft formatting codes (`&a`, `§a`, etc.)
- **Updated BananaSMP Template**:
  - `sent`: `<#55FFAA>{icon} ${amount}<gray> has been sent to <white>{user}.`
  - `received`: `<#55FFAA>{icon} ${amount}<gray> has been received from <white>{user}.`
  - Both `{icon}` and `${amount}` now render in authentic mint green (`#55FFAA`).

### Compatibility
- **Minecraft Version**: `1.21.11`
- **Fabric Loader**: `0.19.4+`
- **Fabric API**: `0.141.6+1.21.11`
- **Cloth Config**: `15.0.140+`
- **Environment**: Client-side only (`client`)
