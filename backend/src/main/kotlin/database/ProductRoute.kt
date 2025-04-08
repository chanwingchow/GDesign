package com.chanwingchow.database

import com.chanwingchow.Response
import com.chanwingchow.spider.getProducts
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database

/**
 * 配置产品路由。
 */
fun Application.configureProductRoute(database: Database) {
    val productService = ProductService(database)

    routing {
        authenticate("jwt") {
            get("/products") {
                var products = productService.selectAll()
                if (products.isEmpty()) {
                    products = getProducts()
                    productService.insertAll(products)
                }
                call.respond(Response(products))
            }
        }
    }
}