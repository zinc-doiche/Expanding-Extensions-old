package com.github.zinc.util

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ChainEventCall(vararg val calls: KClass<*>) {

}
