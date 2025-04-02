package com.chanwingchow.database

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun Application.configureOrderRoute(database: Database) {
    val orderService = OrderService(database)

    routing {
        authenticate("jwt") {
            // 获取订单
            get("/orders") {
                val userId = call.parameters["userId"]?.toInt()
                userId?.let {
                    call.respond(orderService.selectAll(it))
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            // 创建订单
            post("/orders") {
                val order = call.receive<Order>()
                orderService.insert(order)
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}