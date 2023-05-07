package com.github.zinc.quest.manager

import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.quest.dao.QuestDAO
import com.github.zinc.util.scheduler.loop
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * 일일 퀘스트 = 기본 퀘스트 + 한정 퀘스트
 */
object QuestManager {
                            //name, require, reward
    private val quests: Map<String, Pair<Int, Int>> = hashMapOf(
        // Raiders -> TODO 레이드 성공시 각각 3000xp.
//        "Pillager" to Pair(15, 300),
//        "Ravager" to Pair(3, 650),
//        "Vindicator" to Pair(15, 500),
//        "Illusioner" to Pair(10, 250),
//        "Evoker" to Pair(7, 400),
//        "Vex" to Pair(15, 300),
    )

    private val dailyQuests: Map<String, Pair<Int, Int>> = hashMapOf(
        //OverWorld - 기본 퀘스트
        "Phantom" to Pair(10, 70),
        "Zombie" to Pair(30, 70),
        "Creeper" to Pair(30, 100),
        "Skeleton" to Pair(30, 100),
        "Drowned" to Pair(30, 70),
        "Spider" to Pair(30, 50),

        //Nether - daily
        "Blaze" to Pair(30, 500),
        "WitherSkeleton" to Pair(30, 500),
        "PigZombie" to Pair(20, 150),

        //Ender
        "Shulker" to Pair(20, 2500),

        //Boss
        "Wither" to Pair(3, 5000),
        "Warden" to Pair(1, 30000)
    )

    private val randomQuests:  Map<String, Pair<Int, Int>> = hashMapOf(
        // 3개 랜덤 - 한정 퀘스트
        "Slime" to Pair(20, 30),
        "Enderman" to Pair(30, 500),
        "Guardian" to Pair(20, 300),
        "Endermite" to Pair(10, 100),
        "Silverfish" to Pair(10, 90),
        "Stray" to Pair(20, 100),
        "Husk" to Pair(20, 70),
        "ZombieVillager" to Pair(20, 50),
        "CaveSpider" to Pair(20, 100),
        "ElderGuardian" to Pair(3, 700),
        "Witch" to Pair(5, 200),

        // random
        "Hoglin" to Pair(15, 100),
        "MagmaCube" to Pair(20, 50),
        "Ghast" to Pair(10, 150),
        "Zoglin" to Pair(20, 80),
        "Piglin" to Pair(20, 80),
        "PiglinBrute" to Pair(10, 150),
    )

    val clearMap: HashMap<String, HashSet<String>> = hashMapOf()
    private val formatter = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
    private const val START_TIME = "09:20:00"
    private const val START_MILLISECONDS = 1000L * 60L * 60L * 9L + 1000L * 60L * 20L
    private const val UPDATE_PERIOD = 1000L * 60L * 60L * 24L

    fun registerAllQuestList() {
        QuestDAO().use { dao ->
            dailyQuests.forEach { dao.registerQuest(it.key, it.value.first, it.value.second, "daily") }
            randomQuests.forEach { dao.registerQuest(it.key, it.value.first, it.value.second, "limit") }
        }
    }

    fun registerAllQuests(playerDTO: PlayerDTO) {
        QuestDAO().use { dao ->
            dailyQuests.keys.forEach { dao.insert(playerDTO.playerId, it) }
            randomQuests.keys.toMutableList().run {
                this.shuffle()
                this.subList(0, 5).forEach { dao.insert(playerDTO.playerId, it) }
            }
        }
    }

    fun appendQuestUpdater() {
        val currentTime = formatter.parse(formatter.format(System.currentTimeMillis()))
        val startTime = formatter.parse(START_TIME)
        val difference = startTime.time - currentTime.time

        val delay = if(difference < 0) abs(difference) + START_MILLISECONDS else difference

        loop(delay, UPDATE_PERIOD) {
            QuestDAO().use(QuestDAO::resetAll)
        }
    }
}