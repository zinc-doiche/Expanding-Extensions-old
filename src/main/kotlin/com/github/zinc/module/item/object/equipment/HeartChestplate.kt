package com.github.zinc.module.item.`object`.equipment

import com.github.zinc.module.item.`object`.OnHitDetection
import com.github.zinc.util.*
import com.github.zinc.util.item
import com.github.zinc.util.list
import com.github.zinc.util.plain
import com.github.zinc.util.setPersistent
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.TextDecoration
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

class HeartChestplate: Equipment, OnHitDetection {
    override val name = "heart_chestplate"
    override val item: ItemStack
        get() = item(
            Material.DIAMOND_CHESTPLATE,
            text("바다의 심장 다이아몬드 흉갑").color(NamedTextColor.BLUE).bold().noItalic(),
            list(
                empty(),
                text("보호됨", GRAY).noItalic().bold(),
                plain("피격 시:").bold()
                    .append(text(" 3초간 재생 II 획득 (10초)").color(GRAY).noBold()))
        ) { meta ->
            meta.setPersistent(Equipment.namespace, name)
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier)
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughnessModifier)
        }

    private val armorModifier: AttributeModifier
        get() = AttributeModifier(UUID.nameUUIDFromBytes(name.toByteArray()),
            name, 9.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST)
    private val armorToughnessModifier: AttributeModifier
        get() = AttributeModifier(UUID.nameUUIDFromBytes((name + "tough").toByteArray()),
            name, 2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST)

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