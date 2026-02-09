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
                if (player.gameMode == GameMode.SURVIVAL) {
                    player.allowFlight = false
                    player.isFlying = false
                }
                continue
            }

            if (plugin.isBlockedWorld(player.world.name)) {
                if (player.gameMode == GameMode.SURVIVAL) {
                    player.allowFlight = false
                    player.isFlying = false
                }
                plugin.databaseManager.setEnabled(player.uniqueId, player.name, false)
                player.sendMessage("${plugin.prefix()} 이 월드에서는 플라이를 사용할 수 없습니다.")
            }
        }
    }
}
