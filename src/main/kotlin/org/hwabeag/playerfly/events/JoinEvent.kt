package org.hwabeag.playerfly.events

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.hwabeag.playerfly.PlayerFlyPlugin

class JoinEvent(private val plugin: PlayerFlyPlugin) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.databaseManager.getData(player.uniqueId, player.name)

        if (player.gameMode != GameMode.CREATIVE && player.gameMode != GameMode.SPECTATOR) {
            player.isFlying = false
            player.allowFlight = false
            plugin.databaseManager.setEnabled(player.uniqueId, player.name, false)
        }
    }
}
