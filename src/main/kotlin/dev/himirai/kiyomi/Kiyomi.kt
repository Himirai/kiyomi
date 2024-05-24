package dev.himirai.kiyomi

import dev.himirai.kiyomi.container.IoC
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

lateinit var KIYOMI: Kiyomi

open class Kiyomi : JavaPlugin(), Listener {

	override fun onEnable() {
		KIYOMI = this
		IoC.scan(this)
	}

	override fun onDisable() {
		IoC.close()
	}

}
