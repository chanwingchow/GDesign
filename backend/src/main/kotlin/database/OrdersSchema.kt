package com.chanwingchow.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * 订单。
 *
 * @param id 唯一标识
 * @param userId 用户 id
 * @param productId 商品 id
 * @param points 订单所需点数
 * @param time 下单时间
 */
@Serializable
data class Order(
    val userId: Int,
    val productId: Long,
    val points: Int,
    val time: String = LocalDateTime.now().toString(),
)


/**
 * 订单服务。
 */
class OrderService(database: Database) {
    object Orders : Table() {
        val id = integer("id").autoIncrement()
        val userId = integer("user_id")
        val productId = long("product_id")
        val points = integer("points")
        val time = varchar("time", length = 50)

        override val primaryKey = PrimaryKey(id)
    }


    init {
        transaction(database) {
            SchemaUtils.create(Orders)
        }
    }


    /**
     * 插入订单。
     */
    suspend fun insert(order: Order) = query {
        Orders.insert {
            it[userId] = order.userId
            it[productId] = order.productId
            it[points] = order.points
        }
    }


    /**
     * 查询所有订单。
     */
    suspend fun selectAll(userId: Int) = query {
        Orders.selectAll()
            .where { Orders.userId eq userId }
            .map {
                Order(
                    userId = it[Orders.userId],
                    productId = it[Orders.productId],
                    points = it[Orders.points],
                    time = it[Orders.time],
                )
            }
    }

    private suspend fun <T> query(block: suspend () -> T) =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}