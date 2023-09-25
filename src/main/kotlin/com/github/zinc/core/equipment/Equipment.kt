package com.github.zinc.core.equipment

import com.github.zinc.container.EquipmentContainer
import com.github.zinc.core.player.PlayerData
import com.github.zinc.module.user.`object`.Status
import com.github.zinc.util.Colors
import com.github.zinc.util.text
import com.github.zinc.util.texts
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

interface Equipment {
    var item: ItemStack
    val status: Status
    var level: Int

    fun isDeserved(playerData: PlayerData): Boolean
    fun getLore(): MutableList<Component> {
        return texts(
            text(""),
            text("요구 스테이터스:").color(Colors.green)
        ).apply {
            if(status.strength > 0)
                add(
                    text("STR").color(Colors.red).append(
                    text(": ${status.strength}").color(Colors.white)))
            if(status.swiftness > 0)
                add(
                    text("SWT").color(Colors.skyblue).append(
                    text(": ${status.swiftness}").color(Colors.white)))
            if(status.balance > 0)
                add(
                    text("BAL").color(Colors.green).append(
                    text(": ${status.balance}").color(Colors.white)))
            if(status.concentration > 0)
                add(
                    text("CON").color(Colors.gold).append(
                    text(": ${status.concentration}").color(Colors.white)))
        }.apply {
            if(level > 0) {
                add(text(""))
                add(
                    text("요구 레벨: ").color(Colors.beige).append(
                    text(": $level").color(Colors.white)))
            }
        }
    }

    fun setStatus()
    fun setPDC()
    fun setLore()

    companion object {
        fun register(uuid: String, equipment: Equipment) : Equipment = equipment.apply {
            setStatus()
            setPDC()
            setLore()
            EquipmentContainer[uuid] = this
        }
    }
}

val STATUS_KEY: NamespacedKey = NamespacedKey.minecraft("status_key")
val STRENGTH: NamespacedKey = NamespacedKey.minecraft("str")
val SWIFTNESS: NamespacedKey = NamespacedKey.minecraft("swt")
val BALANCE: NamespacedKey = NamespacedKey.minecraft("bal")
val CONCENTRATION: NamespacedKey = NamespacedKey.minecraft("con")
val LEVEL_CONSTRAINT_KEY: NamespacedKey = NamespacedKey.minecraft("level_constraint")

internal fun ItemStack.isEquipment(): Boolean {
    return this.type.name.let {
        it.contains(Status.HELMET)     || it.contains(Status.CHESTPLATE) ||
        it.contains(Status.LEGGINGS)   || it.contains(Status.BOOTS)
    }
}

internal fun ItemStack.isTool(): Boolean {
    return this.isEquipment()   || this.type.name.let {
        it.contains(Status.PICKAXE)    || it.contains(Status.AXE)         || it.contains(Status.SWORD)       ||
        it.contains(Status.BOW)        || it.contains(Status.CROSSBOW)    || it.contains(Status.TRIDENT)     ||
        it.contains(Status.SHIELD)     || it.contains(Status.FISHING_ROD)
    }
}



