package com.github.zinc.util.extension

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
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
    block: ((ItemMeta) -> Unit)? = null
): ItemStack {
    return ItemStack(material).apply {
        editMeta { meta ->
            meta.displayName(name)
            meta.setCustomModelData(customModelNumber)
            block?.invoke(meta) ?: return@editMeta
        }
    }
}

internal fun isNullOrAir(itemStack: ItemStack?) = itemStack?.type == Material.AIR

internal fun ItemStack.getPersistent(key: NamespacedKey)
        = this.itemMeta.persistentDataContainer.get(key, PersistentDataType.STRING)

internal fun ItemStack.setPersistent(key: NamespacedKey, value: String)
        = this.itemMeta.persistentDataContainer.set(key, PersistentDataType.STRING, value)

 internal fun<T, Z> ItemStack.getPersistent(key: NamespacedKey, type: PersistentDataType<T, Z>)
        = this.itemMeta.persistentDataContainer.get(key, type)

internal fun<T, Z : Any> ItemStack.setPersistent(key: NamespacedKey, value: Z, type: PersistentDataType<T, Z>)
        = this.itemMeta.persistentDataContainer.set(key, type, value)

internal fun ItemStack.hasPersistent(key: NamespacedKey)
        = this.itemMeta?.persistentDataContainer?.has(key) ?: false

internal val AIR: ItemStack = ItemStack(Material.AIR)

