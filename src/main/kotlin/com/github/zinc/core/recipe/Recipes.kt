package com.github.zinc.core.recipe

import com.github.zinc.core.equipment.OceanArmor
import com.github.zinc.plugin
import com.github.zinc.util.getCustomItem
import com.github.zinc.util.setPersistent
import com.github.zinc.util.text
import com.github.zinc.util.texts
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import org.bukkit.inventory.recipe.CraftingBookCategory
import java.util.*

object Recipes {
    private val list = ArrayList<Recipe>()

    val customRecipes = HashSet<DynamicRecipe>()

    private val oceanArmorRecipe = DynamicSmitingRecipe(
        result = OceanArmor.getArmor(),
        origin =  {it.type.name.contains("CHESTPLATE")},
        ingredient = {it.type == Material.HEART_OF_THE_SEA}
    )

    private val starKey = NamespacedKey.minecraft("nether_star_fragment")
    val netherStarFragment = getCustomItem(Material.PAPER, text("네더의 별 파편"), 7) {
        it.setPersistent(starKey, starKey.namespace)
    }
    private val netherStarFragmentRecipe =
        ShapelessRecipe(NamespacedKey.minecraft("nether_star_fragment_recipe"), netherStarFragment.apply {amount = 4}).setup {
            this as ShapelessRecipe
            addIngredient(Material.NETHER_STAR)
            this.category = CraftingBookCategory.MISC
        }
    private val netherStarRecipe =
        ShapelessRecipe(NamespacedKey.minecraft("nether_star"), ItemStack(Material.NETHER_STAR)).setup {
            this as ShapelessRecipe
            addIngredient(4, netherStarFragment)
            this.category = CraftingBookCategory.MISC
        }

    val statusElement = getCustomItem(Material.PAPER, text("엔더 정수"), 8) {
        it.lore(texts("asd", "dsa"))
    }
    val statusFragment = getCustomItem(Material.PAPER, text("엔더 파편"), 9) {
        it.lore(texts("asd", "dsa"))
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