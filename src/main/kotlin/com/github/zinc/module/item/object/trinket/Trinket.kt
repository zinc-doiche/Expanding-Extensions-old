package com.github.zinc.module.item.`object`.trinket

import com.github.zinc.NAMESPACE
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.mongodb.findOne
import com.github.zinc.util.item
import com.github.zinc.util.list
import com.github.zinc.util.setPersistent
import com.github.zinc.util.toItemStack
import com.mongodb.DBRef
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bson.types.ObjectId
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

interface Trinket {
    val name: String
    val slot: TrinketSlot

    fun getItem(): ItemStack

    fun register() {
        Trinket[name] = this
    }

    fun trinketItem(
        material: Material,
        displayName: Component,
        availableSlot: TrinketSlot,
        whenEquip: Component,
        additionalLore: List<Component>? = null
    ): ItemStack = item(
        material,
        displayName,
        list(
            text("착용 가능 슬롯: ").append(text(availableSlot.korName, NamedTextColor.YELLOW, TextDecoration.BOLD)),
            empty(),
            text("착용 시: ").append(whenEquip),
            empty()
        ).apply {
            if(additionalLore != null) {
                addAll(additionalLore)
            }
        },
        block = { it.setPersistent(namespace, name) }
    )

    companion object {
        private val trinkets: MutableMap<String, Trinket> = HashMap()
        val names: List<String>
            get() = trinkets.values.map { it.name }
        val namespace: NamespacedKey
            get() = NamespacedKey(NAMESPACE, "trinket")

        operator fun get(name: String): Trinket? = trinkets[name]
        operator fun set(name: String, trinket: Trinket) {
            trinkets[name] = trinket
        }
        operator fun contains(name: String): Boolean = trinkets.containsKey(name)
    }
}

enum class TrinketSlot(val korName: String) {
    RING("반지"),
    EARRING("귀걸이"),
    NECKLACE("목걸이"),
    BRACELET("팔찌"),
    BELT("허리띠"),
    SHOES("신발");
}