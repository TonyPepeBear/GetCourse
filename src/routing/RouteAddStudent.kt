package com.tonypepe.routing

import com.github.javafaker.Faker
import com.tonypepe.database.AppDatabase
import com.tonypepe.database.Students
import com.tonypepe.html.addStudentHTML
import com.tonypepe.html.respond404
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val faker = Faker()
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
