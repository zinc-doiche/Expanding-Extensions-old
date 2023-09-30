package com.github.zinc.module.user.gui

import com.github.zinc.lib.gui.EventType
import com.github.zinc.lib.gui.SquareGUI
import com.github.zinc.module.user.`object`.*
import com.github.zinc.module.user.`object`.damage
import com.github.zinc.module.user.`object`.speed
import com.github.zinc.util.*
import com.github.zinc.util.format
import com.github.zinc.util.item
import com.github.zinc.util.list
import com.github.zinc.util.plain
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory

class StatusGUI(val uuid: String): SquareGUI() {
    private val inventory: Inventory = Bukkit.createInventory(this, 9)

    private fun strengthLore(strength: Int, player: Player) = list(
        empty(),
        text("$strength")
            .color(NamedTextColor.LIGHT_PURPLE)
            .bold(),
        empty(),
        text("현재 기본 공격력: ${player.damage.format("#.##")}").color(NamedTextColor.GRAY)
    )

    private fun swiftnessLore(swiftness: Int, player: Player) = list(
        empty(),
        text("$swiftness")
            .color(NamedTextColor.LIGHT_PURPLE)
            .bold(),
        empty(),
        text("현재 이동속도: ${player.speed.format("#.##")}").color(NamedTextColor.GRAY)
    )

    private fun balanceLore(balance: Int, player: Player) = list(
        empty(),
        text("$balance")
            .color(NamedTextColor.LIGHT_PURPLE)
            .bold(),
        empty(),
        text("현재 체력: ${player.health.format("#.##")}").color(NamedTextColor.GRAY)
    )

    private fun concentrationLore(concentration: Int, user: User) = list(
        empty(),
        text("$concentration")
            .color(NamedTextColor.LIGHT_PURPLE)
            .bold(),
        empty(),
        text("현재 치명타 확률: ${user.criticalChance.format("#.##")}")
            .color(NamedTextColor.GRAY)
            .noItalic()
    )

    private fun remainLore(status: Status) = list(
        empty(),
        text("${status.remains}")
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.BOLD, true),
        empty(),
        plain("총 스탯(잔여 포함): ${status.total} / 600")
    )

    override fun open() {
        HeartbeatScope().async {
            val user = User[uuid] ?: return@async
            val player = user.player ?: return@async
            with(user.status) {
                val strengthIcon = item(
                    Material.RED_STAINED_GLASS_PANE,
                    plain("힘"),
                    strengthLore(strength, player).apply {
                        if(remains > 0) this.addText()
                    }
                )
                val swiftnessIcon = item(
                    Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                    plain("속도"),
                    swiftnessLore(swiftness, player).apply {
                        if(remains > 0) this.addText()
                    }
                )
                val balanceIcon = item(
                    Material.LIME_STAINED_GLASS_PANE,
                    plain("체력"),
                    balanceLore(balance, player).apply {
                        if(remains > 0) this.addText()
                    }
                )
                val concentrationIcon = item(
                    Material.YELLOW_STAINED_GLASS_PANE,
                    plain("집중"),
                    concentrationLore(concentration, user).apply {
                        if(remains > 0) this.addText()
                    }
                )

                setItem(strengthIcon, 0, 0)
                setItem(swiftnessIcon, 1, 0)
                setItem(balanceIcon, 2, 0)
                setItem(concentrationIcon, 3, 0)
                setItem(item(Material.BOOK, plain("남은 스탯 포인트"), remainLore(this)), 4, 0)
            }
            player.openInventory(inventory)
        }
    }

    override fun onEvent(event: InventoryEvent, type: EventType) {
        when(type) {
            EventType.CLICK -> {
                event as InventoryClickEvent
                event.isCancelled = true

                if(event.rawSlot > 3) {
                    return
                }
                val user = User[uuid] ?: return
                val statusType = StatusType.entries[event.rawSlot]

                if(user.status.remains == 0) {
                    return
                }
                user.status.distribute(statusType)
            }
            EventType.CLOSE -> {
                event as InventoryCloseEvent
                val user = User[uuid] ?: return
                user.status.applyStatus(event.player as Player)
            }
            else -> {
                return
            }
        }
    }

    override fun getInventory(): Inventory = inventory
}

private fun MutableList<Component>.addText() {
    add(empty())
    add(text("클릭하여 능력치를 올릴 수 있습니다.", NamedTextColor.GRAY).noItalic())
}