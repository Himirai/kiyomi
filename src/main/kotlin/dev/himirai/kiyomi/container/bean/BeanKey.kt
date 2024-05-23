package dev.himirai.kiyomi.container.bean

data class BeanKey<T : Any>(
	val clazz: Class<T>
)
