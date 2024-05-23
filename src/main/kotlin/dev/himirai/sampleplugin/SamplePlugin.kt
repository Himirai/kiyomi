package dev.himirai.sampleplugin

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

lateinit var PLUGIN: SamplePlugin

open class SamplePlugin : JavaPlugin(), Listener {

	override fun onEnable() {
		PLUGIN = this
		Bukkit.getPluginManager().registerEvents(this, this)
	}

	@EventHandler
	fun AsyncPlayerPreLoginEvent.on() {
		logger.log(Level.INFO, "Player ${playerProfile.name} pre-logging in...")
	}

	@EventHandler
	fun PlayerJoinEvent.on() {
		joinMessage(null)
		logger.log(Level.INFO, "Player ${player.name} connected!")
	}

	@EventHandler
	fun PlayerQuitEvent.on() {
		quitMessage(null)
		logger.log(Level.INFO, "Player ${player.name} disconnected")
	}

}
