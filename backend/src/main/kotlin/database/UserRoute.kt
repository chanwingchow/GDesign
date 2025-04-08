package com.chanwingchow.database

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.chanwingchow.Response
import de.mkammerer.argon2.Argon2Factory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

/**
 * 配置用户路由。
 */
fun Application.configureUserRoute(userService: UserService) {
    val argon2 = Argon2Factory.create()

    routing {
        // 注册登录
        post("/login") {
            val userForm = call.receive<UserForm>()
            val user = userService.select(userForm.id)

            if (user == null) {
                // 用户未注册，注册新用户
                userService.insert(
                    User(
                        userForm.id,
                        argon2.hash(5, 65536, 2, userForm.password.toCharArray()),
                    )
                )
            } else {
                // 用户已注册，密码错误
                if (!argon2.verify(user.password, userForm.password.toCharArray())) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
            }

            // 创建 JWT Token
            val token = JWT.create()
                .withAudience(environment.config.property("jwt.audience").getString())
                .withIssuer(environment.config.property("jwt.issuer").getString())
                .withClaim("id", userForm.id)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(environment.config.property("jwt.secret").getString()))

            call.respond(Response(token))
        }

        authenticate("jwt") {
            // 获取游戏点数
            get("points") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal!!.payload.getClaim("id").asInt()
                val user = userService.select(id)

                call.respond(Response(user!!.points))
            }

            // 添加点数
            post("points") {
                val pointsForm = call.receive<PointsForm>()
                val principal = call.principal<JWTPrincipal>()
                val id = principal!!.payload.getClaim("id").asInt()
                val user = userService.select(id)!!
                userService.update(user.copy(points = user.points + pointsForm.points))
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}


@Serializable
data class UserForm(val id: Int, val password: String)


@Serializable
data class PointsForm(val points: Int)