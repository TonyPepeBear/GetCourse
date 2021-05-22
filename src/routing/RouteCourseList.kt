package com.tonypepe.routing

import com.tonypepe.LoginSession
import com.tonypepe.database.AppDatabase
import com.tonypepe.database.PickedList
import com.tonypepe.database.WatchList
import com.tonypepe.html.courseDetail
import com.tonypepe.html.courseListHTML
import com.tonypepe.html.respond404
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

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

    post("/watch/{cid}") {
        val cid = call.parameters["cid"]?.toIntOrNull()
        val stuID = call.sessions.get<LoginSession>()?.stuID
        if (cid == null || stuID == null) {
            call.respondHtml { respond404() }
        } else {
            val course = AppDatabase.getWatchedList(stuID)
                .filter { it[WatchList.courseID] == cid }
            if (course.count() > 0) {
                AppDatabase.withdrawCourse(stuID, cid)
            } else {
                AppDatabase.watchCourse(stuID, cid)
            }
        }
        call.respondRedirect("/")
    }
}
