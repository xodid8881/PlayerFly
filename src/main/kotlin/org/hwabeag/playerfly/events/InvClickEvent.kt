package org.hwabeag.playerfly.events

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.hwabeag.playerfly.PlayerFlyPlugin
import org.hwabeag.playerfly.inventory.FlySettingGUI

class InvClickEvent(private val plugin: PlayerFlyPlugin) : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null || event.currentItem == null) {
            return
        }
        if (event.view.title != FlySettingGUI.TITLE) {
            return
        }
        if (event.currentItem?.type != Material.PLAYER_HEAD) {
            return
        }

        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val data = plugin.databaseManager.getData(player.uniqueId, player.name)

        if (!data.enabled) {
            if (!plugin.config.getBoolean("playerfly.fly-infinity") && data.minutes <= 0) {
                player.sendMessage("${plugin.prefix()} 보유한 플라이 시간이 없습니다.")
                return
            }

            if (player.gameMode == GameMode.SURVIVAL && !player.allowFlight) {
                player.allowFlight = true
            }
            plugin.databaseManager.setEnabled(player.uniqueId, player.name, true)
            plugin.markManagedFlight(player.uniqueId)
            player.closeInventory()
            player.sendMessage("${plugin.prefix()} 플라이를 활성화했습니다.")
            return
        }

        if (player.gameMode == GameMode.SURVIVAL && player.allowFlight && plugin.isManagedFlight(player.uniqueId)) {
            player.allowFlight = false
        }
        player.isFlying = false
        plugin.databaseManager.setEnabled(player.uniqueId, player.name, false)
        plugin.unmarkManagedFlight(player.uniqueId)
        player.closeInventory()
        player.sendMessage("${plugin.prefix()} 플라이를 비활성화했습니다.")
    }
}
