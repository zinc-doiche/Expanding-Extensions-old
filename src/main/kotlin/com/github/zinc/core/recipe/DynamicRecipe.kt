package com.github.zinc.core.recipe

import org.bukkit.inventory.ItemStack

interface DynamicRecipe {
    fun isCorrect(origin: ItemStack, ingredient: ItemStack): Boolean
    fun getResult(item: ItemStack): ItemStack
}