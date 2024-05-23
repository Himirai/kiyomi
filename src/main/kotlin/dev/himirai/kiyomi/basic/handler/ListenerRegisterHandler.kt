package dev.himirai.kiyomi.basic.handler

import dev.himirai.kiyomi.KIYOMI
import dev.himirai.kiyomi.annotation.Component
import dev.himirai.kiyomi.container.bean.handler.BeanRegisterHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager

@Component
class ListenerRegisterHandler(
	private val pluginManager: PluginManager
) : BeanRegisterHandler {

	override fun on(component: Any) {
		if (component is Listener) pluginManager.registerEvents(component, KIYOMI)
	}

}
