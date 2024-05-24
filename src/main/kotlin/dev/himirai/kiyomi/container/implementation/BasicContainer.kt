package dev.himirai.kiyomi.container.implementation

import dev.himirai.kiyomi.KIYOMI
import dev.himirai.kiyomi.annotation.*
import dev.himirai.kiyomi.container.Container
import dev.himirai.kiyomi.container.bean.BeanKey
import dev.himirai.kiyomi.container.bean.handler.BeanRegisterHandler
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class BasicContainer : Container {

	protected val beans = mutableMapOf<BeanKey<*>, Any>()
	private val beanRegisterHandlers = arrayListOf<BeanRegisterHandler>()

	override fun <T : Any> register(instance: T) {
		beans[BeanKey(instance::class.java)] = instance
		processComponent(instance)
	}

	private fun <T : Any> forceRegister(key: BeanKey<T>, instance: Any) {
		beans[key] = instance
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> register(clazz: Class<T>): T {
		val found = beans.entries.firstOrNull { it.key.clazz == clazz }
		if (found != null) return found.value as T
		if (clazz.isInterface || Modifier.isAbstract(clazz.modifiers))
			throw IllegalStateException("Can't make bean interface or abstract class: ${clazz.simpleName}")
		val constructor = clazz.kotlin.primaryConstructor
		val params = constructor?.parameters?.map { param ->
			val paramType = param.type.classifier as? KClass<*>
				?: throw IllegalStateException("Cannot handle constructor param type: ${param.type}")
			get(paramType.java) ?: register(paramType.java)
		}?.toTypedArray() ?: emptyArray()
		val instance = constructor?.call(*params) ?: throw IllegalStateException("Failed to create instance of $clazz")
		register(instance)
		return instance
	}

	override fun <T : Any> get(clazz: Class<T>): T? {
		return beans.values.filterIsInstance(clazz).firstOrNull()
	}

	override fun containsBean(clazz: Class<*>): Boolean {
		return beans.keys.any { it.clazz == clazz }
	}

	private fun processComponent(instance: Any) {
		val clazz = instance::class.java
		val functions = clazz.declaredMethods
		clazz.declaredFields.filter { it.isAnnotationPresent(Autowired::class.java) }
			.forEach {
				it.set(instance, register(it.type))
			}
		functions.filter { it.isAnnotationPresent(PostConstruct::class.java) }
			.forEach {
				val prevState = it.isAccessible
				it.isAccessible = true
				it.invoke(instance)
				it.isAccessible = prevState
			}
		if (clazz.isAnnotationPresent(Configuration::class.java)) {
			functions.filter { it.isAnnotationPresent(Bean::class.java) }
				.forEach {
					val result = it.invoke(instance)
					if (result == null) {
						KIYOMI.logger.severe("${clazz.simpleName}#${it.name} returned null, bean not registered")
						return@forEach
					}
					forceRegister(BeanKey(it.returnType), result)
				}
		}
		if (instance is BeanRegisterHandler) {
			beanRegisterHandlers.add(instance)
		} else if (!instance::class.java.isAnnotationPresent(Configuration::class.java)) {
			beanRegisterHandlers.forEach { it.on(instance) }
		}
	}

	override fun close() {
		for (instance in beans.values) {
			val clazz = instance::class.java
			clazz.declaredMethods.filter { it.isAnnotationPresent(PreDestroy::class.java) }
				.forEach {
					val prevState = it.isAccessible
					it.isAccessible = true
					it.invoke(instance)
					it.isAccessible = prevState
				}
		}
	}

}
