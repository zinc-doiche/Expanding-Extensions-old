package com.github.zinc.module.item.`object`

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

interface Passive {
    fun on(player: Player)
    fun off(player: Player)
}

/**
 * 타격 시 판정
 */
interface OnHit: Passive {
    fun onCloseHit(player: Player, entity: LivingEntity, event: EntityDamageByEntityEvent)
    fun onLongHit(player: Player, entity: LivingEntity, event: EntityDamageByEntityEvent)
}

/**
 * 피격 시 판정
 */
interface OnHitDetection: Passive {
    fun onCloseHitDetection(damager: LivingEntity, player: Player, event: EntityDamageByEntityEvent)
    fun onLongHitDetection(damager: LivingEntity, player: Player, event: EntityDamageByEntityEvent)
}