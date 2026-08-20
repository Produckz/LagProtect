<a href="https://modrinth.com/mod/fabric-api"><img src="https://i.imgur.com/Ol1Tcf8.png" alt="Requires Fabric API" width="200"></a>

# Lag Protect

A lightweight, server-side Fabric mod that protects players from taking damage during lag spikes. When a player's connection stops responding, damage to them is cancelled until they catch back up, so nobody dies to a frozen screen.

## How it works

The server sends each player a tiny ping on a configurable interval (default every 30 ticks). If the response is overdue by more than a second and the player has also stopped sending movement packets, they are considered lagging and incoming damage is cancelled. Protection ends the moment the client catches up and always caps at 15 seconds, the same point where vanilla would disconnect them anyway. The movement check means a client that fakes lag while still playing gets no protection.

## Gamerule

`/gamerule lag_protect <ticks>` -- interval between pings (default 30, range 0-200, 0 disables the mod).

Lower = lag is detected faster. Values above ~60 will miss short spikes entirely.

## Versions

One jar per supported range, pick the one matching your server:

*   `mc1.20.5-1.21.10` -- Minecraft 1.20.5 through 1.21.10
*   `mc1.21.11` -- Minecraft 1.21.11 only
*   `mc26.1` -- Minecraft 26.1 and newer

## Notes

Protection covers damage only. A player who stays lagged past the vanilla keepalive timeout is still disconnected as normal.

## License

This mod is available under the MIT license.
