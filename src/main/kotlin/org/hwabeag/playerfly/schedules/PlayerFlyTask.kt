package org.hwabeag.playerfly.schedules

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.hwabeag.playerfly.PlayerFlyPlugin

class PlayerFlyTask(private val plugin: PlayerFlyPlugin) : BukkitRunnable() {
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val data = plugin.databaseManager.getData(player.uniqueId, player.name)
            if (!data.enabled) {
                continue
            }

            if (plugin.config.getBoolean("playerfly.fly-infinity")) {
                player.sendActionBar("${plugin.prefix()} 무제한 플라이 사용 중입니다.")
                continue
            }

            if (data.minutes > 0) {
                val next = data.minutes - 1
                plugin.databaseManager.setMinutes(player.uniqueId, player.name, next)
                player.sendActionBar("${plugin.prefix()} 남은 플라이 시간: ${next}분")
                continue
            }

            plugin.databaseManager.setMinutes(player.uniqueId, player.name, 0)
            plugin.databaseManager.setEnabled(player.uniqueId, player.name, false)
            player.isFlying = false
            player.allowFlight = false
            player.sendTitle(plugin.color("&a&l[플라이]"), plugin.color("&a&l- 플라이 시간이 모두 소진되었습니다."))
        }
    }
}
