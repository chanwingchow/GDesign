package com.chanwingchow.database

import com.chanwingchow.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

fun Application.configureOrderRoute(
    userService: UserService,
    productService: ProductService,
    orderService: OrderService,
) {
    routing {
        authenticate("jwt") {
            // 获取订单
            get("/orders") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                call.respond(Response(orderService.selectAll(userId)))
            }

            // 创建订单
            post("/orders") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                val user = userService.select(userId)!!

                val productId = call.receive<OrderForm>().productId
                val product = productService.select(productId)!!

                if (user.points < product.points) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                orderService.insert(
                    Order(
                        userId = userId,
                        productId = productId,
                    )
                )
                userService.update(user.copy(points = user.points - product.points))
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}


@Serializable
data class OrderForm(val productId: Long)