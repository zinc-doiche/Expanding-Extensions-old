package com.github.zinc.core.recipe

import com.github.zinc.plugin
import com.github.zinc.util.Colors
import com.github.zinc.util.extension.getCustomItem
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.setPersistent
import com.github.zinc.util.extension.text
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.recipe.CraftingBookCategory
import java.util.*
import kotlin.collections.ArrayList

object Recipes {
    private val list = ArrayList<Recipe>()

    private val oceanArmorRecipes =
        Material.values()
        .filter { it.name.contains("CHESTPLATE") }
        .forEach { material ->
            list.add(
                SmithingRecipe(
                    NamespacedKey.minecraft("ocean_${material.name.lowercase(Locale.getDefault())}"),
                    item(material) {
                        it.lore()?.add(text(""))
                        it.lore()?.add(text("보호됨").color(Colors.skyblue))
                    },
                    MaterialChoice(material),
                    MaterialChoice(Material.HEART_OF_THE_SEA),
                    true
                )
            )
        }

    private val starKey = NamespacedKey.minecraft("nether_star_fragment")
    private val netherStarFragment = getCustomItem(Material.PAPER, text(""), 7) {
        it.setPersistent(starKey, starKey.namespace)
    }
    private val netherStarFragmentRecipe =
        ShapelessRecipe(starKey, netherStarFragment.apply {amount = 4}).setup {
            this as ShapelessRecipe
            addIngredient(Material.NETHER_STAR)
            this.category = CraftingBookCategory.MISC
        }
    private val netherStarRecipe =
        ShapelessRecipe(NamespacedKey.minecraft("nether_star_fragment"), ItemStack(Material.NETHER_STAR)).setup {
                    this as ShapelessRecipe
            addIngredient(4, netherStarFragment)
            this.category = CraftingBookCategory.MISC
        }

    private val statusElement = getCustomItem(Material.PAPER, text("엔더 정수"), 8) {
        it.lore()
    }
    private val statusFragment = getCustomItem(Material.PAPER, text("엔더 파편"), 9) {
        it.lore()
    }

    //setIngredient 는 수량을 제외하고 ItemMeta 까지 비교를 해준다.
    private val statusElementRecipe =
        ShapedRecipe(NamespacedKey.minecraft("status_element"), statusElement).setup {
            this as ShapedRecipe
            shape("nEn", "EsE", "nnn")
            setIngredient('n', netherStarFragment)
            setIngredient('E', Material.EMERALD_BLOCK)
            setIngredient('s', statusFragment)
            this.category = CraftingBookCategory.MISC
            this.group = "element"

        }

    fun registerAll() = list.forEach(plugin.server::addRecipe)

    private fun Recipe.setup(setting: Recipe.() -> Unit) {
        setting(this)
        list.add(this)
    }
}


