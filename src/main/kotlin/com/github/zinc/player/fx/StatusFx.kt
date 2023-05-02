package com.github.zinc.player.fx

import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.fx.StatusFx.clear
import com.github.zinc.player.manager.PlayerStatusManager
import com.github.zinc.util.extension.*
import com.github.zinc.util.extension.getCustomItem
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.text
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.frame.InvSlot
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object StatusFx {
    val clear = ItemStack(Material.AIR)
    private val upIcon: ItemStack = getCustomItem(Material.PAPER, "UP", 1)
    private val downIcon: ItemStack = getCustomItem(Material.PAPER, "DOWN", 2)

    private val statusIcons: List<ItemStack> = listOf(
        getCustomItem(Material.PAPER, "STRENGTH", 3),
        getCustomItem(Material.PAPER, "SWIFTNESS", 4),
        getCustomItem(Material.PAPER, "BALANCE", 5),
        getCustomItem(Material.PAPER, "CONCENTRATION", 6)
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

        val str = playerDTO.playerStrength
        val swt = playerDTO.playerSwiftness
        val bal = playerDTO.playerBalance
        val con = playerDTO.playerConcentration

        return InvFX.frame(4, Component.text("${player.name}의 스테이터스")) {
            for (i in 0..3){
                slot(2*i + 1, 0) {
                    item = if(playerDTO.hasRemain()) upIcon else clear
                    onClick {
                        if(!playerDTO.hasRemain()) return@onClick

                        this@frame.remainSlot {
                            item = getRemainingStatusIcon(--playerDTO.playerStatusRemain)
                        }
                        when(i) {
                            0 -> this@frame.updateStrengthSlot(++playerDTO.playerStrength)
                            1 -> this@frame.updateSwiftnessSlot(++playerDTO.playerSwiftness)
                            2 -> this@frame.updateBalanceSlot(++playerDTO.playerBalance)
                            3 -> this@frame.updateConcentrationSlot(++playerDTO.playerConcentration)
                        }

                        if(!playerDTO.hasRemain()) this@frame.clearUpIcon()
                    }
                }
                slot(2*i + 1, 2) {
                    item = downIcon //temp
                    onClick {
                        /* TODO :
                            1) 스텟롤백 테이블 만들기
                            2) 1에서 롤백가능횟수 가져오기
                            3) 잘 코딩하기
                         */
                    }
                }
            }

            updateStrengthSlot(str)
            updateSwiftnessSlot(swt)
            updateBalanceSlot(bal)
            updateConcentrationSlot(con)

            updateRemainSlot(playerDTO.playerStatusRemain)
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
private fun InvFrame.remainSlot(init: InvSlot.() -> Unit) = this.slot(8, 3, init)

private fun InvFrame.strengthSlot(init: InvSlot.() -> Unit) = this.slot(1, 1, init)
private fun InvFrame.swiftnessSlot(init: InvSlot.() -> Unit) = this.slot(3, 1, init)
private fun InvFrame.balanceSlot(init: InvSlot.() -> Unit) = this.slot(5, 1, init)
private fun InvFrame.concentrationSlot(init: InvSlot.() -> Unit) = this.slot(7, 1, init)