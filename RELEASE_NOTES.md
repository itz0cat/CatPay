# CatPay v1.0.1 — Release & Patch Notes

CatPay is a client-side payment and economy message simulator for **Minecraft Java 1.21.11** (*Mounts of Mayhem*) built for the **Fabric** mod loader.

## What's New in v1.0.1

### Critical Bug Fixes
- **Resolved `StackOverflowError` on Command Forwarding**:
  - Fixed an infinite recursion crash when forwarding `/pay` to the server while CatPay is disabled.
  - Replaced `handler.sendChatCommand(commandLine)` with direct `handler.sendPacket(new CommandExecutionC2SPacket(commandLine))` to prevent Fabric API's client command dispatcher from re-intercepting forwarded commands.
  - Added an `AtomicBoolean` (`IS_FORWARDING`) re-entrancy guard to guarantee recursion is impossible.

### Visual & Formatting Improvements (BananaSMP)
- **Authentic BananaSMP Colors & Formatting**:
  - Matched in-game server screenshots:
    - `{icon} &a${amount}&7 has been sent to &f{user}.`
    - `{icon} &a${amount}&7 has been received from &f{user}.`
  - Currency amount rendered in authentic bright green/mint (`&a`).
  - Transaction description rendered in gray (`&7`).
  - Target player name rendered in white (`&f`).

### Compatibility
- **Minecraft Version**: `1.21.11`
- **Fabric Loader**: `0.19.4+`
- **Fabric API**: `0.141.6+1.21.11`
- **Cloth Config**: `15.0.140+`
- **Environment**: Client-side only (`client`)
