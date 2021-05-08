package com.tonypepe

import com.fasterxml.jackson.databind.*
import com.github.javafaker.Faker
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
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.coroutines.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.transactions.transaction
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

    post("/courses/{cid}") {
        val courseID = call.parameters["cid"]?.toIntOrNull()
        val stuID = call.sessions.get<LoginSession>()?.stuID
        if (courseID == null || stuID == null) {
            call.respondHtml { respond404() }
        } else {
            val course = AppDatabase.getPickedList(stuID)
                .filter { it[PickedList.cID] == courseID }
            if (course.count() > 0) {
                AppDatabase.withdrawCourse(stuID, courseID)
            } else {
                AppDatabase.pickCourse(stuID, courseID)
            }
        }
        call.respondRedirect("/")
    }
}

fun Route.routeSearchCourse() {
    post("/search") {
        val s = call.receiveParameters()["s"]
        call.respondHtml {
            if (s == null) {
                respond404()
            } else {
                AppDatabase.searchCourse(s).also {
                    if (it.count() > 0) {
                        searchHTML(s, it)
                    } else respond404("無搜尋結果")
                }
            }
        }
    }
}

fun Route.routeAddStudent() {
    get("/add-student") {
        call.respondHtml {
            addStudentHTML()
        }
    }

    post("/add-student") {
        val parameters = call.receiveParameters()

        val cls = parameters["cls"]
        val count = parameters["count"]?.toIntOrNull()
        println("$cls\t$count")

        if (cls == null || count == null) {
            call.respondHtml { respond404() }
        } else {
            val allStudentID = AppDatabase.getAllStudentID()
            val ids = mutableListOf<String>()
            repeat(count) {
                var studentID = Students.createStudentID()
                while (allStudentID.contains(studentID) || ids.contains(studentID))
                    studentID = Students.createStudentID()
                ids.add(studentID)
            }
            val jobs = mutableListOf<Job>()
            ids.forEach { stuID ->
                jobs.add(GlobalScope.launch {
                    AppDatabase.insertStudent(stuID, faker.name().fullName(), cls)
                    AppDatabase.pickAllCompulsoryCourses(stuID)
                })
            }
            jobs.forEach { it.join() }
            call.respondRedirect("/")
        }
    }
}
