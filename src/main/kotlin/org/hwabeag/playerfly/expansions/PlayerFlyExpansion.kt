package org.hwabeag.playerfly.expansions

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.hwabeag.playerfly.PlayerFlyPlugin
import org.hwabeag.playerfly.database.DatabaseManager

class PlayerFlyExpansion(
    private val plugin: PlayerFlyPlugin,
    private val databaseManager: DatabaseManager
) : PlaceholderExpansion() {
    override fun persist(): Boolean = true

    override fun canRegister(): Boolean = true

    override fun getAuthor(): String = plugin.description.authors.joinToString(",")

    override fun getIdentifier(): String = "playerfly"

    override fun getVersion(): String = plugin.description.version

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null || !params.equals("get", ignoreCase = true)) {
            return null
        }
        val name = player.name ?: return "0"
        return databaseManager.getData(player.uniqueId, name).minutes.toString()
    }
}
