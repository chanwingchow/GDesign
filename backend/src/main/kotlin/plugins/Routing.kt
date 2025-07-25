package com.chanwingchow.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 配置路由。
 */
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello Narrow Escape backend!")
        }
    }
}
