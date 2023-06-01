package com.github.zinc.core

import com.github.zinc.mybatis.IMapper
import com.github.zinc.mybatis.MybatisConfig
import org.apache.ibatis.session.SqlSession

abstract class AbstractDAO: AutoCloseable {
    protected val sqlSession: SqlSession = MybatisConfig.sqlSessionFactory.openSession(true)
    protected abstract val mapper: IMapper

    override fun close() = sqlSession.close()
}