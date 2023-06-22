package com.github.zinc.util

import com.github.zinc.util.Synchronous
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

internal fun item(material: Material, block: (ItemMeta) -> Unit): ItemStack {
    return ItemStack(material).apply {
        editMeta(block)
    }
}

internal fun item(material: Material, name: Component, block: ((ItemMeta) -> Unit)? = null): ItemStack {
    return ItemStack(material).apply {
        this.editMeta {
            it.displayName(name)
            block?.invoke(it) ?: return@editMeta
        }
    }
}

internal fun getCustomItem(
    material: Material,
    name: Component,
    customModelNumber: Int,
    init: ((ItemMeta) -> Unit)? = null
): ItemStack {
    return ItemStack(material).apply {
        editMeta { meta ->
            meta.displayName(name)
            meta.setCustomModelData(customModelNumber)
            init?.invoke(meta) ?: return@editMeta
        }
    }
}

internal fun isNullOrAir(itemStack: ItemStack?): Boolean {
    val type = itemStack?.type ?: return true // null
    return type == Material.AIR // air
}

internal fun ItemStack.getPersistent(key: NamespacedKey)
        = this.itemMeta.getPersistent(key)

internal fun<T, Z> ItemStack.getPersistent(key: NamespacedKey, type: PersistentDataType<T, Z>)
        = this.itemMeta.getPersistent(key, type)

internal fun ItemMeta.getPersistent(key: NamespacedKey)
        = this.persistentDataContainer.get(key, PersistentDataType.STRING)

internal fun<T, Z> ItemMeta.getPersistent(key: NamespacedKey, type: PersistentDataType<T, Z>)
        = this.persistentDataContainer.get(key, type)

internal fun ItemStack.setPersistent(key: NamespacedKey, value: String)
        = this.setPersistent(key, value, PersistentDataType.STRING)

internal fun<T, Z : Any> ItemStack.setPersistent(key: NamespacedKey, value: Z, type: PersistentDataType<T, Z>)
        = this.editMeta{ it.setPersistent(key, value, type) }

internal fun ItemMeta.setPersistent(key: NamespacedKey, value: String)
        = this.setPersistent(key, value, PersistentDataType.STRING)

internal fun<T, Z: Any> ItemMeta.setPersistent(key: NamespacedKey, value: Z, type: PersistentDataType<T, Z>)
        = this.persistentDataContainer.set(key, type, value)

// itemMeta is nullable
internal fun ItemStack.hasPersistent(key: NamespacedKey)
        = this.itemMeta?.hasPersistent(key) ?: false

internal fun ItemMeta.hasPersistent(key: NamespacedKey)
        = this.persistentDataContainer.has(key)

internal val AIR: ItemStack = ItemStack(Material.AIR)

internal fun Player.setItem(slot: Int, itemStack: ItemStack) {
    inventory.setItem(slot, itemStack)
}

internal fun Player.setItem(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
    inventory.setItem(equipmentSlot, itemStack)
}

@Synchronous
internal fun Player.removeSlot(slot: Int) {
    inventory.setItem(slot, AIR)
}

@Synchronous
internal fun Player.removeSlot(equipmentSlot: EquipmentSlot) {
    inventory.setItem(equipmentSlot, AIR)
}

/**
 * 1. 일단 인벤토리에 저장 시도
 *
 * 2. 안되면 뱉음
 */
@Synchronous
internal fun Player.giveItem(itemStack: ItemStack) {
    val emptySlot = inventory.firstEmpty()
    if(emptySlot == -1) world.dropItem(location, itemStack)
    else setItem(emptySlot, itemStack)
    //sendMessage(itemStack.type.toString())
}

fun Inventory.isFull(): Boolean {
    return this.maxStackSize == this.size
}

