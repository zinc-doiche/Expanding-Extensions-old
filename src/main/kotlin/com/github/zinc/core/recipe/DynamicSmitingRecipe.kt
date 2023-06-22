package com.github.zinc.core.recipe

import com.github.zinc.util.hasPersistent
import com.github.zinc.util.setPersistent
import org.bukkit.inventory.ItemStack

class DynamicSmitingRecipe(
    val result: (ItemStack) -> ItemStack,
    val origin: (ItemStack) -> Boolean,
    val ingredient: (ItemStack) -> Boolean
): DynamicRecipe {

    /**
     * Do auto storing to the set
     */
    init { Recipes.customRecipes.add(this) }

    override fun isCorrect(origin: ItemStack, ingredient: ItemStack): Boolean {
        return origin(origin) && ingredient(ingredient) &&
                !origin.hasPersistent(DynamicRecipe.dynamicKey) && !ingredient.hasPersistent(DynamicRecipe.dynamicKey)
    }

    override fun getResult(item: ItemStack): ItemStack {
        return result(item).apply { setPersistent(DynamicRecipe.dynamicKey, DynamicRecipe.dynamicKey.key) }
    }


}