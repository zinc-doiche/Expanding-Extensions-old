package com.github.zinc.module.user.listener

import com.github.zinc.module.item.`object`.OnHit
import com.github.zinc.module.item.`object`.OnHitDetection
import com.github.zinc.module.item.`object`.equipment.Equipment
import com.github.zinc.module.quest.`object`.Quest
import com.github.zinc.module.quest.`object`.SimpleQuest
import com.github.zinc.module.user.`object`.user
import com.github.zinc.util.getPersistent
import com.github.zinc.util.hasPersistent
import com.github.zinc.util.isNotNull
import org.bukkit.entity.Damageable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.random.Random

class UserDamageListener: Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity

        if(entity is Player) {
            //근접공격 피격 시
            if(damager is LivingEntity) {
                onHitDetection(entity, damager, event, true)
                entity.sendMessage("${damager.name}에게 근접공격 피격: ${event.finalDamage}")
            }
            //원거리공격 피격 시
            else if(damager is Projectile) {
                val shooter = damager.shooter
                if(shooter is LivingEntity) {
                    onHitDetection(entity, shooter, event, false)
                    entity.sendMessage("${shooter.name}에게 원거리공격 피격: ${event.finalDamage}")
                }
            }
        }

        if(entity is LivingEntity) {
            //근접공격 타격 시
            if(damager is Player) {
                onHit(damager, entity, event, true)
                damager.sendMessage("${entity.name}에게 근접공격 타격: ${event.finalDamage}")
            }
            //원거리공격 타격 시
            else if(damager is Projectile) {
                val shooter = damager.shooter ?: return
                if(shooter is Player) {
                    onHit(shooter, entity, event, false)
                    shooter.sendMessage("${entity.name}에게 원거리공격 타격: ${event.finalDamage}")
                }
            }
        }
    }

    private fun onHitDetection(player: Player, damager: LivingEntity, event: EntityDamageByEntityEvent, isClose: Boolean) {
        val user = player.user ?: return
        user.trinkets.values.forEach {
            if(it is OnHitDetection) {
                if(isClose) {
                    it.onCloseHitDetection(damager, player, event)
                } else {
                    it.onLongHitDetection(damager, player, event)
                }
            }
        }
        for (armorContent in player.equipment.armorContents.filter(::isNotNull)) {
            if(armorContent.hasPersistent(Equipment.namespace)) {
                val name = armorContent.getPersistent(Equipment.namespace) ?: return
                val equipment = Equipment[name] ?: return
                if(equipment is OnHitDetection) {
                    if(isClose) {
                        equipment.onCloseHitDetection(damager, player, event)
                    } else {
                        equipment.onLongHitDetection(damager, player, event)
                    }
                }
            }
        }
    }

    private fun onHit(player: Player, entity: LivingEntity, event: EntityDamageByEntityEvent, isClose: Boolean) {
        val user = player.user ?: return
        val entityName = entity.type.name

        user.trinkets.values.forEach {
            if(it is OnHit) {
                if(isClose) {
                    it.onCloseHit(player, entity, event)
                } else {
                    it.onLongHit(player, entity, event)
                }
            }
        }
        for (armorContent in player.equipment.armorContents.filter(::isNotNull)) {
            if(armorContent.hasPersistent(Equipment.namespace)) {
                val name = armorContent.getPersistent(Equipment.namespace) ?: return
                val equipment = Equipment[name] ?: return
                if(equipment is OnHit) {
                    if(isClose) {
                        equipment.onCloseHit(player, entity, event)
                    } else {
                        equipment.onLongHit(player, entity, event)
                    }
                }
            }
        }
        if(Random.nextDouble() < user.criticalChance) {
            event.damage *= 1.8
        }
        if((entity as Damageable).health <= event.finalDamage && user.questProcesses.contains(entityName)) {
            val quest = Quest[entityName] as? SimpleQuest ?: return
            val current = user.questIncrement(entityName)
            quest.onIncrement(player, current)
            if(current == quest.requires) {
                quest.onClear(user)
                user.questProcesses.remove(entityName)
            }
        }
    }
}