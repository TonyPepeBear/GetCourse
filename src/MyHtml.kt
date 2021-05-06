package com.tonypepe

import com.tonypepe.database.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction

const val bootstrapCdn = "https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"

/**
 * 課程表格
 */
@HtmlTagMarker
fun FlowContent.courseGrid(courses: List<ResultRow>) {
    div(classes = "row") {
        div(classes = "col-2 border p-1") { +"課程代號" }
        div(classes = "col-2 border p-1") { +"課程名稱" }
        div(classes = "col-2 border p-1") { +"課程學分" }
        div(classes = "col-2 border p-1") { +"人數" }
        div(classes = "col-2 border p-1") { +"授課教師" }
    }
    courses.forEach {
        div(classes = "row") {
            div(classes = "col-2 border") { +it[Courses.courseID].toString() }
            div(classes = "col-2 border") { +it[Courses.courseName] }
            div(classes = "col-2 border") { +it[Courses.coursePoint].toString() }
            div(classes = "col-2 border") { +"${AppDatabase.getCourseStudentCount(it[Courses.courseID])} / ${it[Courses.studentCount]}" }
            div(classes = "col-2 border") { +it[Teachers.teacherName].toString() }
        }
    }
}

@HtmlTagMarker
fun FlowContent.urlButton(title: String, url: String) {
    input(type = InputType.button, classes = "btn btn-primary m-2") {
        attributes["value"] = title
        attributes["onclick"] = "self.location.href='$url'"
    }
}

fun HTML.respond404(message: String = "") {
    head {
        title = "404"
        styleLink(bootstrapCdn)
    }
    body {
        div(classes = "container") {
            h1 { +"404" }
            h3 { +message }
            urlButton("返回主頁面", "/")
        }
    }
}

/**
 * 未登入的 HTML
 */
fun HTML.notLoginHtml() {
    head {
        title = "Course"
        styleLink(bootstrapCdn)
    }
    body {
        div(classes = "container") {
            h1 { +"選課系統" }
            form(classes = "form-inline", action = "/", method = FormMethod.post) {
                input(classes = "m-2", type = InputType.text, name = Students.stuID.name) {
                    placeholder = "學號"
                }
                button(type = ButtonType.submit, classes = "btn btn-primary m-2") { +"登入" }
            }
            ul {
                AppDatabase.getAllStudentID().forEach {
                    li {
                        +it
                    }
                }
            }
            urlButton("課程列表", "/courses")
        }
    }
}

/**
 * 已登入的 HTML
 */
fun HTML.loginHTML(stuID: String, row: ResultRow) {
    head {
        title = "Course"
        styleLink(bootstrapCdn)
    }
    body {
        div(classes = "container") {
            h1 { +"HI  ${row[Students.stuName]}" }
            h3 {
                +"${row[Students.stuID]}   ${
                    toClassName(
                        row[Students.dep],
                        row[Students.grade],
                        row[Students.cls]
                    )
                }"
            }
            div(classes = "row") {
                urlButton("登出", "logout")
                urlButton("課程列表", "/courses")
            }
            h2 { +"必修課表" }
            courseGrid(AppDatabase.getCompulsoryCourses(stuID))
            h2 { +"已選課表" }
            courseGrid(AppDatabase.getPickedList(stuID).map {
                transaction { AppDatabase.getCourse(it[PickedList.courseID])!! }
            })
        }
    }
}

fun HTML.courseListHTML() {
    head {
        title = "課程列表"
        styleLink(bootstrapCdn)
    }
    body {
        div(classes = "container") {
            h1 { +"課程列表" }
            urlButton("返回主頁面", "/")
            courseGrid(AppDatabase.getAllCourse())
        }
    }
}

fun HTML.courseDetail(id: Int) {
    val course = AppDatabase.getCourse(id)
    if (course == null) {
        respond404("Course Not Found")
    } else {
        head {
            title = course[Courses.courseName]
            styleLink(bootstrapCdn)
        }
        body {
            div(classes = "container") {
                h1 { +"${course[Courses.courseID]}  ${course[Courses.courseName]}" }
            }
        }
    }
}
