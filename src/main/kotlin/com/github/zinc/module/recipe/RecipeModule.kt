package com.github.zinc.module.recipe

import com.github.zinc.module.Module
import com.github.zinc.util.item
import com.github.zinc.util.setPersistent
import com.github.zinc.util.texts
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

class RecipeModule: Module {
    override fun registerCommands() {

    }

    override fun registerListeners() {

    }

    override fun register() {
        super.register()
        val starKey = NamespacedKey.minecraft("nether_star_fragment")
        val netherStarFragment = item(Material.PAPER, Component.text("네더의 별 파편"), amount = 7) {
            it.setPersistent(starKey, starKey.namespace)
        }
        val netherStarFragmentRecipe =
            ShapelessRecipe(NamespacedKey.minecraft("nether_star_fragment_recipe"), netherStarFragment.apply {amount = 4}).setup {
                this as ShapelessRecipe
                addIngredient(Material.NETHER_STAR)
                this.category = CraftingBookCategory.MISC
            }
        val netherStarRecipe =
            ShapelessRecipe(NamespacedKey.minecraft("nether_star"), ItemStack(Material.NETHER_STAR)).setup {
                this as ShapelessRecipe
                addIngredient(4, netherStarFragment)
                this.category = CraftingBookCategory.MISC
            }

        val statusElement = item(Material.PAPER, Component.text("엔더 정수"), customModelNumber = 8) {
            it.lore(texts("asd", "dsa"))
        }
        val statusFragment = item(Material.PAPER, Component.text("엔더 파편"), customModelNumber = 9) {
            it.lore(texts("asd", "dsa"))
        }

        //setIngredient 는 수량을 제외하고 ItemMeta 까지 비교를 해준다.
        val statusElementRecipe = ShapedRecipe(NamespacedKey.minecraft("status_element"), statusElement).setup {
            this as ShapedRecipe
            shape(
                "nEn",
                "EsE",
                "nnn")
            setIngredient('n', netherStarFragment)
            setIngredient('E', Material.EMERALD_BLOCK)
            setIngredient('s', statusFragment)
            this.category = CraftingBookCategory.MISC
            this.group = "element"
        }
    }

    override fun onDisable() {

    }

    private fun Recipe.setup(setting: Recipe.() -> Unit) {
        setting(this)
    }
}