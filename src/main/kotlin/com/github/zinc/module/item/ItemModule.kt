package com.github.zinc.module.item

import com.github.zinc.info
import com.github.zinc.module.Module
import com.github.zinc.module.item.`object`.listener.ItemListener
import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.item.`object`.trinket.TrinketSlot
import com.github.zinc.module.user.gui.TrinketGUI
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.plugin
import com.github.zinc.util.addItem
import com.github.zinc.util.isNull
import com.github.zinc.util.toJson
import com.github.zinc.util.warn
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bson.Document
import org.bson.types.ObjectId
import java.util.EnumSet

class ItemModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("trinket", "장신구", "트링켓") {
                //open
                executes { TrinketGUI(player.uniqueId.toString()).open() }

                then("get", "name" to string()) {
                    requires { player.isOp }
                    executes {
                        val name: String by it
                        val trinket = Trinket[name] ?: run {
                            player.warn("존재하지 않는 장신구입니다.")
                            return@executes
                        }
                        val item = trinket.item ?: return@executes
                        player.addItem(item)
                    }
                }
                then("add", "name" to string(), "slot" to dynamicByEnum(EnumSet.allOf(TrinketSlot::class.java))) {
                    requires { player.isOp }
                    executes {
                        val name: String by it
                        val slot: TrinketSlot by it
                        val item = player.inventory.itemInMainHand

                        if(name in Trinket) {
                            player.warn("이미 존재하는 이름입니다.")
                            return@executes
                        }
                        if(isNull(item)) {
                            player.warn("손에 아이템을 들고 있어야 합니다.")
                            return@executes
                        }

                        val trinket = Document(mapOf(
                            "_id" to ObjectId(),
                            "name" to name,
                            "slot" to slot.name,
                            "item" to item.toJson()
                        ))

                        MongoDB["trinket"].insertOne(trinket)
                    }
                }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager){
            registerEvents(ItemListener(), plugin)
        }
    }

    fun registerItems() {
        MongoDB["trinket"].find().forEach { document ->
            val _id = document.getObjectId("_id") ?: return@forEach
            val name = document.getString("name") ?: return@forEach
            val slot = TrinketSlot.valueOf(document.getString("slot") ?: return@forEach)
            val trinket = Class.forName(name)
                .asSubclass(Trinket::class.java)
                .getConstructor(ObjectId::class.java, String::class.java, TrinketSlot::class.java)
                .newInstance(_id, name, slot)
            info("Register trinket: $name")
            Trinket[name] = trinket
        }
    }
}