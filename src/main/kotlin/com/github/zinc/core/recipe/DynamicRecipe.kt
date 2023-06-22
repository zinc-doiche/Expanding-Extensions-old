package com.github.zinc.core.recipe

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

interface DynamicRecipe {
    fun isCorrect(origin: ItemStack, ingredient: ItemStack): Boolean
    fun getResult(item: ItemStack): ItemStack

    companion object { val dynamicKey = NamespacedKey.minecraft("dynamic_smote") }
}