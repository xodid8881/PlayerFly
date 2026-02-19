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
        val data = plugin.databaseManager.getData(player.uniqueId, player.name)

        // 세션 시작 시 플라이 활성 상태는 초기화하되, 타 플러그인의 비행 권한은 건드리지 않는다.
        if (data.enabled) {
            plugin.databaseManager.setEnabled(player.uniqueId, player.name, false)
        }

        if (player.gameMode != GameMode.CREATIVE && player.gameMode != GameMode.SPECTATOR && player.isFlying) {
            player.isFlying = false
        }
        plugin.unmarkManagedFlight(player.uniqueId)
    }
}
