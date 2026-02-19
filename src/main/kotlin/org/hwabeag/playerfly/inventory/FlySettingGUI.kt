package org.hwabeag.playerfly.inventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.hwabeag.playerfly.PlayerFlyPlugin

class FlySettingGUI(private val plugin: PlayerFlyPlugin) {
    companion object {
        const val TITLE = "플라이 설정"
    }

    fun open(player: Player) {
        val inv: Inventory = Bukkit.createInventory(null, 9, TITLE)
        inv.setItem(4, createHead(player))
        player.openInventory(inv)
    }

    private fun createHead(player: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as? SkullMeta ?: return item
        val data = plugin.databaseManager.getData(player.uniqueId, player.name)

        meta.owningPlayer = player
        meta.setDisplayName(
            if (data.enabled) {
                plugin.color("&a&l${player.name} &7님의 플라이: &a사용 중")
            } else {
                plugin.color("&a&l${player.name} &7님의 플라이: &c중지 중")
            }
        )

        val firstLine = if (plugin.config.getBoolean("playerfly.fly-infinity")) {
            plugin.color("&7&l- &a&l무제한 사용 가능")
        } else {
            plugin.color("&7&l- &a&l${data.minutes} &7분 사용 가능")
        }

        meta.lore = listOf(
            firstLine,
            plugin.color("&7&l- &a&l클릭 시 플라이를 켜거나 끌 수 있습니다.")
        )
        item.itemMeta = meta
        return item
    }
}
