package com.github.zinc.module.world.listener

import com.github.zinc.module.item.`object`.equipment.HeartChestPlate
import com.github.zinc.module.world.`object`.WorldObserver
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class WorldListener: Listener {
    @EventHandler
    fun onPlayerWorldChange(event: PlayerChangedWorldEvent) {
        val player = event.player
        val uuid = player.uniqueId.toString()

        if(player.world.environment != World.Environment.NETHER) {
            if(uuid in WorldObserver.nethers) {
                WorldObserver.nethers.remove(uuid)
            }
            return
        }

        if(!HeartChestPlate.contains(uuid)) {
            WorldObserver.nethers.add(uuid)
            HeartbeatScope().async {
                while (uuid in WorldObserver.nethers) {
                    player.damage(6.1)
                    delay(3000)
                }
            }
        }
    }
}