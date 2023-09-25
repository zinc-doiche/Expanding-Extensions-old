package com.github.zinc.lib.brief

import com.github.zinc.util.edit
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

data class BriefItem(
    val material: Material,
    val displayName: String,
    val customModelNumber: Int = 0,
    val lore: List<String>? = null
) {
    fun create(): ItemStack {
        return ItemStack(material).edit {
            it.displayName(Component.text(displayName))
            it.setCustomModelData(customModelNumber)
            this@BriefItem.lore?.let { lore -> it.lore(lore.map(Component::text)) }
        }
    }
    fun create(editMeta: (ItemMeta) -> Unit): ItemStack {
        return ItemStack(material).edit {
            it.displayName(Component.text(displayName))
            it.setCustomModelData(customModelNumber)
            this@BriefItem.lore?.let { lore -> it.lore(lore.map(Component::text)) }
            editMeta(it)
        }
    }
}
