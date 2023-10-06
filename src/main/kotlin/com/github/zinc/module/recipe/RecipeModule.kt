package com.github.zinc.module.recipe

import com.github.zinc.module.Module
import com.github.zinc.module.item.`object`.equipment.Equipment
import com.github.zinc.plugin
import com.github.zinc.util.item
import com.github.zinc.util.setPersistent
import com.github.zinc.util.texts
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

object RecipeModule: Module {
    override fun registerCommands() {

    }

    override fun registerListeners() {

    }

    override fun register() {
        val starFragmentKey = NamespacedKey(plugin, "nether_star_fragment")
        val starKey = NamespacedKey(plugin, "nether_star")
        val statusElementKey = NamespacedKey(plugin, "status_element")
        val heartChestplateKey = NamespacedKey(plugin, "heart_chestplate")

        val netherStarFragment = item(Material.PAPER, Component.text("네더의 별 파편"), amount = 7) {
            it.setPersistent(starFragmentKey, starFragmentKey.namespace)
        }
        val statusElement = item(Material.PAPER, Component.text("엔더 정수"), texts("asd", "dsa"), customModelNumber = 8)
        val statusFragment = item(Material.PAPER, Component.text("엔더 파편"), texts("asd", "dsa"), customModelNumber = 9)

        val netherStarFragmentRecipe = ShapelessRecipe(starFragmentKey, netherStarFragment.apply {amount = 4})
        netherStarFragmentRecipe.setup {
            this as ShapelessRecipe
            addIngredient(Material.NETHER_STAR)
            this.category = CraftingBookCategory.MISC
        }
        val netherStarRecipe = ShapelessRecipe(starKey, ItemStack(Material.NETHER_STAR))
        netherStarRecipe.setup {
            this as ShapelessRecipe
            addIngredient(4, netherStarFragment)
            this.category = CraftingBookCategory.MISC
        }
        //setIngredient 는 수량을 제외하고 ItemMeta 까지 비교를 해준다.
        val statusElementRecipe = ShapedRecipe(statusElementKey, statusElement)
        statusElementRecipe.setup {
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

        val heartChestplateRecipe = ShapedRecipe(heartChestplateKey, Equipment["heart_chestplate"]?.item ?: return)
        heartChestplateRecipe.setup {
            this as ShapedRecipe
            shape(
                "d d",
                "chc",
                "dCd")
            setIngredient('d', Material.DIAMOND)
            setIngredient('c', Material.COPPER_INGOT)
            setIngredient('h', Material.HEART_OF_THE_SEA)
            setIngredient('C', Material.DIAMOND_CHESTPLATE)
            this.category = CraftingBookCategory.EQUIPMENT
        }

        Bukkit.addRecipe(heartChestplateRecipe)
        Bukkit.addRecipe(netherStarFragmentRecipe)
        Bukkit.addRecipe(netherStarRecipe)
        Bukkit.addRecipe(statusElementRecipe)
    }

    override fun onDisable() {

    }
}

private fun Recipe.setup(setting: Recipe.() -> Unit) {
    setting(this)
}