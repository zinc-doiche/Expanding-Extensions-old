package com.github.zinc.core.recipe

import com.github.zinc.util.extension.hasPersistent
import com.github.zinc.util.extension.setPersistent
import org.bukkit.NamespacedKey
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
                !origin.hasPersistent(dynamicKey) && !ingredient.hasPersistent(dynamicKey)
    }

    override fun getResult(item: ItemStack): ItemStack {
        return result(item).apply { setPersistent(dynamicKey, dynamicKey.namespace) }
    }

    companion object { val dynamicKey = NamespacedKey.minecraft("dynamic_smote") }
}