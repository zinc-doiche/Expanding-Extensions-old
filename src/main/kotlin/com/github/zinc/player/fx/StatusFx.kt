package com.github.zinc.player.fx

import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.player.domain.StatusType
import com.github.zinc.player.manager.PlayerStatusManager
import com.github.zinc.util.component.getCustomItem
import com.github.zinc.util.component.item
import com.github.zinc.util.component.text
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.frame.InvSlot
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object StatusFx {
    private val clear = ItemStack(Material.AIR)
    private val upIcon: ItemStack = getCustomItem(Material.PAPER, "UP", 1)
    private val downIcon: ItemStack = getCustomItem(Material.PAPER, "DOWN", 2)

    private val statusIcons: List<ItemStack> = listOf(
        getCustomItem(Material.PAPER, "STRENGTH", 3), getCustomItem(Material.PAPER, "SWIFTNESS", 4),
        getCustomItem(Material.PAPER, "BALANCE", 5), getCustomItem(Material.PAPER, "CONCENTRATION", 6)
    )

    private fun getRemainingStatusIcon(player: PlayerDTO): ItemStack = item(Material.BLACK_STAINED_GLASS_PANE) { meta ->
        meta.displayName(text("잔여스탯: ${player.playerStatusRemain}"))
        //meta.setCustomModelData(2)
    }

    fun getStatusFrame(player: Player): InvFrame {
        val playerDTO = PlayerContainer[player.name]!!
        val manager = PlayerStatusManager(playerDTO)

        val str = playerDTO.playerStrength
        val swt = playerDTO.playerSwiftness
        val bal = playerDTO.playerBalance
        val con = playerDTO.playerConcentration

        return InvFX.frame(4, Component.text("${player.name}의 스테이터스")) {
            if(playerDTO.playerStatusRemain > 0) for (i in 0..3) {
                slot(2 * i + 1, 0) {
                    item = upIcon

                    onClick {
                        if(--playerDTO.playerStatusRemain <= 0) {
                            playerDTO.playerStatusRemain = 0
                            item = clear
                            return@onClick
                        }
                        when(i) {
                            0 -> playerDTO.playerStrength++
                            1 -> playerDTO.playerSwiftness++
                            2 -> playerDTO.playerBalance++
                            3 -> playerDTO.playerConcentration++
                        }
                        this@frame.remainSlot {
                            item?.editMeta { it.displayName(text("${playerDTO.playerStatusRemain}")) } ?: return@remainSlot
                        }
                    }
                }
            }
            for (i in 0..3) {
                slot(2*i+1, 1) {
                    item = statusIcons[i]
                }
            }
//          if(playerDTO.playerStatusRemain > 0) for (i in 0..3) {
//                slot(2 * i + 1, 0) {
//                    item = upIcon
//
//                    onClick {
//                        if(--playerDTO.playerStatusRemain <= 0) {
//                            playerDTO.playerStatusRemain = 0
//                            item = clear
//                        }
//                        when(i) {
//                            0 -> playerDTO.playerStrength++
//                            1 -> playerDTO.playerSwiftness++
//                            2 -> playerDTO.playerBalance++
//                            3 -> playerDTO.playerConcentration++
//                        }
//                    }
//                }
//            }
            remainSlot {
                item = getRemainingStatusIcon(playerDTO)
            }
            onClose {
                if(str != playerDTO.playerStrength) manager.applyStatus(StatusType.STRENGTH)
                if(swt != playerDTO.playerSwiftness) manager.applyStatus(StatusType.SWIFTNESS)
                if(bal != playerDTO.playerBalance) manager.applyStatus(StatusType.BALANCE)
                if(con != playerDTO.playerConcentration) manager.applyStatus(StatusType.CONCENTRATION)
            }
        }
    }

    fun open() {

    }
}

private fun  InvFrame.remainSlot(init: InvSlot.() -> Unit) = this.slot(8, 3, init)