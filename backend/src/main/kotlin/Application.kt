package com.chanwingchow

import com.chanwingchow.plugins.configureJWT
import com.chanwingchow.plugins.configureRouting
import com.chanwingchow.plugins.configureSerialization
import com.chanwingchow.plugins.configureDatabases
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    configureRouting()
    configureSerialization()
    configureTemplating()
}
