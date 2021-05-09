package com.tonypepe.html

import com.tonypepe.database.AppDatabase
import com.tonypepe.database.Courses
import com.tonypepe.database.PickedList
import kotlinx.html.*

fun HTML.courseListHTML() {
    bootstrapHead("課程列表")
    body {
        navBar()
        div(classes = "container") {
            h1 { +"課程列表" }
            urlButton("返回主頁面", "/")
            courseGrid(AppDatabase.getAllCourse())
        }
    }
}

fun HTML.courseDetail(sID: String, cID: Int) {
    val course = AppDatabase.getCourse(cID)
    if (course == null) {
        respond404("Course Not Found")
    } else {
        bootstrapHead(course[Courses.courseName])
        body {
            navBar()
            div(classes = "container") {
                h1 { +"${course[Courses.courseID]}  ${course[Courses.courseName]}" }
            }
            form(action = "/courses/$cID", method = FormMethod.post) {
                if (
                    AppDatabase.getPickedList(sID).filter { it[PickedList.cID] == cID }
                        .count() > 0
                ) {
                    button(type = ButtonType.submit, classes = "btn btn-danger m-2") { +"退選" }
                } else {
                    button(type = ButtonType.submit, classes = "btn btn-primary m-2") { +"加選" }
                }
            }
        }
    }
}
