package com.github.zinc.module.item.gui

import com.github.zinc.lib.gui.EventType
import com.github.zinc.lib.gui.SquareGUI
import com.github.zinc.module.item.`object`.Passive
import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.item.`object`.trinket.TrinketSlot
import com.github.zinc.module.user.`object`.User
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.util.*
import com.github.zinc.util.toItemStack
import com.mongodb.client.model.Filters
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class TrinketGUI(private val uuid: String): SquareGUI() {
    private val inventory: Inventory = Bukkit.createInventory(this, 9, text("장신구"))

    @Asynchronous
    override fun open() {
        HeartbeatScope().async {
            val user = User[uuid]?: return@async
            TrinketSlot.entries.forEach { slot ->
                val item = if(user.trinkets.contains(slot)) {
                    user.trinkets[slot]?.getItem()
                } else {
                    item(Material.GRAY_STAINED_GLASS_PANE, text("${slot.korName} 슬롯")
                        .color(NamedTextColor.GRAY)
                        .noItalic())
                }
                setItem(item, slot.ordinal)
            }
            user.player?.openInventory(inventory)
        }
    }

    override fun onEvent(event: InventoryEvent, type: EventType) {
        when(type) {
            EventType.CLICK -> {
                event as InventoryClickEvent
                val user = User[uuid] ?: return
                val player = event.whoClicked as Player

                //인벤토리 쉬프트 클릭
                if(event.rawSlot >= 9 && event.isShiftClick) {
                    event.isCancelled = true
                    val item = event.currentItem ?: return

                    //인벤토리에서 클릭한 아이템이 장신구가 아니면 캔슬 후 리턴
                    if(!item.hasPersistent(Trinket.namespace)) {
                        return
                    }
                    val name = item.getPersistent(Trinket.namespace)!!
                    val trinket = Trinket[name] ?: return

                    //이미 있으면 캔슬 후 리턴
                    if(getItem(trinket.slot.ordinal)?.type != Material.GRAY_STAINED_GLASS_PANE) {
                        return
                    }
                    setItem(item, trinket.slot.ordinal)
                    player.inventory.removeItem(item)
                    user.setTrinket(trinket)
                    //Passive 존재 시 발동
                    if(trinket is Passive) {
                        trinket.on(player)
                    }
                    return
                }

                // 장신구 슬롯 클릭
                if(event.rawSlot < 9) {
                    event.isCancelled = true
                    val item = event.currentItem ?: return

                    //장신구가 있으면
                    if(item.hasPersistent(Trinket.namespace)) {
                        val outTrinket = Trinket[item.getPersistent(Trinket.namespace)!!] ?: return

                        //쉬프트 클릭이면
                        if(event.isShiftClick) {
                            player.inventory.addItem(item)
                            setItem(ItemStack(Material.GRAY_STAINED_GLASS_PANE), event.rawSlot)
                        }
                        //빈손 or 장신구가 아닌 아이템으로 일반 클릭 시
                        else if(!event.cursor.hasPersistent(Trinket.namespace)) {
                            //빈손이 아니면 리턴
                            if(isNotNull(event.cursor)) {
                                return
                            }
                            player.setItemOnCursor(item)
                            setItem(ItemStack(Material.GRAY_STAINED_GLASS_PANE), event.rawSlot)
                        }
                        //장신구를 들고 클릭 시
                        else {
                            val trinket = Trinket[event.cursor.getPersistent(Trinket.namespace)!!] ?: return
                            //맞는 슬롯이 아니면 리턴
                            if(trinket.slot != outTrinket.slot) {
                                return
                            }
                            val outItem = item.clone()
                            setItem(event.cursor, event.rawSlot)
                            player.setItemOnCursor(outItem)
                            user.setTrinket(trinket)
                            //Passive 존재 시 발동
                            if(trinket is Passive) {
                                trinket.on(player)
                            }
                        }
                        user.removeTrinket(outTrinket.slot)
                        //Passive 존재 시 해제
                        if(outTrinket is Passive) {
                            outTrinket.off(player)
                        }
                        return
                    }

                    //장신구가 없으면
                    //빈손 or 장신구가 아닌 아이템으로 클릭 시 리턴
                    if(!event.cursor.hasPersistent(Trinket.namespace)) {
                        return
                    }

                    //장신구 든 채로 클릭 시
                    val trinket = Trinket[event.cursor.getPersistent(Trinket.namespace)!!] ?: return
                    //쉬프트 클릭이면 리턴
                    if(event.isShiftClick) {
                        return
                    }
                    //맞는 슬롯이 아니면 리턴
                    if(trinket.slot.ordinal != event.rawSlot) {
                        return
                    }
                    setItem(event.cursor, event.rawSlot)
                    player.setItemOnCursor(null)
                    user.setTrinket(trinket)
                    //Passive 존재 시 발동
                    if(trinket is Passive) {
                        trinket.on(player)
                    }
                }
            }
            else -> return
        }
    }

    override fun getInventory(): Inventory = inventory
}