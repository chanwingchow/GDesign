package com.chanwingchow.plugins

import com.chanwingchow.database.OrderService
import com.chanwingchow.database.ProductService
import com.chanwingchow.database.UserService
import com.chanwingchow.database.configureOrderRoute
import com.chanwingchow.database.configureProductRoute
import com.chanwingchow.database.configureUserRoute
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 配置数据库。
 */
fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:mysql://localhost:3306",
        user = "root",
        password = "1246",
    )
    transaction {
        // 创建数据库
        SchemaUtils.createDatabase("narrow_escape")
    }
    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/narrow_escape",
        user = "root",
        password = "1246",
    )

    configureJWT()
    val userService = UserService(database)
    val productService = ProductService(database)
    val orderService = OrderService(database)
    configureUserRoute(userService)
    configureProductRoute(productService)
    configureOrderRoute(userService, productService, orderService)
}
