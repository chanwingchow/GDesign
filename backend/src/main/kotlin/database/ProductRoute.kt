package com.chanwingchow.database

import com.chanwingchow.Response
import com.chanwingchow.spider.getProducts
import com.github.androidpasswordstore.sublimefuzzy.Fuzzy
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 配置产品路由。
 */
fun Application.configureProductRoute(
    userService: UserService,
    productService: ProductService,
    orderService: OrderService,
) {
    routing {
        authenticate("jwt") {
            // 所有产品
            get("/products") {
                // 获取商品列表
                var products = productService.selectAll()
                if (products.isEmpty()) {
                    products = getProducts()
                    productService.insertAll(products)
                }

                // 获取玩家当前点数
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                val points = userService.select(userId)!!.points

                // 获取玩家历史购买记录
                val historyProducts = orderService.selectAll(userId)
                    .map { it.productId }.toSet()
                    .map { productService.select(it)!! }

                // 无购买记录则返回打乱后列表
                if (historyProducts.isEmpty()) {
                    call.respond(Response(products.shuffled()))
                    return@get
                }

                val mutableProducts = products.toMutableList()
                val recommendProducts = mutableListOf<Product>()

                // 优先推荐与玩家购买过的商品相匹配的商品
                for (index in mutableProducts.size - 1 downTo 0) {
                    for (historyProduct in historyProducts) {
                        if (Fuzzy.fuzzyMatchSimple(mutableProducts[index].name, historyProduct.name)) {
                            recommendProducts.add(mutableProducts[index])
                            mutableProducts.removeAt(index)
                            break
                        }
                    }
                }

                // 商品所需点数普遍大于玩家点数，则按点数排序
                if (recommendProducts.count { it.points >= points } > recommendProducts.size / 2) {
                    recommendProducts.sortBy { it.points }
                }

                // 在尾部加入随机的剩余商品
                recommendProducts.addAll(mutableProducts.shuffled())

                call.respond(Response(recommendProducts))
            }

            // 单个产品
            get("/products/{id}") {
                val id = call.parameters["id"]!!
                val product = productService.select(id.toLong())!!
                call.respond(Response(product))
            }
        }
    }
}