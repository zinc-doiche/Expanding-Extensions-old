package com.github.zinc.module.item.`object`.equipment

import com.github.zinc.module.item.`object`.OnHitDetection
import com.github.zinc.util.*
import com.github.zinc.util.item
import com.github.zinc.util.list
import com.github.zinc.util.plain
import com.github.zinc.util.setPersistent
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class HeartChestPlate: Equipment, OnHitDetection {
    override val name = "heart_chest_plate"
    override val item: ItemStack
        get() = item(
            Material.DIAMOND_CHESTPLATE,
            plain("바다의 심장 다이아몬드 흉갑"),
            list(
                text("보호됨", GRAY).noItalic().bold(),
                empty(),
                plain("피격 시:").append(text("3초간 재생 I 획득 (대기시간 10s)").color(GRAY).noItalic()))
        ) { meta ->
            meta.setPersistent(Equipment.namespace, name)
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier)
        }

    val modifier: AttributeModifier
        get() = AttributeModifier(UUID.nameUUIDFromBytes(name.toByteArray()),
            name, 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST)

    override fun onEquip(player: Player) {
        users.add(player.uniqueId.toString())
    }

    override fun onTakeOff(player: Player) {
        users.remove(player.uniqueId.toString())
    }

    override fun onCloseHitDetection(damager: LivingEntity, player: Player, event: EntityDamageByEntityEvent) {
        onHitDetection(player)
    }

    override fun onLongHitDetection(damager: LivingEntity, player: Player, event: EntityDamageByEntityEvent) {
        onHitDetection(player)
    }

    private fun onHitDetection(player: Player) {
        val uuid = player.uniqueId.toString()
        if(uuid in cooldowns) {
            return
        }
        cooldowns.add(uuid)
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 1))
        async(20 * 10) {
            cooldowns.remove(uuid)
        }
    }

    companion object {
        private val users = HashSet<String>()
        private val cooldowns = HashSet<String>()
        operator fun contains(uuid: String): Boolean = uuid in users
    }
}