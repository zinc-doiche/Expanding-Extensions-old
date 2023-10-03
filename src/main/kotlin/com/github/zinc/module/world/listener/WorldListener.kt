package com.github.zinc.module.world.listener

import com.github.zinc.module.item.`object`.equipment.HeartChestplate
import com.github.zinc.module.world.`object`.WorldObserver
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class WorldListener: Listener {
    private val infiniteFireTicks = 1_000_000

    @EventHandler
    fun onPlayerWorldChange(event: PlayerChangedWorldEvent) {
        val player = event.player
        val uuid = player.uniqueId.toString()

        if(player.world.environment != World.Environment.NETHER) {
            player.fireTicks = 0
            if(uuid in WorldObserver.nether) {
                WorldObserver.nether.remove(uuid)
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
            }
            return
        }

        if(!HeartChestplate.isPutOn(uuid)) {
            WorldObserver.nether.add(uuid)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, PotionEffect.INFINITE_DURATION, 2, false, false, false))
            player.fireTicks = infiniteFireTicks
            HeartbeatScope().async {
                while (uuid in WorldObserver.nether) {
                    player.damage(6.1)
                    delay(3000)
                }
            }
        }
    }

    @EventHandler
    fun onMilk(event: PlayerItemConsumeEvent) {
        if(event.item.type != Material.MILK_BUCKET) {
            return
        }
        if(event.player.world.environment == World.Environment.NETHER) {
            event.isCancelled = true
        }
    }
}