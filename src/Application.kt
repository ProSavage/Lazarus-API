package net.prosavage

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import net.prosavage.db.MongoClient
import net.prosavage.db.User

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {

        post("/register-user") {
            val post = call.receive<User>()
            val user = MongoClient.getUser(post.uuid) ?: kotlin.run {
                val theUser = User(post.uuid, post.name, post.tokens)
                MongoClient.registerUser(theUser)
                call.respond(mapOf("message" to "Registered new user.", "new-user" to theUser))
                return@post
            }
            call.respond(mapOf("message" to "user already exists.", "existing-user" to user))

        }

        post("/update-tokens") {
            val post = call.receive<UpdateTokensPost>()
            val user = MongoClient.getUser(post.uuid) ?: kotlin.run {
                call.respond(mapOf("message" to "user does not exist"))
                return@post
            }
            MongoClient.updateUserTokens(user.uuid, post.tokensAmt)
            call.respond(mapOf("message" to "successfully updated user", "updated-user" to MongoClient.getUser(user.uuid)))
        }

        get("/") {
            call.respond(mapOf("status" to "ACTIVE"))
        }
    }
}

data class UpdateTokensPost(val uuid: String, val tokensAmt: Int)