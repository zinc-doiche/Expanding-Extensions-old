package com.github.zinc.module.user.`object`

import com.github.zinc.NAMESPACE
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.mongodb.findOne
import com.github.zinc.util.toItemStack
import io.netty.handler.codec.spdy.SpdyHttpHeaders.Names
import org.bson.types.ObjectId
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

/**
 * trinket collection :
 * {
 *    _id: ...
 *    name: "..."
 *    slot: "..."
 *    item: ...
 * }
 *
 * in user:
 * {
 * ...
 * trinkets: \[
 *     { slot: "...", _id: ... },
 *     {...},
 *     ...
 * \]
 * ...
 * }
 */
class Trinket(val _id: ObjectId) {
    companion object {
        val namespace: NamespacedKey = NamespacedKey(NAMESPACE, "trinket")
    }

    fun getItem(): ItemStack {
        return MongoDB["trinket"].findOne("_id", _id)?.getString("item")
            ?.let(::toItemStack)
            ?: ItemStack(Material.PAPER)
    }
}


enum class TrinketSlot {
    RING,
    EARRING,
    NECKLACE,
    BRACELET,
    BELT,
    SHOES
}