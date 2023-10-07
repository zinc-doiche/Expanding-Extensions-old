package com.github.zinc.module.user.util

import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.user.`object`.User
import com.github.zinc.mongodb.toObject
import org.bson.Document

//for save
internal fun User.toDocument(): Document {
    val document = com.github.zinc.mongodb.toDocument(this)
    val trinketMap = HashMap<String, String>()
    trinkets.forEach { (slot, trinket) -> trinketMap[slot.name] = trinket.name }
    document["trinkets"] = trinketMap
    document.remove("questRegistries")
    return document
}

//for load
internal fun Document.toUser(): User {
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