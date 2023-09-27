package com.github.zinc.module.item.`object`.trinket

import com.github.zinc.NAMESPACE
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.mongodb.findOne
import com.github.zinc.util.toItemStack
import com.mongodb.DBRef
import org.bson.types.ObjectId
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

interface Trinket {
    val _id: ObjectId
    val name: String
    val slot: TrinketSlot
    val item: ItemStack?
        get() {
            val serializedItem = MongoDB["trinket"]
                .findOne("_id", _id)
                ?.getString("item")
            return toItemStack(serializedItem)
        }

    companion object {
        val namespace: NamespacedKey = NamespacedKey(NAMESPACE, "trinket")
        private val trinkets: Map<String, Trinket> = HashMap()

        operator fun get(name: String): Trinket? = trinkets[name]
        operator fun set(name: String, trinket: Trinket) {
            (trinkets as HashMap)[name] = trinket
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