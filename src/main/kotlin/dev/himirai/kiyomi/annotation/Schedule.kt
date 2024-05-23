package dev.himirai.kiyomi.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Schedule(val initialDelay: Int = 0, val fixedRate: Int = -1, val async: Boolean = false)
