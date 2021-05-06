package com.tonypepe

import com.fasterxml.jackson.databind.*
import com.tonypepe.database.AppDatabase
import com.tonypepe.database.PickedList
import com.tonypepe.database.Students
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

const val LOGIN_SESSION = "LOGIN_SESSION"

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
    }
}

fun Route.routeRoot() {
    get("/") {
        val loginSession = call.sessions.get<LoginSession>()
        if (loginSession == null) {
            call.respondHtml {
                notLoginHtml()
            }
        } else {
            val id = loginSession.stuID
            val row = AppDatabase.getStudentByStuID(loginSession.stuID)
            if (row != null) {
                call.respondHtml {
                    loginHTML(id, row)
                }
            }
        }
    }

    // Try Login
    post("/") {
        val id = call.receiveParameters()[Students.stuID.name]
        if (id == null) call.respondHtml(HttpStatusCode.NotFound) { respond404("登入失敗") }.also { return@post }
        val row = transaction { AppDatabase.getStudentByStuID(id!!) }
        if (row == null) call.respondHtml(HttpStatusCode.NotFound) { respond404("登入失敗") }.also { return@post }
        call.sessions.set(LoginSession(id!!))
        call.respondRedirect("/")
    }

    get("/logout") {
        call.sessions.clear<LoginSession>()
        call.respondRedirect("/")
    }
}

fun Route.routeCourseList() {
    get("/courses") {
        call.respondHtml {
            courseListHTML()
        }
    }

    get("/courses/{id}") {
        val sID = call.sessions.get<LoginSession>()?.stuID
        val cID = call.parameters["id"]?.toIntOrNull()
        if (sID == null || cID == null) {
            call.respondHtml { respond404("Course Not Found") }
        } else {
            call.respondHtml {
                courseDetail(sID, cID)
            }
        }
    }

    post("/courses/{id}") {
        val courseID = call.parameters["id"]?.toIntOrNull()
        val stuID = call.sessions.get<LoginSession>()?.stuID
        if (courseID == null || stuID == null) {
            call.respondHtml { respond404() }
        } else {
            val course = AppDatabase.getPickedList(stuID)
                .filter { it[PickedList.courseID] == courseID }
            if (course.count() > 0) {
                AppDatabase.withdrawCourse(stuID, courseID)
            } else {
                AppDatabase.pickCourse(stuID, courseID)
            }
        }
        call.respondRedirect("/")
    }
}
