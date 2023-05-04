package com.github.zinc.mybatis

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.IOException

object MybatisConfig {
    lateinit var sqlSessionFactory: SqlSessionFactory
        private set

    fun init() {;}

    init {
        val resource = "mybatis/config.xml"
        try {
            val reader = Resources.getResourceAsReader(resource)
            sqlSessionFactory = SqlSessionFactoryBuilder().build(reader)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}