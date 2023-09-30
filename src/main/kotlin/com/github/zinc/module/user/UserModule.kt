package com.github.zinc.module.user

import com.github.zinc.module.Module
import com.github.zinc.module.user.gui.StatusGUI
import com.github.zinc.module.user.listener.UserListener
import com.github.zinc.module.user.`object`.StatusType
import com.github.zinc.module.user.`object`.User
import com.github.zinc.module.user.`object`.user
import com.github.zinc.plugin
import com.github.zinc.util.warn
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import java.util.*

class UserModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("status") {
                executes { StatusGUI(player.uniqueId.toString()).open() }
                then("add", "status" to dynamicByEnum(EnumSet.allOf(StatusType::class.java)), "amount" to int()) {
                    requires { isOp }
                    executes {
                        val status: StatusType by it
                        val amount: Int by it
                        val user = User[player] ?: return@executes
                        with(user.status) {
                            if(checkAddition(status, amount)) {
                                val afterAddition: Int = addStatus(status, amount)
                                applyStatus(player)
                                player.sendMessage("현재 ${status.korName}: $afterAddition")
                            } else {
                                player.warn("0 이하로 줄일 수 없습니다. (현재 ${status.korName}: ${get(status)})")
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

            register("exp", "경험치") {
                then("add", "amount" to long()) {
                    requires { isOp }
                    executes {
                        val amount: Long by it
                        val user = User[player] ?: return@executes
                        user.level.addExperience(amount)
                    }
                }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(UserListener(), plugin)
        }
    }
}