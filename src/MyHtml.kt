package com.tonypepe

import com.tonypepe.database.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction

const val bootstrapCssCdn = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/css/bootstrap.min.css"
const val bootstrapJsCdn = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"

fun HTML.bootstrapHead(title: String = "") {
    head {
        meta("viewport", content = "width=device-width, initial-scale=1")
        styleLink(bootstrapCssCdn)
    }
}

/**
 * 課程列表
 */
@HtmlTagMarker
fun FlowContent.courseGrid(courses: List<ResultRow>) {
    table(classes = "table table-hover") {
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
                        val cit = it[Courses.cID].toString()
                        a("/courses/$cit") { +it[Courses.courseID].toString() }
                    }
                    td { +it[Courses.courseName] }
                    td { +it[Courses.coursePoint].toString() }
                    td { +"${AppDatabase.getCourseStudentCount(it[Courses.courseID])} / ${it[Courses.studentCount]}" }
                    td { +it[Courses.teacherName].toString() }
                }
            }
        }
    }
}

/**
 * 課表
 */
@HtmlTagMarker
fun FlowContent.courseGridDateTable(courses: List<ResultRow>) {
    val coursesTime = arrayListOf<ResultRow>().apply {
        courses.forEach {
            this.addAll(
                AppDatabase.getCourseTime(it[Courses.courseID])
            )
        }
    }

    table(classes = "table table-bordered align-middle text-center") {
        thead {
            tr {
                th { style = "width: 9%"; +"#" }
                th { style = "width: 13%"; +"一" }
                th { style = "width: 13%"; +"二" }
                th { style = "width: 13%"; +"三" }
                th { style = "width: 13%"; +"四" }
                th { style = "width: 13%"; +"五" }
                th { style = "width: 13%"; +"六" }
                th { style = "width: 13%"; +"日" }
            }
        }
        tbody {
            repeat(14) { r ->
                tr {
                    // course time
                    td() {
                        +when (r + 1) {
                            1 -> "08:10"
                            2 -> "09:10"
                            3 -> "10:10"
                            4 -> "11:10"
                            5 -> "12:10"
                            6 -> "13:10"
                            7 -> "14:10"
                            8 -> "15:10"
                            9 -> "16:10"
                            10 -> "17:10"
                            11 -> "18:30"
                            12 -> "19:25"
                            13 -> "20:25"
                            14 -> "21:20"
                            else -> ""
                        }
                        br()
                        +"|"
                        br()
                        +when (r + 1) {
                            1 -> "09:00"
                            2 -> "10:00"
                            3 -> "11:00"
                            4 -> "12:00"
                            5 -> "13:00"
                            6 -> "14:00"
                            7 -> "15:00"
                            8 -> "16:00"
                            9 -> "17:00"
                            10 -> "18:00"
                            11 -> "19:20"
                            12 -> "20:15"
                            13 -> "21:15"
                            14 -> "22:10"
                            else -> ""
                        }
                    }
                    repeat(7) { c ->
                        val d =
                            coursesTime.filter { it[CourseTime.courseDate] == c + 1 && it[CourseTime.coursePeriod] == r + 1 }
                        if (d.count() > 0) {
                            td {
                                d.forEach { ct ->
                                    val s =
                                        courses.filter { it[Courses.courseID] == ct[CourseTime.courseID] }[0][Courses.courseName]
                                    +s
                                    br()
                                }
                            }
                        } else {
                            th { +"" }
                        }
                    }
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
}

fun HTML.respond404(message: String = "") {
    bootstrapHead("404")
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
    bootstrapHead("選課系統")
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
    bootstrapHead("選課系統")
    body {
        navBar()
        div(classes = "container") {
            h1 { +"HI  ${row[Students.stuName]}" }
            h3 {
                row[Students.stuClass]
            }
            span {
                urlButton("登出", "logout")
                urlButton("課程列表", "/courses")
            }
            h2 { +"必修課程" }
            courseGrid(AppDatabase.getCompulsoryCourses(stuID))
            h2 { +"已選課程" }
            courseGrid(AppDatabase.getPickedList(stuID).map {
                transaction { AppDatabase.getCourse(it[PickedList.cID])!! }
            })
            h2 { +"已選課表" }
            courseGridDateTable(AppDatabase.getPickedList(stuID).map {
                transaction { AppDatabase.getCourse(it[PickedList.cID])!! }
            })
        }
    }
}

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
