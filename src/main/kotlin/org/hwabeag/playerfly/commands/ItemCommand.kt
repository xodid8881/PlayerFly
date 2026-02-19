package org.hwabeag.playerfly.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.hwabeag.playerfly.PlayerFlyPlugin

class ItemCommand(private val plugin: PlayerFlyPlugin) : CommandExecutor {
    private val minuteKey = NamespacedKey(plugin, "fly_ticket_minutes")

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
            "지급", "give" -> giveTicket(sender, args)
            else -> {
                sendHelp(sender)
                true
            }
        }
    }

    private fun giveTicket(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${plugin.prefix()} /플라이권 지급 <닉네임> [분]")
            return true
        }

        val target = findOnlinePlayer(args[1]) ?: run {
            sender.sendMessage("${plugin.prefix()} 해당 플레이어가 접속 중이 아닙니다.")
            return true
        }

        val defaultMinutes = plugin.config.getInt("fly-ticket.default-minutes", 30)
        val minutes = when {
            args.size >= 3 -> args[2].toIntOrNull()
            else -> defaultMinutes
        } ?: run {
            sender.sendMessage("${plugin.prefix()} 분은 숫자로 입력해 주세요.")
            return true
        }

        if (minutes <= 0) {
            sender.sendMessage("${plugin.prefix()} 분은 1 이상이어야 합니다.")
            return true
        }

        target.inventory.addItem(createItem(minutes))
        sender.sendMessage("${plugin.prefix()} ${target.name}에게 ${minutes}분 플라이권을 지급했습니다.")
        target.sendMessage("${plugin.prefix()} ${minutes}분 플라이권을 받았습니다.")
        return true
    }

    private fun createItem(minutes: Int): ItemStack {
        val configuredMaterial = plugin.config.getString("fly-ticket.material", "BOOK").orEmpty()
        val material = Material.matchMaterial(configuredMaterial.uppercase()) ?: Material.BOOK
        val item = ItemStack(material)
        val meta = item.itemMeta ?: return item

        val displayName = plugin.config.getString("fly-ticket.display-name", "&a&l[플라이권]").orEmpty()
        val loreTemplate = plugin.config.getStringList("fly-ticket.lore")
        val lore = loreTemplate.map { line ->
            plugin.color(line.replace("%minutes%", minutes.toString()))
        }

        meta.setDisplayName(plugin.color(displayName))
        meta.lore = lore
        meta.persistentDataContainer.set(minuteKey, PersistentDataType.INTEGER, minutes)
        item.itemMeta = meta
        return item
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${plugin.prefix()} /플라이권 지급 <닉네임> [분]")
    }

    private fun findOnlinePlayer(input: String): Player? {
        return Bukkit.getOnlinePlayers().firstOrNull { it.name.equals(input, ignoreCase = true) }
            ?: Bukkit.getPlayer(input)
    }
}
