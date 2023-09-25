package com.github.zinc.mongodb

import com.github.zinc.plugin
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object MongoDB {
    lateinit var client: MongoClient
        private set
    lateinit var database: MongoDatabase
        private set

    fun register() {
        val yml = File(plugin.dataFolder, "mongodb.yml")

        if(!yml.exists()) {
            plugin.saveResource("mongodb.yml", false)
        }

        YamlConfiguration.loadConfiguration(yml).get("url").let { url ->
            val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(url as String))
                .build()
            client = MongoClients.create(settings)
        }
        val databaseName = YamlConfiguration.loadConfiguration(yml).get("name") as String
        database = client.getDatabase(databaseName)
    }

    operator fun get(name: String): MongoCollection<Document> = database.getCollection(name)
}