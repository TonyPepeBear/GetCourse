package com.tonypepe

import com.fasterxml.jackson.databind.SerializationFeature
import com.github.javafaker.Faker
import com.tonypepe.database.AppDatabase
import com.tonypepe.routing.routeAddStudent
import com.tonypepe.routing.routeCourseList
import com.tonypepe.routing.routeRoot
import com.tonypepe.routing.routeSearchCourse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

const val LOGIN_SESSION = "LOGIN_SESSION"
val faker = Faker(Locale.TAIWAN)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    AppDatabase.initDatabase(testing)

    install(Authentication) {}

    install(ContentNegotiation) {
        gson {}
        jackson { enable(SerializationFeature.INDENT_OUTPUT) }
    }

    install(Sessions) {
        cookie<LoginSession>(LOGIN_SESSION)
    }

    val client = HttpClient(Apache) {}


    routing {
        routeRoot()
        routeCourseList()
        routeSearchCourse()
        routeAddStudent()
    }
}
