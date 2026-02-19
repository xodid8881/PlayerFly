package org.hwabeag.playerfly.events

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.hwabeag.playerfly.PlayerFlyPlugin

class BookClickEvent(private val plugin: PlayerFlyPlugin) : Listener {
    private val minuteRegex = Regex("(\\d+)")
    private val minuteKey = NamespacedKey(plugin, "fly_ticket_minutes")

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        val player = event.player
        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            return
        }

        val meta = item.itemMeta ?: return
        val expectedName = plugin.color(plugin.config.getString("fly-ticket.display-name", "&a&l[플라이권]").orEmpty())
        val displayNameMatched = meta.displayName == expectedName
        val pdcMinutes = meta.persistentDataContainer.get(minuteKey, PersistentDataType.INTEGER)
        if (pdcMinutes == null && !displayNameMatched) {
            return
        }
        val loreMinutes = meta.lore
            ?.firstOrNull()
            ?.let { line -> minuteRegex.find(line)?.groupValues?.getOrNull(1)?.toIntOrNull() }

        val minutes = pdcMinutes ?: loreMinutes ?: return
        if (minutes <= 0) {
            return
        }

        val next = plugin.databaseManager.addMinutes(player.uniqueId, player.name, minutes)
        player.sendMessage("${plugin.prefix()} 플라이권을 사용해 ${minutes}분을 추가했습니다.")
        player.sendMessage("${plugin.prefix()} 현재 남은 시간: ${next}분")

        item.amount = item.amount - 1
        event.isCancelled = true
    }
}
