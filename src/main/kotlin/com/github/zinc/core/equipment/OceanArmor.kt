package com.github.zinc.core.equipment

import com.github.zinc.util.Colors
import com.github.zinc.util.item
import com.github.zinc.util.text
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class OceanArmor {
    companion object {
        val KEY: NamespacedKey = NamespacedKey.minecraft("o");
    }

    fun getArmorBy(material: Material): ItemStack {
        return item(material) { meta ->
            meta.lore()?.add(text("")) ?: return@item
            meta.lore()?.add(text("보호됨").color(Colors.skyblue)) ?: return@item
        }
    }
}