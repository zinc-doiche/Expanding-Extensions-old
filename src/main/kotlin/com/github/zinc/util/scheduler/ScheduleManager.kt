package com.github.zinc.util.scheduler

import com.github.zinc.plugin
import org.bukkit.scheduler.BukkitScheduler

private val scheduler: BukkitScheduler = plugin.server.scheduler

internal fun sync(delay: Long = 0L, block: () -> Unit) { scheduler.runTaskLater(plugin, block, delay) }
internal fun loop(delay: Long, period: Long, block: () -> Unit) { scheduler.runTaskTimer(plugin, block, delay, period) }
internal fun async(delay: Long = 0L, block: () -> Unit) { scheduler.runTaskLaterAsynchronously(plugin, block, delay) }
internal fun asyncLoop(delay: Long, period: Long, block: () -> Unit) { scheduler.runTaskTimerAsynchronously(plugin, block, delay, period) }

