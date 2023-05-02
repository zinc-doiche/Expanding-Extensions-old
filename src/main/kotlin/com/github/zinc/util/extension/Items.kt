package com.github.zinc.util.extension

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

internal fun item(material: Material, block: (ItemMeta) -> Unit): ItemStack {
    return ItemStack(material).apply {
        editMeta(block)
    }
}

internal fun getCustomItem(material: Material, name: String, customModelNumber: Int): ItemStack {
    return ItemStack(material).apply {
        editMeta { meta ->
            meta.displayName(text(name))
            meta.setCustomModelData(customModelNumber)
        }
    }
}

internal fun isNullOrAir(itemStack: ItemStack?) = itemStack?.type == Material.AIR