package org.hwabeag.playerfly

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.hwabeag.playerfly.commands.ItemCommand
import org.hwabeag.playerfly.commands.MainCommand
import org.hwabeag.playerfly.database.DatabaseManager
import org.hwabeag.playerfly.events.BookClickEvent
import org.hwabeag.playerfly.events.InvClickEvent
import org.hwabeag.playerfly.events.JoinEvent
import org.hwabeag.playerfly.expansions.PlayerFlyExpansion
import org.hwabeag.playerfly.schedules.FlyCheckTask
import org.hwabeag.playerfly.schedules.PlayerFlyTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerFlyPlugin : JavaPlugin() {
    lateinit var databaseManager: DatabaseManager
        private set
    private val managedFlightPlayers: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    override fun onEnable() {
        saveDefaultConfig()
        config.options().copyDefaults(true)
        saveConfig()

        databaseManager = DatabaseManager(this)
        databaseManager.initialize()

        server.pluginManager.registerEvents(BookClickEvent(this), this)
        server.pluginManager.registerEvents(JoinEvent(this), this)
        server.pluginManager.registerEvents(InvClickEvent(this), this)

        getCommand("playerfly")?.setExecutor(MainCommand(this))
        getCommand("flyticket")?.setExecutor(ItemCommand(this))

        PlayerFlyTask(this).runTaskTimer(this, 1200L, 1200L)
        FlyCheckTask(this).runTaskTimer(this, 100L, 100L)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlayerFlyExpansion(this, databaseManager).register()
        }

        logger.info("[PlayerFly] Enable")
    }

    override fun onDisable() {
        databaseManager.close()
        logger.info("[PlayerFly] Disable")
    }

    fun prefix(): String {
        return color(config.getString("playerfly.prefix") ?: "&a&l[플라이]&7")
    }

    fun color(text: String): String {
        return ChatColor.translateAlternateColorCodes('&', text)
    }

    fun isBlockedWorld(worldName: String): Boolean {
        return config.getConfigurationSection("playerfly.noworld")
            ?.getKeys(false)
            ?.contains(worldName) == true
    }

    fun markManagedFlight(uuid: UUID) {
        managedFlightPlayers.add(uuid)
    }

    fun unmarkManagedFlight(uuid: UUID) {
        managedFlightPlayers.remove(uuid)
    }

    fun isManagedFlight(uuid: UUID): Boolean {
        return managedFlightPlayers.contains(uuid)
    }
}
