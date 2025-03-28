package com.chanwingchow.database

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.mkammerer.argon2.Argon2Factory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.util.*

/**
 * 配置用户路由。
 */
fun Application.configureUserRoute(database: Database) {
    val userService = UserService(database)
    val argon2 = Argon2Factory.create()

    routing {
        // 注册登录
        post("/login") {
            val user = call.receive<User>()
            val oldUser = userService.select(user.id)

            if (oldUser == null) {
                // 用户未注册，注册新用户
                userService.insert(
                    user.copy(
                        password = argon2.hash(5, 65536, 2, user.password.toCharArray()),
                    )
                )
            } else {
                // 用户已注册，密码错误
                if (!argon2.verify(oldUser.password, user.password.toCharArray())) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
            }

            // 创建 JWT Token
            val token = JWT.create()
                .withAudience(environment.config.property("jwt.audience").getString())
                .withIssuer(environment.config.property("jwt.issuer").getString())
                .withClaim("id", user.id)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(environment.config.property("jwt.secret").getString()))

            call.respond(HttpStatusCode.OK, mapOf("token" to token))
        }
    }
}