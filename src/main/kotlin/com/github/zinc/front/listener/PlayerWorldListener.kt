package com.github.zinc.front.listener

import com.github.zinc.util.extension.hasPersistent
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * 1. PDC of chestplate
 * 2. equip I/O
 * 3. resist potion
 * 4. world I/O
 */
class PlayerWorldListener: Listener {

    @EventHandler
    fun onChangeWorld(e: PlayerChangedWorldEvent) {

        if (e.player.world.environment == World.Environment.NETHER) {
            if(e.player.equipment.chestplate.hasPersistent(NETHER_RESIST_KEY)) {
                e.player.removePotionEffect(fireResistance.type)
                return
            }
            e.player.apply {
                addPotionEffect(fatigue)
                fireTicks = infiniteFireTicks
            }
        } else if (e.from.environment == World.Environment.NETHER) {
            if(e.player.equipment.chestplate.hasPersistent(NETHER_RESIST_KEY)) {
                e.player.addPotionEffect(fireResistance)
                return
            }
            e.player.apply {
                removePotionEffect(fatigue.type)
                fireTicks = 0
            }
        }
    }

    //passed by PlayerInventorySlotChangeEvent
    @EventHandler
    fun onOffEquip(e: PlayerInventorySlotChangeEvent) {

    }

    companion object {
        private val fatigue = PotionEffect(PotionEffectType.SLOW_DIGGING , PotionEffect.INFINITE_DURATION, 2, false, false, false)
        private val fireResistance = PotionEffect(PotionEffectType.FIRE_RESISTANCE , PotionEffect.INFINITE_DURATION, 0, false, false, true)
        private val NETHER_RESIST_KEY = NamespacedKey.minecraft("nether_resist_key")
        private const val infiniteFireTicks = 1_000_000
    }

}