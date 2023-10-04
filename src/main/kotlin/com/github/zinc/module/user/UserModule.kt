package com.github.zinc.module.user

import com.github.zinc.module.Module
import com.github.zinc.module.user.gui.StatusGUI
import com.github.zinc.module.user.listener.UserDamageListener
import com.github.zinc.module.user.listener.UserListener
import com.github.zinc.module.user.listener.toDocument
import com.github.zinc.module.user.`object`.StatusType
import com.github.zinc.module.user.`object`.User
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.plugin
import com.github.zinc.util.warn
import com.mongodb.client.model.Filters
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.text

class UserModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("status", "스테이터스", "스탯") {
                executes { StatusGUI(player.uniqueId.toString()).open() }

                then("add", "status" to suggestion(StatusType.entries), "amount" to int()) {
                    requires { isOp }
                    executes {
                        val status: String by it
                        val statusType = StatusType.valueOf(status)
                        val amount: Int by it
                        val user = User[player] ?: return@executes
                        with(user.status) {
                            if(checkAddition(statusType, amount)) {
                                val afterAddition: Int = addStatus(statusType, amount)
                                applyStatus(player)
                                player.sendMessage("현재 ${statusType.korName}: $afterAddition")
                            } else {
                                player.warn("0 이하로 줄일 수 없습니다. (현재 ${statusType.korName}: ${get(statusType)})")
                            }
                        }
                    }
                }
                then("info") {
                    executes {
                        val user = User[player] ?: return@executes
                        val message = text("${player.name}의 스테이터스 :")
                            .appendNewline()
                            .append(text("[${user.level.level}Lv.] ${user.level.experience}xp / ${user.level.requiredExpForNextLevel}xp"))
                            .appendNewline()
                            .append(text("| 힘: ${user.status.strength}"))
                            .appendNewline()
                            .append(text("| 속도: ${user.status.swiftness}"))
                            .appendNewline()
                            .append(text("| 체력: ${user.status.balance}"))
                            .appendNewline()
                            .append(text("| 집중: ${user.status.concentration}"))
                            .appendNewline()
                            .append(text("잔여 스탯: ${user.status.remains}"))
                            .appendNewline()
                            .append(text("총 스탯: ${user.status.total}"))
                        player.sendMessage(message)
                    }
                }
            }
        }

        plugin.kommand {
            register("exp", "경험치") {
                then("add", "amount" to int()) {
                    requires { isOp }
                    executes {
                        val amount: Int by it
                        val user = User[player] ?: return@executes
                        user.level.addExperience(amount, user)
                    }
                }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(UserListener(), plugin)
            registerEvents(UserDamageListener(), plugin)
        }
    }

    override fun register() {
        super.register()
        HeartbeatScope().async {
            while (true) {
                //for 10m
                delay(1000 * 60 * 10)
                plugin.server.broadcast(text("유저 정보 저장 중..."))
                val start = System.currentTimeMillis()
                saveUsers()
                val end = System.currentTimeMillis()
                plugin.server.broadcast(text("저장 완료! (${end - start}ms)"))
            }
        }
    }

    override fun onDisable() {
        saveUsers()
    }

    private fun saveUsers() {
        val collection = MongoDB["user"]
        MongoDB.transaction {
            User.getUsers().forEach { user ->
                collection.replaceOne(Filters.eq("uuid", user.uuid), user.toDocument())
            }
        }
    }
}