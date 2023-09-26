package com.github.zinc.mongodb

import com.google.gson.Gson
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import kotlin.reflect.KClass

internal val gson: Gson = Gson()

internal fun <T : Any> Document.toObject(kclass: KClass<T>): T = gson.fromJson(toJson(), kclass.java)

internal fun toDocument(any: Any): Document = Document.parse(gson.toJson(any))

internal fun MongoCollection<Document>.findOne(key: String, value: Any): Document? = find(Filters.eq(key, value)).first()
