package com.github.zinc.mongodb

import com.google.gson.Gson
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import kotlin.reflect.KClass

internal val gson: Gson = Gson()

internal fun Document(init: Document.() -> Unit): Document = Document().apply(init)

internal fun <T : Any> Document.toObject(kclass: KClass<T>): T = gson.fromJson(toJson(), kclass.java)

internal fun toDocument(any: Any): Document = Document.parse(gson.toJson(any))

internal fun MongoCollection<Document>.findOne(key: String, value: Any): Document? = find(Filters.eq(key, value)).first()

/**
 * @param from collection name to join
 * @param localField local field name to join
  * @param foreignField foreign field name to join
 * @param as alias name
 */
internal fun lookup(from: String, localField: String, foreignField: String, `as`: String): Document {
    return Document().apply {
        put("from", from)
        put("localField", localField)
        put("foreignField", foreignField)
        put("as", `as`)
    }
}

internal fun set(vararg pairs: Pair<String, Any>): Document {
    return Document("\$set", Document {
        pairs.forEach { (key, value) -> put(key, value) }
    })
}

