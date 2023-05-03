package com.github.zinc.util.domain

abstract class Container<K, V> {
    val container: HashMap<K, V> = HashMap()
    operator fun get(k: K): V? = container[k]
    fun add(k: K, v: V) { container[k] = v }
    fun remove(k: K): V? = container.remove(k)
}