package com.github.zinc.core.equipment

import com.github.zinc.container.RecipeContainer
import com.github.zinc.util.extension.text
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

interface Recipe {
    val inventoryType: InventoryType

    fun isCorrect(items: Map<Int, ItemStack>): Boolean

    companion object {
        fun registerAll() {
            SmitingRecipe("ocean_armor", {it.apply{editMeta{meta->meta.lore()?.add(text("vhxlzbnxl"))}}},
                {it.type.name.contains("CHESTPLATE")}, {it.type == Material.HEART_OF_THE_SEA})
        }
    }
}

data class SmitingRecipe(
    val recipeName: String,
    val resultItem: (ItemStack) -> ItemStack,
    val origin: (ItemStack) -> Boolean,
    val ingredient: (ItemStack) -> Boolean
) : Recipe {
    override val inventoryType: InventoryType = InventoryType.SMITHING

    init { RecipeContainer[recipeName] = this }

    constructor(
        recipeName: String,
        resultItem: ItemStack,
        origin: ItemStack,
        ingredient: ItemStack
    ) : this(recipeName, {resultItem}, {origin == it}, {ingredient == it})

    override fun isCorrect(items: Map<Int, ItemStack>): Boolean {
        val originItem = items[0] ?: return false
        val ingredientItem = items[1] ?: return false

        return origin(originItem) && ingredient(ingredientItem)
    }
}