package com.github.zinc.core.player

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import kotlin.random.Random

class PlayerStatusManager(
    private val playerData: PlayerData,
    var playerEntity: Player
) {
    fun getTotalStatus(): Int {
        val str = playerData.playerVO.playerStrength
        val con = playerData.playerVO.playerConcentration
        val bal = playerData.playerVO.playerBalance
        val swt = playerData.playerVO.playerSwiftness
        val rem = playerData.playerVO.playerStatusRemain
        return str + con + bal + swt + rem
    }

    fun levelUp(amount: Int = 1) {
        playerData.playerVO.playerLevel += amount
        playerData.playerVO.playerStatusRemain += amount
    }

    fun setExp(amount: Int) {
        playerData.playerVO.playerExperience = amount
    }

    fun expUp(amount: Int) {
        playerData.playerVO.playerExperience += amount
    }

    fun rollCritical() = (Random.nextInt(100)+1) <= 100 * getAdditionalCriticalProbability(playerData.playerVO.playerConcentration)

    fun hasRemain() = playerData.playerVO.playerStatusRemain > 0

    fun applyAll() {
        applyStrength()
        applyBalance()
        applySwiftness()
        applyConcentration()
    }

    fun applyStatus(type: StatusType) {
        when(type) {
            StatusType.STRENGTH -> applyStrength()
            StatusType.BALANCE -> applyBalance()
            StatusType.SWIFTNESS -> applySwiftness()
            StatusType.CONCENTRATION -> applyConcentration()
            StatusType.REMAIN -> return
        }
    }

    private fun applyStrength() {
        val damageAttr = playerEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return
        damageAttr.baseValue = getAdditionalDamage(playerData.playerVO.playerStrength) + defaultDamage
    }

    private fun applyBalance() {
        val healthAttr = playerEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        healthAttr.baseValue = getAdditionalHealth(playerData.playerVO.playerBalance) + defaultHealth
        playerEntity.health = healthAttr.baseValue
    }

    private fun applySwiftness() {
        val speedAttr = playerEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return
        speedAttr.baseValue = getAdditionalSpeed(playerData.playerVO.playerSwiftness) + defaultSpeed
    }

    private fun applyConcentration() {
        //TODO CUSTOM
    }

    fun updateStatus(type: StatusType, amount: Int = 1): Int {
        return when(type) {
            StatusType.STRENGTH -> {
                playerData.playerVO.playerStrength += amount
                playerData.playerVO.playerStrength
            }
            StatusType.BALANCE -> {
                playerData.playerVO.playerBalance += amount
                playerData.playerVO.playerBalance
            }
            StatusType.SWIFTNESS -> {
                playerData.playerVO.playerSwiftness += amount
                playerData.playerVO.playerSwiftness
            }
            StatusType.CONCENTRATION -> {
                playerData.playerVO.playerConcentration += amount
                playerData.playerVO.playerConcentration
            }
            StatusType.REMAIN -> {
                playerData.playerVO.playerStatusRemain += amount
                playerData.playerVO.playerStatusRemain
            }
        }
    }

    fun getMaxExpForNextLevel(): Int {
        return when(playerData.playerVO.playerLevel){
            in 0..99 ->
                (-1/500.0)*playerData.playerVO.playerLevel*playerData.playerVO.playerLevel*(playerData.playerVO.playerLevel-150) + 100
            //1100
            in 100..199 ->
                (-1/50.0)*(playerData.playerVO.playerLevel-100)*(playerData.playerVO.playerLevel-100)*(playerData.playerVO.playerLevel-250) + 1100
            //11100
            in 200..299 ->
                (-1/5.0)*(playerData.playerVO.playerLevel-100)*(playerData.playerVO.playerLevel-100)*(playerData.playerVO.playerLevel-250) + 11100
            //111100
            else -> 0
        }.toInt()
    }

    companion object {
        const val defaultDamage = 1.0
        const val defaultHealth = 20.0
        const val defaultSpeed = 0.1
        const val defaultCriticalProbability = 0.0

        private const val INTERVAL1 = 150
        private const val INTERVAL2 = 250
        private const val INTERVAL3 = 400
        private const val INTERVAL4 = 550

        private fun getAdditionalDamage(strength: Int): Double {
            var damage = 0.0

            damage += when (strength) {
                in 0..INTERVAL1 -> //150
                    strength * 5.0 / INTERVAL1
                //max 6 = 1 + 5
                // 5/150 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    6 + (strength - INTERVAL1) * 2.0 / (INTERVAL2 - INTERVAL1)
                //max 8 = 1 + 5 + 2
                // 3/150 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    8 + (strength - INTERVAL2) * 1.0 / (INTERVAL3 - INTERVAL2)
                //max 9 = 1 + 5 + 2 + 1
                // 1/150 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    9 + (strength - INTERVAL3) * 2.0 / (INTERVAL4 - INTERVAL3)
                //max 11 = 1 + 5 + 2 + 1 + 2
                // 3/150 ps
                else -> //600
                    11 + (strength - INTERVAL4) * 4.0 / (600 - INTERVAL4)
                //max 15 = 1 + 5 + 2 + 1 + 2 + 4
                // 12/150 ps
            }
            return damage
        }

        private fun getAdditionalHealth(balance: Int): Double {
            var health = 0.0

            health += when(balance) {
                in 0..INTERVAL1 -> //150
                    balance * 10.0 / INTERVAL1
                //max 30 = 20 + 10
                // 10/150 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    10 + (balance - INTERVAL1) * 6.0 / (INTERVAL2 - INTERVAL1)
                //max 36 = 20 + 10 + 6
                // 9/150 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    16 + (balance - INTERVAL2) * 4.0 / (INTERVAL3 - INTERVAL2)
                //max 40 = 20 + 10 + 6 + 4
                // 4/150 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    20 + (balance - INTERVAL3) * 6.0 / (INTERVAL4 - INTERVAL3)
                //max 46 = 20 + 10 + 6 + 4 + 6
                // 6/150 ps
                else -> //600
                    26 + (balance - INTERVAL4) * 14.0 / (600 - INTERVAL4)
                //max 60 = 20 + 10 + 6 + 4 + 6 + 14
                // 42/150 ps
            }
            return health
        }

        private fun getAdditionalSpeed(swiftness: Int): Double {
            var speed = 0.0

            speed += when(swiftness) {
                in 0..INTERVAL1 -> //150
                    swiftness * .04 / INTERVAL1
                //max 0.14 = 1 + .4
                // 0.04/150 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    .14 + (swiftness - INTERVAL1) * .02 / (INTERVAL2 - INTERVAL1)
                //max 0.16 = 0.1 + .04 + .02
                // 0.03/150 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    .16 + (swiftness - INTERVAL2) * .02 / (INTERVAL3 - INTERVAL2)
                //max 0.18 = 0.1 + .04 + .02 + .02
                // 0.02/150 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    .18 + (swiftness - INTERVAL3) * .04 / (INTERVAL4 - INTERVAL3)
                //max 0.22 = 0.1 + .04 + .02 + .02 + .04
                // 0.04/150 ps
                else -> //600
                    .22 + (swiftness - INTERVAL4) * .08 / (600 - INTERVAL4)
                //max 0.3 = 0.1 + .04 + .02 + .02 + .04 + .08
                // 0.24/150 ps
            }
            return speed
        }

        //활이 메인딜
        private fun getAdditionalCriticalProbability(concentration: Int): Double {
            var criticalProbability = 0.0

            criticalProbability += when(concentration) {
                in 0..INTERVAL1 -> //150
                    concentration * .2 / INTERVAL1
                //max 0.2 = 0 + .2
                // 20/15000 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    .2 + (concentration - INTERVAL1) * .05 / (INTERVAL2 - INTERVAL1)
                //max 0.25 = 0 + .2 + .05
                // 5/15000 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    .25 + (concentration - INTERVAL2) * .05 / (INTERVAL3 - INTERVAL2)
                //max 0.3 = 0 + .2 + .05 +.05
                // 5/15000 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    .3 + (concentration - INTERVAL3) * .1 / (INTERVAL4 - INTERVAL3)
                //max 0.4 = 0 + .2 + .05 + .05 + .1
                // 10/15000 ps
                else -> //600
                    .4 + (concentration - INTERVAL4) * .6 / (600 - INTERVAL4)
                //max 1 = 0 + .2 + .05 + .05 + .1 + .6
                // 60/15000 ps
            }
            return criticalProbability
        }
    }
}