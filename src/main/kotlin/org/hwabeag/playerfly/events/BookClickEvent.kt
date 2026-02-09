package org.hwabeag.playerfly.events

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.hwabeag.playerfly.PlayerFlyPlugin

class BookClickEvent(private val plugin: PlayerFlyPlugin) : Listener {
    private val minuteRegex = Regex("(\\d+)")

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val player = event.player
        val item = player.inventory.itemInMainHand
        if (item.type != Material.BOOK) {
            return
        }

        val meta = item.itemMeta ?: return
        if (meta.displayName != plugin.color("&a&l[플라이아이템]")) {
            return
        }

        val firstLore = meta.lore?.firstOrNull() ?: return
        val minutes = minuteRegex.find(firstLore)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: return

        val next = plugin.databaseManager.addMinutes(player.uniqueId, player.name, minutes)
        player.sendMessage("${plugin.prefix()} 플라이 아이템을 사용해 ${minutes}분을 추가했습니다.")
        player.sendMessage("${plugin.prefix()} 현재 남은 시간: ${next}분")

        item.amount = item.amount - 1
        event.isCancelled = true
    }
}
