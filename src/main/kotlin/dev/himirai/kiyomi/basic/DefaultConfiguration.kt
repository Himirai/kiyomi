package dev.himirai.kiyomi.basic

import dev.himirai.kiyomi.annotation.Bean
import dev.himirai.kiyomi.annotation.Configuration
import io.papermc.paper.datapack.DatapackManager
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.potion.PotionBrewer
import org.bukkit.scheduler.BukkitScheduler
import java.util.logging.Logger

@Configuration
class DefaultConfiguration {

	@Bean
	fun scheduler(): BukkitScheduler = Bukkit.getScheduler()

	@Bean
	fun asyncScheduler(): AsyncScheduler = Bukkit.getAsyncScheduler()

	@Bean
	fun logger(): Logger = Bukkit.getServer().logger

	@Bean
	fun pluginManager(): PluginManager = Bukkit.getPluginManager()

	@Bean
	fun server(): Server = Bukkit.getServer()

	@Bean
	fun commandMap(): CommandMap = Bukkit.getCommandMap()

	@Bean
	fun plugins(): Array<Plugin> = Bukkit.getPluginManager().plugins

	@Bean
	fun datapackManager(): DatapackManager = Bukkit.getDatapackManager()

	@Bean
	fun potionBrewer(): PotionBrewer = Bukkit.getPotionBrewer()

}
