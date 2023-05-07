package com.github.zinc.util.extension

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

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