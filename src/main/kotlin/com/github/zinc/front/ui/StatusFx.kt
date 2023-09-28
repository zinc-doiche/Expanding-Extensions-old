package com.github.zinc.front.ui

import com.github.zinc.util.getCustomItem
import com.github.zinc.util.item
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object StatusFx {
    private val clear = ItemStack(Material.AIR)
    private val upIcon: ItemStack = getCustomItem(Material.PAPER, text("UP"), 1)

    private val statusIcons: List<ItemStack> = listOf(
        item(Material.RED_STAINED_GLASS_PANE, text("STRENGTH")),
        item(Material.LIGHT_BLUE_STAINED_GLASS_PANE, text("SWIFTNESS")),
        item(Material.LIME_STAINED_GLASS_PANE, text("BALANCE")),
        item(Material.YELLOW_STAINED_GLASS_PANE, text("CONCENTRATION"))
    )

    private fun getRemainingStatusIcon(remain: Int): ItemStack {
        return item(
            if (remain > 0) Material.GREEN_STAINED_GLASS_PANE
            else Material.RED_STAINED_GLASS_PANE
        ) { meta ->
            meta.displayName(text("잔여스탯: $remain"))
            meta.setCustomModelData(7)
        }
    }

//    fun getStatusFrame(playerData: PlayerData): InvFrame {
//        val manager = playerData.manager!!
//
//        return InvFX.frame(3, Component.text("${playerData.playerVO.playerName}의 스테이터스")) {
//            for (i in 0..3) {
//                slot(2 * i + 1, 0) {
//                    if (manager.hasRemain() && manager.getTotalStatus() <= 600) {
//                        item = upIcon
//
//                        onClick {
//                            manager.updateStatus(StatusType.values()[i], 1)
//                            manager.updateStatus(StatusType.REMAIN, -1)
//                            manager.playerEntity.playSound(Sounds.click)
//                            manager.playerEntity.openFrame(getStatusFrame(playerData))
//                        }
//                    } else item = clear
//                }
//            }
//
//            updateStrengthSlot(playerData.playerVO.playerStrength)
//            updateSwiftnessSlot(playerData.playerVO.playerSwiftness)
//            updateBalanceSlot(playerData.playerVO.playerBalance)
//            updateConcentrationSlot(playerData.playerVO.playerConcentration)
//            updateRemainSlot(playerData.playerVO.playerStatusRemain)
//
//            onClose { event ->
//                //player.sendMessage(event.reason.toString())
//                if (event.reason != InventoryCloseEvent.Reason.PLUGIN) {
//                    manager.playerEntity.playSound(Sounds.uiClose)
//                    manager.applyAll()
//                }
//            }
//        }
//    }
//
//    private fun InvFrame.updateStrengthSlot(strength: Int) = this.strengthSlot {
//        item = statusIcons[0].clone().apply {
//            this.lore(texts(text("$strength")))
//        }
//    }
//    private fun InvFrame.updateSwiftnessSlot(swiftness: Int) = this.swiftnessSlot {
//        item = statusIcons[1].clone().apply {
//            this.lore(texts(text("$swiftness")))
//        }
//    }
//    private fun InvFrame.updateBalanceSlot(balance: Int) = this.balanceSlot {
//        item = statusIcons[2].clone().apply {
//            this.lore(texts(text("$balance")))
//        }
//    }
//    private fun InvFrame.updateConcentrationSlot(concentration: Int) = this.concentrationSlot {
//        item = statusIcons[3].clone().apply {
//            this.lore(texts(text("$concentration")))
//        }
//    }
//    private fun InvFrame.updateRemainSlot(remains: Int) = this.remainSlot {
//        item = getRemainingStatusIcon(remains)
//    }
}
//private fun InvFrame.remainSlot(init: InvSlot.() -> Unit) = this.slot(8, 2, init)
//private fun InvFrame.strengthSlot(init: InvSlot.() -> Unit) = this.slot(1, 1, init)
//private fun InvFrame.swiftnessSlot(init: InvSlot.() -> Unit) = this.slot(3, 1, init)
//private fun InvFrame.balanceSlot(init: InvSlot.() -> Unit) = this.slot(5, 1, init)
//private fun InvFrame.concentrationSlot(init: InvSlot.() -> Unit) = this.slot(7, 1, init)