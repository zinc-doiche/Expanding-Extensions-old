package com.github.zinc.database

import com.github.zinc.warn
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.PreparedStatement

object DBManager {
    private const val IDLE = 30
    private const val MAX_POOL_SIZE = 30
    private const val MAX_LIFE_TIME = 58000L

    private lateinit var dataSource: HikariDataSource

    fun hikariSetup() {
        val hikari = HikariConfig()

        hikari.dataSourceClassName = "com.mysql.cj.jdbc.MysqlDataSource"
        hikari.addDataSourceProperty("serverName", "localhost")
        hikari.addDataSourceProperty("port", 3306)
        hikari.addDataSourceProperty("databaseName", "zinc")
        hikari.addDataSourceProperty("user", "root")
        hikari.addDataSourceProperty("password", "1234")

        hikari.minimumIdle = IDLE
        hikari.maximumPoolSize = MAX_POOL_SIZE
        hikari.maxLifetime = MAX_LIFE_TIME

        dataSource = HikariDataSource(hikari)
    }

    fun executeQuery(sql: String) = usePreparedStatement(sql, PreparedStatement::execute)

    fun usePreparedStatement(sql: String, useStatement: (PreparedStatement) -> Unit) {
        val connection = dataSource.connection ?: run { warn("Failed to get connection"); return }
        connection.use { it.prepareStatement(sql).use(useStatement::invoke) }
    }

}