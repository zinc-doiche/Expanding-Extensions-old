package com.github.zinc.util

import com.google.gson.Gson
import org.bson.Document
import kotlin.reflect.KClass

internal val gson: Gson = Gson()

internal fun <T : Any> Document.toObject(kclass: KClass<T>): T = gson.fromJson(toJson(), kclass.java)

internal fun toDocument(any: Any): Document = Document.parse(gson.toJson(any))