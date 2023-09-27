package com.github.zinc.module.user.gui

import com.github.zinc.NAMESPACE
import com.github.zinc.lib.gui.EventType
import com.github.zinc.lib.gui.SquareGUI
import com.github.zinc.module.user.`object`.Trinket
import com.github.zinc.module.user.`object`.TrinketSlot
import com.github.zinc.module.user.`object`.User
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.util.*
import com.github.zinc.util.toItemStack
import com.mongodb.client.model.Filters
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class TrinketGUI(val uuid: String): SquareGUI() {
    private val inventory: Inventory = Bukkit.createInventory(this, 9, text("장신구"))
    @Asynchronous
    override fun open() {
        HeartbeatScope().async {
            val user = User[uuid]?: return@async
            MongoDB["trinket"]
                .find(Filters.`in`("_id", user.trinkets.map { it.value._id }))
                .map {
                    TrinketSlot.valueOf(it.getString("slot")).ordinal to
                            (toItemStack(it.getString("item")) ?: ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                }
                .forEach { setItem(it.second, it.first, 0) }
            user.player?.openInventory(inventory)
        }
    }

    override fun onEvent(event: InventoryEvent, type: EventType) {
        val player: Player
        when(type) {
            EventType.CLICK -> {
                event as InventoryClickEvent
                player = event.whoClicked as Player

                if(event.isShiftClick) {
                    if(event.rawSlot < 9) {
                        val trinket = event.currentItem ?: return
                        if(!trinket.hasPersistent(Trinket.namespace)) {
                            return
                        }

                    }
                }

                if (event.rawSlot < 9) {
                    event.isCancelled = true
                }
                if (event.cursor.hasPersistent(Trinket.namespace)) {

                }
                if (event.rawSlot != 1) {

                }
            }
            EventType.CLOSE -> {

            }
            else -> return
        }
    }

    override fun getInventory(): Inventory = inventory
}