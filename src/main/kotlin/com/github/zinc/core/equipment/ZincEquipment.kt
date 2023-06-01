package com.github.zinc.core.equipment

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ZincEquipment(
    private var equipmentType: Material,
    var constraintStatus: Status
) {
    constructor(equipment: ItemStack) : this(equipment.type, getConstrainStatus(equipment))

    fun getConstrainStatus(itemStack: ItemStack): Status{
        return Status(1, 2, 3, 4)
    }
}
