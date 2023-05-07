package com.github.zinc

import com.github.zinc.quest.manager.QuestManager
import com.github.zinc.util.extension.text
import com.github.zinc.util.scheduler.loop
import org.bukkit.entity.Player
//import io.github.monun.kommand.kommand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.server.ServerLoadEvent

class TaskManager: Listener {

    @EventHandler
    fun onLoaded(e: ServerLoadEvent) {
        loop(0L, 20L * 60 * 10) {
            if(updatedTasks.isNotEmpty()) updatedTasks.forEach { it.value() }
        }
//        plugin.kommand {
//            register("status", "스테이터스") {
//                then("open") {
//                    executes {
//                        player.openFrame(StatusFx.getStatusFrame(player))
//                    }
//                }
//                then("view") {
//                    executes {
//                        val playerDTO = PlayerContainer[player.name]!!
//                        player.sendMessage(
//                            "${player.name}의 스테이터스 :\n" +
//                            "| Strength: ${playerDTO.playerStrength}\n" +
//                            "| Swiftness: ${playerDTO.playerSwiftness}\n" +
//                            "| Balance: ${playerDTO.playerBalance}\n" +
//                            "| Concentration: ${playerDTO.playerConcentration}\n"
//                        )
//                    }
//                }
//                then("add") {
//                    then("str") {
//                        then("amount" to int(0)) {
//                            requires { player.isOp }
//                            executes {
//                                val playerDTO = PlayerContainer[player.name]!!
//                                val amount: Int = it["amount"]
//                                playerDTO.playerStrength += amount
//                            }
//                        }
//                    }
//                    then("swt") {
//                        then("amount" to int(0)) {
//                            requires { player.isOp }
//                            executes {
//                                val playerDTO = PlayerContainer[player.name]!!
//                                val amount: Int = it["amount"]
//                                playerDTO.playerSwiftness += amount
//                            }
//                        }
//                    }
//                    then("bal") {
//                        then("amount" to int(0)) {
//                            requires { player.isOp }
//                            executes {
//                                val playerDTO = PlayerContainer[player.name]!!
//                                val amount: Int = it["amount"]
//                                playerDTO.playerBalance += amount
//                            }
//                        }
//                    }
//                    then("con") {
//                        then("amount" to int(0)) {
//                            requires { player.isOp }
//                            executes {
//                                val playerDTO = PlayerContainer[player.name]!!
//                                val amount: Int = it["amount"]
//                                playerDTO.playerConcentration += amount
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    companion object {
        private val updatedTasks = HashMap<String, () -> Unit>()

        fun add(key: String, task: () -> Unit) { updatedTasks[key] = task }
        fun remove(key: String) { updatedTasks.remove(key) }
    }
}