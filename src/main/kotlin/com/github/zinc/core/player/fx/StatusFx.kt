package com.github.zinc.core.player.fx

import com.github.zinc.core.player.domain.PlayerContainer
import com.github.zinc.core.player.domain.StatusType
import com.github.zinc.core.player.fx.StatusFx.clear
import com.github.zinc.core.player.manager.PlayerStatusManager
import com.github.zinc.util.extension.*
import com.github.zinc.util.extension.getCustomItem
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.text
import com.github.zinc.util.sound.Sounds
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.frame.InvSlot
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

object StatusFx {
    val clear = ItemStack(Material.AIR)
    private val upIcon: ItemStack = getCustomItem(Material.PAPER, text("UP"), 1)
    val a = 0
    private val statusIcons: List<ItemStack> = listOf(
        item(Material.RED_STAINED_GLASS_PANE, text("STRENGTH")),
        item(Material.LIGHT_BLUE_STAINED_GLASS_PANE, text("SWIFTNESS")),
        item(Material.LIME_STAINED_GLASS_PANE, text("BALANCE")),
        item(Material.YELLOW_STAINED_GLASS_PANE, text("CONCENTRATION"))
    )

    private fun getRemainingStatusIcon(remain: Int): ItemStack {
        return item(
            if(remain > 0) Material.GREEN_STAINED_GLASS_PANE
            else Material.RED_STAINED_GLASS_PANE
        ) { meta ->
            meta.displayName(text("잔여스탯: $remain"))
            meta.setCustomModelData(7)
        }
    }

    fun getStatusFrame(player: Player): InvFrame {
        val playerDTO = PlayerContainer[player.name]!!
        val manager = PlayerStatusManager(playerDTO)

        return InvFX.frame(3, Component.text("${player.name}의 스테이터스")) {
            for (i in 0..3) {
                slot(2*i + 1, 0) {
                    if(manager.hasRemain() && manager.getTotalStatus() <= 600) {
                        item = upIcon

                        onClick {
//                            when(i) {
//                                0 -> playerDTO.playerStrength++
//                                1 -> playerDTO.playerSwiftness++
//                                2 -> playerDTO.playerBalance++
//                                3 -> playerDTO.playerConcentration++
//                            }
                            manager.updateStatus(StatusType.values()[i], 1)
                            manager.updateStatus(StatusType.REMAIN, -1)
                            player.playSound(Sounds.click)
                            player.openFrame(getStatusFrame(player))
                        }
                    }
                    else item = clear
                }
            }

            updateStrengthSlot(playerDTO.playerStrength)
            updateSwiftnessSlot(playerDTO.playerSwiftness)
            updateBalanceSlot(playerDTO.playerBalance)
            updateConcentrationSlot(playerDTO.playerConcentration)
            updateRemainSlot(playerDTO.playerStatusRemain)

            onClose { event ->
                //player.sendMessage(event.reason.toString())
                if(event.reason != InventoryCloseEvent.Reason.PLUGIN) {
                    player.playSound(Sounds.uiClose)
                    manager.applyAll()
                }
            }
        }
    }

    private fun InvFrame.updateStrengthSlot(strength: Int) = this.strengthSlot {
        item = statusIcons[0].clone().apply {
            this.lore(texts(text("$strength")))
        }
    }
    private fun InvFrame.updateSwiftnessSlot(swiftness: Int) = this.swiftnessSlot {
        item = statusIcons[1].clone().apply {
            this.lore(texts(text("$swiftness")))
        }
    }
    private fun InvFrame.updateBalanceSlot(balance: Int) = this.balanceSlot {
        item = statusIcons[2].clone().apply {
            this.lore(texts(text("$balance")))
        }
    }
    private fun InvFrame.updateConcentrationSlot(concentration: Int) = this.concentrationSlot {
        item = statusIcons[3].clone().apply {
            this.lore(texts(text("$concentration")))
        }
    }

    private fun InvFrame.updateRemainSlot(remains: Int) = this.remainSlot {
        item = getRemainingStatusIcon(remains)
    }
}

private fun InvFrame.clearUpIcon() = run { for(i in 0..3) this.slot(2*i + 1, 0) { item = clear } }
private fun InvFrame.clearDownIcon() = run { for(i in 0..3) this.slot(2*i + 1, 2) { item = clear } }

/**
 * 0: str, 1: swt, 2: bal, 3: con
 */
private fun InvFrame.statusSlot(x: Int, init: InvSlot.() -> Unit) = this.slot(2*x + 1, 1, init)
private fun InvFrame.remainSlot(init: InvSlot.() -> Unit) = this.slot(8, 2, init)

private fun InvFrame.strengthSlot(init: InvSlot.() -> Unit) = this.slot(1, 1, init)
private fun InvFrame.swiftnessSlot(init: InvSlot.() -> Unit) = this.slot(3, 1, init)
private fun InvFrame.balanceSlot(init: InvSlot.() -> Unit) = this.slot(5, 1, init)
private fun InvFrame.concentrationSlot(init: InvSlot.() -> Unit) = this.slot(7, 1, init)