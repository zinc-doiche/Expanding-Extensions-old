package com.github.zinc.module.user.`object`

class Trinket(
    val name: String,
    val slot: TrinketSlot
) {

}

enum class TrinketSlot {
    RING,
    EARRING,
    NECKLACE,
    BRACELET,
    BELT,
    SHOES
}