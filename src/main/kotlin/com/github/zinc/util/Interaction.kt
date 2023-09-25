package com.github.zinc.util

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.InventoryView

/**
 * [GET] 아이템을 가져갈 때: inv!!, cursor == null or air
 *
 * [EXCHANGE] 아이템을 맞바꿀 때: inv!!, cursor!!
 *
 * [PUT] 아이템을 놓을 때: inv == null or air, cursor!!
 */
enum class Interaction {
    GET,
    EXCHANGE,
    PUT;

    companion object {
        fun get(view: InventoryView, slot: Int): Interaction? {
            return if(isNullOrAir(view.getItem(slot))) {
                if(isNullOrAir(view.cursor)) null
                else PUT
            } else {
                if(isNullOrAir(view.cursor)) GET
                else EXCHANGE
            }
        }

        /**
         * called in the [InventoryClickEvent], executes one interaction among [Interaction].
         *
         * this method will force interactions not previously done.
         */
        fun doInteraction(interaction: Interaction, view: InventoryView, slot: Int, isShiftClick: Boolean) {
            if(isShiftClick) when(interaction) {
                PUT -> return
                else -> {
                    if(view.bottomInventory.isFull()) return
                    view.bottomInventory.addItem(view.getItem(slot)!!)
                }
            } else when(interaction) {
                EXCHANGE -> {
                    val temp = view.cursor
                    view.setCursor(view.getItem(slot))
                    view.setItem(slot, temp)
                }
                PUT -> view.setItem(slot, view.cursor)
                GET -> view.setCursor(view.getItem(slot))
            }
        }

        fun doSlotInteraction(view: InventoryView, slot: Int, isShiftClick: Boolean) {
            doInteraction(Interaction.get(view, slot) ?: return, view, slot, isShiftClick)
        }
    }
}

