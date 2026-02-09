package org.hwabeag.playerfly.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.hwabeag.playerfly.PlayerFlyPlugin

class ItemCommand(private val plugin: PlayerFlyPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("${plugin.prefix()} 권한이 없습니다.")
            return true
        }

        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }

        return when (args[0].lowercase()) {
            "불러오기" -> {
                if (sender !is Player) {
                    sender.sendMessage("${plugin.prefix()} 콘솔에서는 사용할 수 없습니다.")
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage("${plugin.prefix()} /플라이아이템 불러오기 <분>")
                    return true
                }
                val minutes = args[1].toIntOrNull() ?: run {
                    sender.sendMessage("${plugin.prefix()} 분은 숫자로 입력해 주세요.")
                    return true
                }
                sender.inventory.addItem(createItem(minutes))
                sender.sendMessage("${plugin.prefix()} ${minutes}분 아이템을 지급했습니다.")
                true
            }
            "지급" -> {
                if (args.size < 3) {
                    sender.sendMessage("${plugin.prefix()} /플라이아이템 지급 <플레이어> <분>")
                    return true
                }
                val target = Bukkit.getPlayerExact(args[1]) ?: run {
                    sender.sendMessage("${plugin.prefix()} 해당 플레이어가 접속 중이 아닙니다.")
                    return true
                }
                val minutes = args[2].toIntOrNull() ?: run {
                    sender.sendMessage("${plugin.prefix()} 분은 숫자로 입력해 주세요.")
                    return true
                }
                target.inventory.addItem(createItem(minutes))
                sender.sendMessage("${plugin.prefix()} ${target.name}님에게 ${minutes}분 아이템을 지급했습니다.")
                target.sendMessage("${plugin.prefix()} 플라이 ${minutes}분 아이템을 받았습니다.")
                true
            }
            else -> {
                sendHelp(sender)
                true
            }
        }
    }

    private fun createItem(minutes: Int): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta ?: return item
        meta.setDisplayName(plugin.color("&a&l[플라이아이템]"))
        meta.lore = listOf(
            plugin.color("&f&l- 사용하면 ${minutes}분 추가"),
            plugin.color("&f&l- 우클릭 시 사용됩니다.")
        )
        item.itemMeta = meta
        return item
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${plugin.prefix()} /플라이아이템 불러오기 <분>")
        sender.sendMessage("${plugin.prefix()} /플라이아이템 지급 <플레이어> <분>")
    }
}
