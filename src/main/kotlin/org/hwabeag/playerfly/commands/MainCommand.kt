package org.hwabeag.playerfly.commands

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.hwabeag.playerfly.PlayerFlyPlugin
import org.hwabeag.playerfly.inventory.FlySettingGUI

class MainCommand(private val plugin: PlayerFlyPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            return handlePlayer(sender, args)
        }
        return handleConsole(sender, args)
    }

    private fun handlePlayer(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (player.isOp) {
                sendHelp(player)
            }
            if (plugin.isBlockedWorld(player.world.name)) {
                player.sendMessage("${plugin.prefix()} 이 월드에서는 플라이를 사용할 수 없습니다.")
                return true
            }
            FlySettingGUI(plugin).open(player)
            return true
        }

        if (!player.isOp) {
            player.sendMessage("${plugin.prefix()} 권한이 없습니다.")
            return true
        }

        return when (args[0].lowercase()) {
            "정보" -> info(player, args)
            "시간지급" -> changeMinutes(player, args, +1)
            "시간차감" -> changeMinutes(player, args, -1)
            "시간설정" -> setMinutes(player, args)
            "무제한" -> toggleInfinity(player)
            "리로드" -> reload(player)
            "지급", "차감", "설정" -> {
                player.sendMessage("${plugin.prefix()} /플라이관리 ${args[0]} 은(는) 비활성화되었습니다.")
                player.sendMessage("${plugin.prefix()} 시간 조작은 /플라이관리 시간지급|시간차감|시간설정을 사용하세요.")
                true
            }
            else -> {
                sendHelp(player)
                true
            }
        }
    }

    private fun handleConsole(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("${plugin.prefix()} /플라이관리 [시간지급|시간차감|시간설정] <플레이어> <분>")
            return true
        }
        return when (args[0].lowercase()) {
            "시간지급" -> changeMinutes(sender, args, +1)
            "시간차감" -> changeMinutes(sender, args, -1)
            "시간설정" -> setMinutes(sender, args)
            "무제한" -> toggleInfinity(sender)
            "리로드" -> reload(sender)
            "지급", "차감", "설정" -> {
                sender.sendMessage("${plugin.prefix()} /플라이관리 ${args[0]} 은(는) 비활성화되었습니다.")
                sender.sendMessage("${plugin.prefix()} 시간 조작은 /플라이관리 시간지급|시간차감|시간설정을 사용하세요.")
                true
            }
            else -> {
                sender.sendMessage("${plugin.prefix()} /플라이관리 [시간지급|시간차감|시간설정] <플레이어> <분>")
                true
            }
        }
    }

    private fun info(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${plugin.prefix()} /플라이관리 정보 <플레이어>")
            return true
        }
        val target = findOnlinePlayer(args[1]) ?: run {
            sender.sendMessage("${plugin.prefix()} 해당 플레이어가 접속 중이 아닙니다.")
            return true
        }
        val data = plugin.databaseManager.getData(target.uniqueId, target.name)
        sender.sendMessage("${plugin.prefix()} ${target.name}의 남은 시간: ${data.minutes}분")
        sender.sendMessage("${plugin.prefix()} 현재 상태: ${if (data.enabled) "사용 중" else "중지"}")
        return true
    }

    private fun changeMinutes(sender: CommandSender, args: Array<out String>, direction: Int): Boolean {
        if (args.size < 3) {
            sender.sendMessage("${plugin.prefix()} /플라이관리 ${if (direction > 0) "시간지급" else "시간차감"} <플레이어> <분>")
            return true
        }
        val target = findOnlinePlayer(args[1]) ?: run {
            sender.sendMessage("${plugin.prefix()} 해당 플레이어가 접속 중이 아닙니다.")
            return true
        }
        val amount = args[2].toIntOrNull() ?: run {
            sender.sendMessage("${plugin.prefix()} 분은 숫자로 입력해 주세요.")
            return true
        }
        val delta = amount * direction
        val next = plugin.databaseManager.addMinutes(target.uniqueId, target.name, delta)
        val action = if (direction > 0) "지급" else "차감"
        sender.sendMessage("${plugin.prefix()} ${target.name}에게 ${amount}분 $action 완료. 현재 ${next}분")
        target.sendMessage("${plugin.prefix()} 관리자가 플라이 시간을 ${if (direction > 0) "지급" else "차감"}했습니다. 현재 ${next}분")
        return true
    }

    private fun setMinutes(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.size < 3) {
            sender.sendMessage("${plugin.prefix()} /플라이관리 시간설정 <플레이어> <분>")
            return true
        }
        val target = findOnlinePlayer(args[1]) ?: run {
            sender.sendMessage("${plugin.prefix()} 해당 플레이어가 접속 중이 아닙니다.")
            return true
        }
        val amount = args[2].toIntOrNull() ?: run {
            sender.sendMessage("${plugin.prefix()} 분은 숫자로 입력해 주세요.")
            return true
        }
        plugin.databaseManager.setMinutes(target.uniqueId, target.name, amount)
        sender.sendMessage("${plugin.prefix()} ${target.name}의 플라이 시간을 ${amount}분으로 설정했습니다.")
        target.sendMessage("${plugin.prefix()} 관리자가 플라이 시간을 ${amount}분으로 설정했습니다.")
        return true
    }

    private fun toggleInfinity(sender: CommandSender): Boolean {
        val current = plugin.config.getBoolean("playerfly.fly-infinity")
        plugin.config.set("playerfly.fly-infinity", !current)
        plugin.saveConfig()
        sender.sendMessage("${plugin.prefix()} 무제한 플라이가 ${if (!current) "활성화" else "비활성화"}되었습니다.")
        return true
    }

    private fun reload(sender: CommandSender): Boolean {
        plugin.reloadConfig()
        plugin.databaseManager.reload()
        for (target in Bukkit.getOnlinePlayers()) {
            if (target.gameMode != GameMode.CREATIVE && target.gameMode != GameMode.SPECTATOR && plugin.isManagedFlight(target.uniqueId)) {
                target.allowFlight = false
                target.isFlying = false
                plugin.databaseManager.setEnabled(target.uniqueId, target.name, false)
                plugin.unmarkManagedFlight(target.uniqueId)
            }
        }
        sender.sendMessage("${plugin.prefix()} 설정을 리로드했습니다.")
        return true
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${plugin.prefix()} /플라이관리 정보 <플레이어>")
        sender.sendMessage("${plugin.prefix()} /플라이관리 시간지급 <플레이어> <분>")
        sender.sendMessage("${plugin.prefix()} /플라이관리 시간차감 <플레이어> <분>")
        sender.sendMessage("${plugin.prefix()} /플라이관리 시간설정 <플레이어> <분>")
        sender.sendMessage("${plugin.prefix()} /플라이관리 무제한")
        sender.sendMessage("${plugin.prefix()} /플라이관리 리로드")
    }

    private fun findOnlinePlayer(input: String): Player? {
        return Bukkit.getOnlinePlayers().firstOrNull { it.name.equals(input, ignoreCase = true) }
            ?: Bukkit.getPlayer(input)
    }
}
