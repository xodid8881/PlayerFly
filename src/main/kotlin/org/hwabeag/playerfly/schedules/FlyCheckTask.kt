package org.hwabeag.playerfly.schedules

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitRunnable
import org.hwabeag.playerfly.PlayerFlyPlugin

class FlyCheckTask(private val plugin: PlayerFlyPlugin) : BukkitRunnable() {
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val data = plugin.databaseManager.getData(player.uniqueId, player.name)
            if (!data.enabled) {
                continue
            }

            if (!plugin.isManagedFlight(player.uniqueId)) {
                plugin.markManagedFlight(player.uniqueId)
            }

            if (plugin.isBlockedWorld(player.world.name)) {
                if (player.gameMode == GameMode.SURVIVAL && plugin.isManagedFlight(player.uniqueId)) {
                    player.allowFlight = false
                    player.isFlying = false
                }
                plugin.databaseManager.setEnabled(player.uniqueId, player.name, false)
                plugin.unmarkManagedFlight(player.uniqueId)
                player.sendMessage("${plugin.prefix()} 이 월드에서는 플라이를 사용할 수 없습니다.")
            }
        }
    }
}
