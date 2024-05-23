package dev.himirai.kiyomi.basic.handler

import dev.himirai.kiyomi.KIYOMI
import dev.himirai.kiyomi.annotation.Component
import dev.himirai.kiyomi.annotation.Schedule
import dev.himirai.kiyomi.container.bean.handler.BeanRegisterHandler
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import java.lang.reflect.Method

@Component
class ScheduleRegisterHandler(
	private val scheduler: BukkitScheduler
) : BeanRegisterHandler {

	private val runningTasks = LinkedHashMap<Method, BukkitTask>()

	override fun on(component: Any) {
		val clazz = component::class.java
		val methods = hashSetOf<Method>()
		methods.addAll(clazz.declaredMethods)
		methods.filter { it.isAnnotationPresent(Schedule::class.java) }
			.forEach { method ->
				if (method.parameterCount > 1 || (method.parameterCount == 1 && method.parameters[0].type != BukkitTask::class.java))
					throw RuntimeException("@Schedule method can accept only BukkitTask or no arguments (${clazz.simpleName}.${method.name})")
				method.isAccessible = true
				val annotation = method.getAnnotation(Schedule::class.java)
				val runnable = Runnable {
					if (method.parameterCount == 1) {
						val task = runningTasks[method] ?: return@Runnable
						method.invoke(component, task)
					} else {
						method.invoke(component)
					}
				}
				val task = if (annotation.fixedRate == -1) {
					if (annotation.async) scheduler.runTaskLaterAsynchronously(
						KIYOMI,
						runnable,
						annotation.initialDelay.toLong() + 1L
					)
					else scheduler.runTaskLater(KIYOMI, runnable, annotation.initialDelay.toLong() + 1L)
				} else {
					if (annotation.async) scheduler.runTaskTimerAsynchronously(
						KIYOMI,
						runnable,
						annotation.initialDelay.toLong() + 1L,
						annotation.fixedRate.toLong()
					)
					else scheduler.runTaskTimer(
						KIYOMI,
						runnable,
						annotation.initialDelay.toLong() + 1L,
						annotation.fixedRate.toLong()
					)
				}
				runningTasks[method] = task
			}
	}

}
