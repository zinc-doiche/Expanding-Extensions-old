package com.github.zinc.util

import org.bukkit.event.Listener
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ChainEventCall(vararg val calls: KClass<*>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PassedBy(val listener: KClass<*>, val triggered: KClass<*>)
