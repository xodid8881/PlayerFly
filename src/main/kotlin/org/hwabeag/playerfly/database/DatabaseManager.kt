package org.hwabeag.playerfly.database

import org.bukkit.configuration.file.FileConfiguration
import org.hwabeag.playerfly.PlayerFlyPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

data class FlyData(
    val minutes: Int,
    val enabled: Boolean
)

class DatabaseManager(private val plugin: PlayerFlyPlugin) {
    private var type: String = "sqlite"
    private var url: String = ""
    private var username: String = ""
    private var password: String = ""

    fun initialize() {
        loadFromConfig(plugin.config)
        createTableIfNeeded()
    }

    fun reload() {
        loadFromConfig(plugin.config)
        createTableIfNeeded()
    }

    fun close() {
    }

    @Synchronized
    fun getData(uuid: UUID, name: String): FlyData {
        ensurePlayer(uuid, name)
        getConnection().use { conn ->
            conn.prepareStatement(
                "SELECT fly_minutes, fly_enabled FROM player_fly WHERE uuid = ?"
            ).use { ps ->
                ps.setString(1, uuid.toString())
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        return FlyData(
                            minutes = rs.getInt("fly_minutes"),
                            enabled = rs.getBoolean("fly_enabled")
                        )
                    }
                }
            }
        }
        return FlyData(0, false)
    }

    @Synchronized
    fun addMinutes(uuid: UUID, name: String, amount: Int): Int {
        val current = getData(uuid, name)
        val next = (current.minutes + amount).coerceAtLeast(0)
        setMinutes(uuid, name, next)
        return next
    }

    @Synchronized
    fun setMinutes(uuid: UUID, name: String, minutes: Int) {
        ensurePlayer(uuid, name)
        getConnection().use { conn ->
            conn.prepareStatement(
                "UPDATE player_fly SET name = ?, fly_minutes = ? WHERE uuid = ?"
            ).use { ps ->
                ps.setString(1, name)
                ps.setInt(2, minutes.coerceAtLeast(0))
                ps.setString(3, uuid.toString())
                ps.executeUpdate()
            }
        }
    }

    @Synchronized
    fun setEnabled(uuid: UUID, name: String, enabled: Boolean) {
        ensurePlayer(uuid, name)
        getConnection().use { conn ->
            conn.prepareStatement(
                "UPDATE player_fly SET name = ?, fly_enabled = ? WHERE uuid = ?"
            ).use { ps ->
                ps.setString(1, name)
                ps.setBoolean(2, enabled)
                ps.setString(3, uuid.toString())
                ps.executeUpdate()
            }
        }
    }

    @Synchronized
    private fun ensurePlayer(uuid: UUID, name: String) {
        getConnection().use { conn ->
            if (isMysql()) {
                conn.prepareStatement(
                    """
                    INSERT INTO player_fly (uuid, name, fly_minutes, fly_enabled)
                    VALUES (?, ?, 0, false)
                    ON DUPLICATE KEY UPDATE name = VALUES(name)
                    """.trimIndent()
                ).use { ps ->
                    ps.setString(1, uuid.toString())
                    ps.setString(2, name)
                    ps.executeUpdate()
                }
            } else {
                conn.prepareStatement(
                    """
                    INSERT INTO player_fly (uuid, name, fly_minutes, fly_enabled)
                    VALUES (?, ?, 0, 0)
                    ON CONFLICT(uuid) DO UPDATE SET name = excluded.name
                    """.trimIndent()
                ).use { ps ->
                    ps.setString(1, uuid.toString())
                    ps.setString(2, name)
                    ps.executeUpdate()
                }
            }
        }
    }

    private fun createTableIfNeeded() {
        getConnection().use { conn ->
            conn.createStatement().use { st ->
                if (isMysql()) {
                    st.executeUpdate(
                        """
                        CREATE TABLE IF NOT EXISTS player_fly (
                            uuid VARCHAR(36) PRIMARY KEY,
                            name VARCHAR(16) NOT NULL,
                            fly_minutes INT NOT NULL DEFAULT 0,
                            fly_enabled BOOLEAN NOT NULL DEFAULT FALSE
                        )
                        """.trimIndent()
                    )
                } else {
                    st.executeUpdate(
                        """
                        CREATE TABLE IF NOT EXISTS player_fly (
                            uuid TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            fly_minutes INTEGER NOT NULL DEFAULT 0,
                            fly_enabled INTEGER NOT NULL DEFAULT 0
                        )
                        """.trimIndent()
                    )
                }
            }
        }
    }

    private fun loadFromConfig(config: FileConfiguration) {
        type = config.getString("database.type", "sqlite").orEmpty().lowercase()
        if (isMysql()) {
            val host = config.getString("database.mysql.host", "127.0.0.1")
            val port = config.getInt("database.mysql.port", 3306)
            val database = config.getString("database.mysql.database", "playerfly")
            val useSSL = config.getBoolean("database.mysql.useSSL", false)
            val timezone = config.getString("database.mysql.serverTimezone", "Asia/Seoul")
            username = config.getString("database.mysql.username", "root").orEmpty()
            password = config.getString("database.mysql.password", "").orEmpty()
            url = "jdbc:mysql://$host:$port/$database?useSSL=$useSSL&serverTimezone=$timezone"
        } else {
            val fileName = config.getString("database.sqlite.file", "playerfly.db").orEmpty()
            val dbFile = plugin.dataFolder.resolve(fileName)
            if (!dbFile.exists()) {
                dbFile.parentFile?.mkdirs()
            }
            username = ""
            password = ""
            url = "jdbc:sqlite:${dbFile.absolutePath}"
        }
    }

    private fun getConnection(): Connection {
        return if (isMysql()) {
            DriverManager.getConnection(url, username, password)
        } else {
            DriverManager.getConnection(url)
        }
    }

    private fun isMysql(): Boolean {
        return type == "mysql"
    }
}
