package com.github.zinc.module.user.listener

import com.github.zinc.lib.constant.Sounds
import com.github.zinc.lib.event.*
import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.user.`object`.User
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.mongodb.findOne
import com.github.zinc.mongodb.toDocument
import com.github.zinc.mongodb.toObject
import com.github.zinc.plugin
import com.github.zinc.util.*
import com.github.zinc.util.AIR
import com.github.zinc.util.isNull
import com.mongodb.client.model.Filters
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bson.Document
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.player.*
import java.time.Duration

class UserListener: Listener {
    @EventHandler
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        try {
            val collection = MongoDB["user"]
            val uuid = event.uniqueId.toString()
            val user: User = collection.findOne("uuid", uuid)?.toUser() ?: User(uuid).apply {
                collection.insertOne(this@apply.toDocument())
            }
            User[uuid] = user
        } catch (e: Exception) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, text("정보를 불러오는 데에 실패했어요.."))
            plugin.slF4JLogger.error("Failed to login: ${event.playerProfile.name}", e)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val user = User[event.player] ?: return
        user.status.applyStatus(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        HeartbeatScope().async {
            try {
                val uuid = event.player.uniqueId.toString()
                val user = User[uuid] ?: return@async
                MongoDB["user"].replaceOne(Filters.eq("uuid", uuid), user.toDocument())
                User.remove(uuid)
            } catch (e: Exception) {
                plugin.slF4JLogger.error("Failed to save user data: ${event.player.name}", e)
            }
        }
    }

    @EventHandler
    fun onUserLevelUp(event: AsyncUserLevelUpEvent) {
        val player = event.user.player ?: return
        event.user.status.addRemains(1)
        player.sendMessage(text("레벨 업!").decoration(TextDecoration.BOLD, true)
                .append(text(" (잔여 스텟 +1)", NamedTextColor.GRAY, TextDecoration.ITALIC)))

        if(event.user.level.level % 50 == 0) {
            player.playSound(Sounds.CHALLENGE_COMPLETED)
            player.showTitle(Title.title(
                text(event.user.level.level, GREEN, TextDecoration.BOLD),
                empty(),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(2))))
        } else {
            player.playSound(Sounds.LEVEL_UP)
        }
    }

    /**
     * 1. 직접 인챈트시
     * 2. 모루 사용시
     * 3. 숫돌로 인챈트 롤백시
     * 4. 도구 조합으로 인첸트 롤백시
     */
    @EventHandler
    fun onLevelUp(e: PlayerLevelChangeEvent) {

    }

    @EventHandler
    fun onInvClick(e: InventoryClickEvent) {
        if(e.inventory != e.view.topInventory) return

        when(e.inventory.type) {
            InventoryType.SMITHING -> {
                if(e.rawSlot != 2 || isNull(e.currentItem) || !isNull(e.cursor)) return
                Interaction.doInteraction(Interaction.GET, e.view, e.rawSlot, e.isShiftClick)
                e.inventory.setItem(0, AIR)
                e.inventory.getItem(1)?.subtract() ?: return
            }
            else -> return
        }
    }

    @EventHandler
    fun onPrepare(e: PrepareSmithingEvent) {
        val origin = e.inventory.getItem(0) ?: return
        val ingredient = e.inventory.getItem(1) ?: return

//        for (recipe in Recipes.customRecipes) {
//            if(recipe.isCorrect(origin, ingredient)) {
//                e.result = recipe.getResult(origin).apply {
//                    if(hasPersistent(DynamicRecipe.dynamicKey)) editMeta { it.displayName(displayName()) }
//                }
//                break
//            }
//        }
    }
}

//for save
fun User.toDocument(): Document {
    val document = toDocument(this)
    val trinketMap = HashMap<String, String>()
    trinkets.forEach { (slot, trinket) -> trinketMap[slot.name] = trinket.name }
    document["trinkets"] = trinketMap
    return document
}

//for load
fun Document.toUser(): User {
    val trinkets = get("trinkets") as Document
    remove("trinkets")
    val user = toObject(User::class)
    user.init()
    trinkets.values.forEach { name ->
        name as String
        val trinket = Trinket[name] ?: return@forEach
        user.setTrinket(trinket)
    }
    return user
}