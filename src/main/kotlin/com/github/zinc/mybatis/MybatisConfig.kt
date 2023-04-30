package com.github.zinc.mybatis

import com.github.zinc.player.domain.PlayerVO
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.io.Resources
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory


object MybatisConfig {
    private const val IDLE = 30
    private const val MAX_POOL_SIZE = 30
    private const val MAX_LIFE_TIME = 58000L

    private const val SERVER_NAME = "localhost"
    private const val PORT = 3306
    private const val DATABASE_NAME = "zinc"
    private const val USER = "root"
    private const val PASSWORD = 1234

    val sqlSessionFactory: SqlSessionFactory = sqlSessionFactoryByXML()

    private fun sqlSessionFactoryByJava(): SqlSessionFactory {
        //hikari config
        val transactionFactory = JdbcTransactionFactory()
        val hikariConfig = HikariConfig().apply {
            this.dataSourceClassName = "com.mysql.cj.jdbc.MysqlDataSource"
            this.addDataSourceProperty("serverName", SERVER_NAME)
            this.addDataSourceProperty("port", PORT)
            this.addDataSourceProperty("databaseName", DATABASE_NAME)
            this.addDataSourceProperty("user", USER)
            this.addDataSourceProperty("password", PASSWORD)

            this.minimumIdle = IDLE
            this.maximumPoolSize = MAX_POOL_SIZE
            this.maxLifetime = MAX_LIFE_TIME
        }

        //mybatis config
        val environment = Environment("mysql", transactionFactory, HikariDataSource(hikariConfig))
        val configuration = Configuration(environment)
        configuration.typeAliasRegistry.registerAlias("playerVO", PlayerVO::class.java)
        configuration.isMapUnderscoreToCamelCase = true
        configuration.addMapper(PlayerMapper::class.java)

        return SqlSessionFactoryBuilder().build(configuration)
    }

    private fun sqlSessionFactoryByXML(): SqlSessionFactory {
        val resource = "mybatis-config.xml"
        return SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(resource))
    }
}