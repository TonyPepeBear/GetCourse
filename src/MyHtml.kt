package com.tonypepe

import com.tonypepe.database.AppDatabase
import com.tonypepe.database.Courses
import com.tonypepe.database.Students
import com.tonypepe.database.toClassName
import kotlinx.html.*
import org.jetbrains.exposed.sql.ResultRow


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
    }
    courses.forEach {
        div(classes = "row") {
            div(classes = "col-2 border") { +it[Courses.courseID].toString() }
            div(classes = "col-2 border") { +it[Courses.courseName] }
            div(classes = "col-2 border") { +it[Courses.coursePoint].toString() }
        }
    }
}

@HtmlTagMarker
fun FlowContent.urlButton(title: String, url: String) {
    input(type = InputType.button, classes = "btn btn-primary") {
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
            form(action = "/", method = FormMethod.post) {
                p {
                    +"id"
                    input(type = InputType.text, name = Students.stuID.name)
                }
                button(type = ButtonType.submit, classes = "btn btn-primary") { +"送出" }
            }
            ul {
                AppDatabase.getAllStudentID().forEach {
                    li {
                        +it
                    }
                }
            }
            urlButton("課程列表", "/course-list")
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
                        row[Students.grade], row[Students.cls]
                    )
                }"
            }
            urlButton("登出", "logout")
            h2 { +"必修" }
            courseGrid(AppDatabase.getCompulsoryCourses(stuID))
        }
    }
}
