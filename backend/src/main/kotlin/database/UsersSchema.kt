package com.chanwingchow.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 用户。
 *
 * @param id 唯一标识
 * @param name 用户名
 * @param password 密码
 */
@Serializable
data class User(val id: Int, val password: String, val name: String = id.toString(), val points: Int = 0)


/**
 * 用户服务。
 */
class UserService(database: Database) {
    // 用户表
    object Users : Table() {
        val id = integer("id")
        val name = varchar("name", length = 20)
        val password = text("password")
        val points = integer("points")

        override val primaryKey = PrimaryKey(id)
    }


    init {
        transaction(database) {
            // 创建表
            SchemaUtils.create(Users)
        }
    }


    /**
     * 查询用户。
     *
     * @param id 用户唯一标识
     */
    suspend fun select(id: Int): User? = query {
        Users.selectAll()
            .where { Users.id eq id }
            .map { User(it[Users.id], it[Users.name], it[Users.password], it[Users.points]) }
            .singleOrNull()
    }


    /**
     * 插入用户。
     *
     * @param user 用户
     */
    suspend fun insert(user: User) = query {
        Users.insert {
            it[id] = user.id
            it[name] = user.name
            it[password] = user.password
        }
    }


    /**
     * 更新用户。
     *
     * @param user 用户
     */
    suspend fun update(user: User) = query {
        Users.update(where = { Users.id eq user.id }) {
            it[name] = user.name
            it[password] = user.password
        }
    }


    /**
     * 删除用户。
     *
     * @param id 用户唯一标识
     */
    suspend fun delete(id: Int) = query { Users.deleteWhere { Users.id eq id } }


    private suspend fun <T> query(block: suspend () -> T) =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}