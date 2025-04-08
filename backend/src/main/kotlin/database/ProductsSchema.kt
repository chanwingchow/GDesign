package com.chanwingchow.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 产品。
 *
 * @param id 唯一标识
 * @param name 名称
 * @param image 图片
 * @param points 兑换所需点数
 */
@Serializable
data class Product(
    val id: Long,
    val name: String,
    val image: String,
    val points: Int,
)


/**
 * 产品服务。
 */
class ProductService(database: Database) {
    /**
     * 产品表。
     */
    object Products : Table() {
        val id = long("id")
        val name = varchar("name", length = 50)
        val image = varchar("image", length = 200)
        val points = integer("points")

        override val primaryKey = PrimaryKey(id)
    }


    init {
        transaction(database) {
            SchemaUtils.create(Products)
        }
    }


    /**
     * 将 [products] 插入产品表。
     */
    suspend fun insertAll(products: List<Product>) = query {
        Products.batchInsert(products) {
            this[Products.id] = it.id
            this[Products.name] = it.name
            this[Products.points] = it.points
            this[Products.image] = it.image
        }
    }


    /**
     * 返回所有产品。
     */
    suspend fun selectAll(): List<Product> = query {
        Products.selectAll()
            .map {
                Product(
                    id = it[Products.id],
                    name = it[Products.name],
                    image = it[Products.image],
                    points = it[Products.points],
                )
            }
    }


    suspend fun select(id: Long): Product? = query {
        Products.selectAll()
            .where { Products.id eq id }
            .map {
                Product(
                    id = it[Products.id],
                    name = it[Products.name],
                    image = it[Products.image],
                    points = it[Products.points],
                )
            }
            .singleOrNull()
    }


    private suspend fun <T> query(block: suspend () -> T) =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}