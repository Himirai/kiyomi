package dev.himirai.kiyomi.container

interface Container {

	fun <T : Any> register(instance: T)
	fun <T : Any> register(clazz: Class<T>): T
	fun <T : Any> get(clazz: Class<T>): T?
	fun containsBean(clazz: Class<*>): Boolean
	fun close()

}
