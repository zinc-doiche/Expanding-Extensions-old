package com.github.zinc.util.domain

interface Container<K, V> {
    operator fun get(k: K): V?
    fun add(k: K, v: V)
    fun remove(k: K)
}