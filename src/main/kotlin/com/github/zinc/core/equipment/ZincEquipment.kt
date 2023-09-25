package com.github.zinc.core.equipment

import com.github.zinc.core.player.PlayerData
import com.github.zinc.module.user.`object`.Status
import com.github.zinc.util.getPersistent
import com.github.zinc.util.hasPersistent
import com.github.zinc.util.setPersistent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


open class ZincEquipment(
    override var item: ItemStack,
    override val status: Status = Status(),
    override var level: Int = 0
): Equipment {

    override fun isDeserved(playerData: PlayerData): Boolean {
        return playerData.playerVO.playerStrength >= status.strength &&
                playerData.playerVO.playerSwiftness >= status.swiftness &&
                playerData.playerVO.playerBalance >= status.balance &&
                playerData.playerVO.playerConcentration >= status.concentration &&
                playerData.playerVO.playerLevel >= level
    }

    // ordered.
    fun setAll() {
        setStatus()
        setPDC()
        setLore()
    }

    override fun setStatus() {
        level = 0
        status.setStatus(item)
        if(item.hasPersistent(LEVEL_CONSTRAINT_KEY))
            level = item.getPersistent(LEVEL_CONSTRAINT_KEY, PersistentDataType.INTEGER)!!
    }

    override fun setPDC() {
        item.setPersistent(STRENGTH, status.strength, PersistentDataType.INTEGER)
        item.setPersistent(SWIFTNESS, status.swiftness, PersistentDataType.INTEGER)
        item.setPersistent(BALANCE, status.balance, PersistentDataType.INTEGER)
        item.setPersistent(CONCENTRATION, status.concentration, PersistentDataType.INTEGER)
    }

    override fun setLore() { item.editMeta { it.lore(getLore()) } }

    override fun toString(): String {
        return "ZincEquipment(item=$item, status=$status, level=$level)"
    }
}