package com.tonypepe

import com.tonypepe.database.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction

const val bootstrapCdn = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/css/bootstrap.min.css"

/**
 * 課程表格
 */
@HtmlTagMarker
fun FlowContent.courseGrid(courses: List<ResultRow>) {
    table(classes = "table table-striped") {
        thead {
            tr {
                th { +"課程代號" }
                th { +"課程名稱" }
                th { +"課程學分" }
                th { +"人數" }
                th { +"授課教師" }
            }
        }
        tbody {
            courses.forEach {
                tr {
                    td {
                        val courseID = it[Courses.courseID].toString()
                        a("/courses/$courseID") { +courseID }
                    }
                    td { +it[Courses.courseName] }
                    td { +it[Courses.coursePoint].toString() }
                    td { +"${AppDatabase.getCourseStudentCount(it[Courses.courseID])} / ${it[Courses.studentCount]}" }
                    td { +it[Teachers.teacherName].toString() }
                }
            }
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

fun FlowContent.navBar() {
    nav(classes = "navbar sticky-top navbar-expand-lg navbar-light bg-light") {
        div(classes = "container-fluid") {
            a(classes = "navbar-brand", href = "/") { +"選課系統" }
        }
        form(classes = "d-flex px-2", action = "/courses/search") {
            input(type = InputType.search, classes = "form-control me-2") {
                placeholder = "搜尋課程"
            }
            button(classes = "btn btn-outline-success", type = ButtonType.submit) { +"Search" }
        }
    }

    /*
<form class="d-flex">
      <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">
      <button class="btn btn-outline-success" type="submit">Search</button>
    </form>
     */
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
        navBar()
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
        navBar()
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
            span() {
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
        head {
            title = course[Courses.courseName]
            styleLink(bootstrapCdn)
        }
        body {
            navBar()
            div(classes = "container") {
                h1 { +"${course[Courses.courseID]}  ${course[Courses.courseName]}" }
            }
            form(action = "/courses/$cID", method = FormMethod.post) {
                if (
                    AppDatabase.getPickedList(sID).filter { it[PickedList.courseID] == cID }
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
