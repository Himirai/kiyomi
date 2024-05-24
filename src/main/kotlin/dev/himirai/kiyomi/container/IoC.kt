package dev.himirai.kiyomi.container

import com.google.common.reflect.ClassPath
import dev.himirai.kiyomi.KIYOMI
import dev.himirai.kiyomi.annotation.Component
import dev.himirai.kiyomi.annotation.Configuration
import dev.himirai.kiyomi.annotation.Service
import dev.himirai.kiyomi.container.bean.handler.BeanRegisterHandler
import dev.himirai.kiyomi.container.implementation.BasicContainer
import org.bukkit.plugin.java.JavaPlugin

object IoC : BasicContainer() {

	private var services = 0
	private var components = 0
	private var configurations = 0

	fun <P : JavaPlugin> scan(plugin: P) {
		register(plugin)
		val pluginClass = plugin::class.java
		val classLoader = pluginClass.classLoader
		val classPath = ClassPath.from(classLoader)
		val classes = classPath.getTopLevelClassesRecursive(pluginClass.packageName)
			.map {
				return@map try {
					it.load()
				} catch (ignored: Exception) {
					null
				}
			}
			.filterNotNull()
			.toMutableList()
		classes.filter {
			it.isAnnotationPresent(Service::class.java) || it.isAnnotationPresent(Component::class.java) || it.isAnnotationPresent(
				Configuration::class.java
			)
		}
			.sortedWith(compareBy(
				{ it.isAnnotationPresent(Configuration::class.java) },
				{ it.isInstance(BeanRegisterHandler::class.java) }
			)).reversed()
			.forEach { register(it) }
		services = classes.filter { it.isAnnotationPresent(Service::class.java) }.size
		components = classes.filter { it.isAnnotationPresent(Component::class.java) }.size
		configurations = classes.filter { it.isAnnotationPresent(Configuration::class.java) }.size
		val logger = KIYOMI.logger
		logger.info("Initialized $components component(s)")
		logger.info("Initialized $services service(s)")
		logger.info("Initialized $configurations configuration(s)")
		logger.info("Totally ${beans.size} bean(s)")
	}

}
