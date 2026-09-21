# CatPay v1.0.2 — Release & Patch Notes

CatPay is a client-side payment and economy message simulator for **Minecraft Java 1.21.11** (*Mounts of Mayhem*) built for the **Fabric** mod loader.

## What's New in v1.0.2

### Branding & Visual Assets
- **New Official Mod Icon**:
  - Incorporated the flying cash with wings icon ([`images (5).jpeg`](file:///sdcard/Download/images%20(5).jpeg)) cropped and rendered as a high-resolution 512x512 PNG at `assets/catpay/icon.png`.
  - Displayed natively in ModMenu and the Fabric mod list.

### Fixes from v1.0.1 (Included)
- **Resolved `StackOverflowError` on Command Forwarding**:
  - Replaced `handler.sendChatCommand(commandLine)` with direct `handler.sendPacket(new CommandExecutionC2SPacket(commandLine))` to prevent Fabric API's client command dispatcher from re-intercepting forwarded commands.
  - Added an `AtomicBoolean` (`IS_FORWARDING`) re-entrancy guard to guarantee recursion is impossible.
- **Authentic BananaSMP Colors & Formatting**:
  - Currency amount rendered in authentic bright green/mint (`&a`).
  - Transaction description rendered in gray (`&7`).
  - Target player name rendered in white (`&f`).

### Compatibility
- **Minecraft Version**: `1.21.11`
- **Fabric Loader**: `0.19.4+`
- **Fabric API**: `0.141.6+1.21.11`
- **Cloth Config**: `15.0.140+`
- **Environment**: Client-side only (`client`)
