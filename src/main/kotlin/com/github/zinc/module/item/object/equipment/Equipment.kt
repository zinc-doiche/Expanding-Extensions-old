package com.github.zinc.module.item.`object`.equipment

import com.github.zinc.module.item.`object`.Item
import com.github.zinc.plugin
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Equipment: Item {
    val name: String
    val item: ItemStack

    fun onEquip(player: Player)
    fun onTakeOff(player: Player)

    override fun register() {
        equipments[name] = this
    }

    companion object {
        private val equipments = HashMap<String, Equipment>()
        val names: Iterable<String>
            get() = equipments.keys
        val namespace: NamespacedKey
            get() = NamespacedKey(plugin, "equipment")

        operator fun get(name: String): Equipment? = equipments[name]
        operator fun set(name: String, equipment: Equipment) {
            equipments[name] = equipment
        }
        operator fun contains(name: String): Boolean = equipments.contains(name)

        fun add(equipment: Equipment) {
            equipments[equipment.name] = equipment
        }
    }
}