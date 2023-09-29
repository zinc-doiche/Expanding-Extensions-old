package com.github.zinc.util

import com.github.zinc.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal fun item(material: Material, block: (ItemMeta) -> Unit): ItemStack {
    return ItemStack(material).edit(block)
}

internal fun item(
    material: Material,
    name: Component,
    lore: List<Component>? = null,
    amount: Int = 1,
    customModelNumber: Int? = null,
    block: ((ItemMeta) -> Unit)? = null,
): ItemStack {
    return ItemStack(material, amount).edit { meta ->
        meta.displayName(name)
        if(lore != null) {
            meta.lore(lore)
        }
        if(customModelNumber != null) {
            meta.setCustomModelData(customModelNumber)
        }
        block?.invoke(meta)
    }
}

internal fun getSkull(player: Player): ItemStack = item(Material.PLAYER_HEAD) { meta ->
    (meta as SkullMeta).owningPlayer = player
}

internal fun getSkull(player: Player, editMeta: (ItemMeta) -> Unit): ItemStack = item(Material.PLAYER_HEAD) { meta ->
    (meta as SkullMeta).owningPlayer = player
    editMeta(meta)
}

internal fun getCustomItem(
    material: Material,
    name: Component,
    customModelNumber: Int,
    init: ((ItemMeta) -> Unit)? = null,
): ItemStack {
    return ItemStack(material).apply {
        editMeta { meta ->
            meta.displayName(name)
            meta.setCustomModelData(customModelNumber)
            init?.invoke(meta) ?: return@editMeta
        }
    }
}

internal fun item(
    material: Material,
    displayName: Component,
    customModelNumber: Int,
    lore: List<Component>,
    editMeta: (ItemMeta) -> Unit,
): ItemStack {
    return ItemStack(material)
}

internal fun isNull(itemStack: ItemStack?): Boolean {
    val type = itemStack?.type ?: return true // null
    return type.isAir // air
}

internal fun isNotNull(itemStack: ItemStack?): Boolean {
    return !isNull(itemStack)
}

internal val ItemStack.isFullStack: Boolean
    get() = getMaxStackSize() == amount || getMaxStackSize() == 1

internal fun ItemStack.edit(editMeta: (ItemMeta) -> Unit): ItemStack {
    this.editMeta(editMeta)
    return this
}

internal fun ItemStack.getPersistent(key: NamespacedKey)
        = this.itemMeta.getPersistent(key)

internal fun<T, Z> ItemStack.getPersistent(key: NamespacedKey, type: PersistentDataType<T, Z>)
        = this.itemMeta.getPersistent(key, type)

internal fun ItemMeta.getPersistent(key: NamespacedKey)
        = this.persistentDataContainer.get(key, PersistentDataType.STRING)

internal fun<T, Z> ItemMeta.getPersistent(key: NamespacedKey, type: PersistentDataType<T, Z>)
        = this.persistentDataContainer.get(key, type)

internal fun ItemStack.setPersistent(key: NamespacedKey, value: String)
        = this.setPersistent(key, value, PersistentDataType.STRING)

internal fun<T, Z : Any> ItemStack.setPersistent(key: NamespacedKey, value: Z, type: PersistentDataType<T, Z>)
        = this.editMeta{ it.setPersistent(key, value, type) }

internal fun ItemMeta.setPersistent(key: NamespacedKey, value: String)
        = this.setPersistent(key, value, PersistentDataType.STRING)

internal fun<T, Z: Any> ItemMeta.setPersistent(key: NamespacedKey, value: Z, type: PersistentDataType<T, Z>)
        = this.persistentDataContainer.set(key, type, value)

// itemMeta is nullable
internal fun ItemStack.hasPersistent(key: NamespacedKey)
        = this.itemMeta?.hasPersistent(key) ?: false

internal fun ItemMeta.hasPersistent(key: NamespacedKey)
        = this.persistentDataContainer.has(key)

internal val AIR: ItemStack = ItemStack(Material.AIR)

internal fun Player.setItem(slot: Int, itemStack: ItemStack) {
    inventory.setItem(slot, itemStack)
}

internal fun Player.setItem(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
    inventory.setItem(equipmentSlot, itemStack)
}

internal fun Player.removeSlot(slot: Int) {
    inventory.setItem(slot, AIR)
}

internal fun Player.removeSlot(equipmentSlot: EquipmentSlot) {
    inventory.setItem(equipmentSlot, AIR)
}

/**
 * 1. 일단 인벤토리에 저장 시도
 *
 * 2. 안되면 뱉음
 */
internal fun Player.addItem(itemStack: ItemStack) {
    val exceed = inventory.addItem(itemStack)
    if(exceed.isNotEmpty()) {
        world.dropItem(location, exceed[0]!!)
    }
}

fun Inventory.isFull(): Boolean {
    return fold(false) { isPrevFull, itemStack -> isNotNull(itemStack) && isPrevFull && itemStack.isFullStack }
}

internal fun ItemStack.toJson(): String? {
    try {
        ByteArrayOutputStream().use { arrayOutputStream ->
            BukkitObjectOutputStream(arrayOutputStream).use { objectOutputStream ->
                objectOutputStream.writeObject(this)
                return Base64Coder.encodeLines(arrayOutputStream.toByteArray())
            }
        }
    } catch (e: Exception) {
        plugin.slF4JLogger.error("Error: itemstack -> json", e)
    }
    return null
}

internal fun toItemStack(json: String?): ItemStack? {
    try {
        ByteArrayInputStream(Base64Coder.decodeLines(json)).use { arrayInputStream ->
            BukkitObjectInputStream(
                arrayInputStream
            ).use { objectInputStream -> return objectInputStream.readObject() as ItemStack }
        }
    } catch (e: Exception) {
        plugin.slF4JLogger.error("Error: json -> itemstack", e)
    }
    return null
}
