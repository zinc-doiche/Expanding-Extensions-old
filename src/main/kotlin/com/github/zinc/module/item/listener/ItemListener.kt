package com.github.zinc.module.item.listener

import com.github.zinc.module.item.`object`.OnHit
import com.github.zinc.module.item.`object`.OnHitDetection
import com.github.zinc.module.user.`object`.user
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class ItemListener: Listener {
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity

        if(entity is Player) {
            val user = entity.user ?: return
            //근접공격 피격 시
            if(damager is LivingEntity) {
                user.trinkets.values.forEach {
                    if(it is OnHitDetection) {
                        it.onCloseHitDetection(damager, entity, event)
                    }
                }
            }
            //원거리공격 피격 시
            else if(damager is AbstractArrow) {
                val shooter = damager.shooter
                if(shooter is LivingEntity) {
                    user.trinkets.values.forEach {
                        if(it is OnHitDetection) {
                            it.onLongHitDetection(shooter, entity, event)
                        }
                    }
                }
            }
        } else if(entity is LivingEntity) {
            //근접공격 타격 시
            if(damager is Player) {
                val user = damager.user ?: return
                user.trinkets.values.forEach {
                    if(it is OnHit) {
                        it.onCloseHit(damager, entity, event)
                    }
                }
            }
            //원거리공격 타격 시
            else if(damager is AbstractArrow) {
                val shooter = damager.shooter
                if(shooter is Player) {
                    val user = shooter.user ?: return
                    user.trinkets.values.forEach {
                        if(it is OnHit) {
                            it.onLongHit(shooter, entity, event)
                        }
                    }
                }
            }
        }
    }
}