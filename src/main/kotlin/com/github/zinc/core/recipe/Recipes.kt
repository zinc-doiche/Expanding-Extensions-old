package com.github.zinc.core.recipe

import com.github.zinc.plugin
import com.github.zinc.util.Colors
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.text
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.SmithingRecipe

object Recipes {
    private val list = ArrayList<Recipe>()

    private val oceanArmor = SmithingRecipe(
        NamespacedKey.minecraft("ocean_armor_diamond"),
        item(Material.DIAMOND_CHESTPLATE) {
            it.lore()?.add(text(""))
            it.lore()?.add(text("보호됨").color(Colors.skyblue))
        },
        MaterialChoice(Material.DIAMOND_CHESTPLATE),
        MaterialChoice(Material.HEART_OF_THE_SEA),
        true
    ).apply(list::add)

    fun registerAll() = list.forEach(plugin.server::addRecipe)
}
