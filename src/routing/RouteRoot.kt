package com.tonypepe.routing

import com.tonypepe.LoginSession
import com.tonypepe.database.AppDatabase
import com.tonypepe.database.Students
import com.tonypepe.html.loginHTML
import com.tonypepe.html.notLoginHtml
import com.tonypepe.html.respond404
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.transactions.transaction

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
