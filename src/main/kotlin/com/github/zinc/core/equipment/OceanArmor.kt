package com.github.zinc.core.equipment

import com.github.zinc.core.player.Status
import com.github.zinc.util.Colors
import com.github.zinc.util.setPersistent
import com.github.zinc.util.text
import com.github.zinc.util.texts
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class OceanArmor(
    item: ItemStack,
    status: Status = Status(),
    level: Int = 0
): EquipmentAdapter(item, status, level) {
    override fun setLore() {
        item.lore(getLore().apply {
            add(text(""))
            add(text("바다의 가호"))
        })
    }

    override fun toString(): String {
        return "OceanArmor(item=$item, status=$status, level=$level)"
    }

    companion object {
        val KEY = NamespacedKey.minecraft("ocean_armor")

        fun getArmor(): (ItemStack) -> ItemStack = { item ->
            item.clone().apply {
                editMeta { meta ->
                    val lore = meta.lore() ?: texts()
                    lore.add(text(""))
                    lore.add(text("바다의 가호").color(Colors.skyblue))
                    meta.lore(lore)
                    meta.displayName(text("function"))
                    meta.setPersistent(KEY, KEY.key)
                }
            }
        }
    }
}