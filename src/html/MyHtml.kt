package com.tonypepe.html

import com.tonypepe.database.AppDatabase
import com.tonypepe.database.CourseTime
import com.tonypepe.database.Courses
import kotlinx.html.*
import org.jetbrains.exposed.sql.ResultRow

const val bootstrapCssCdn = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/css/bootstrap.min.css"
const val bootstrapJsCdn = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"

@HtmlTagMarker
fun HTML.bootstrapHead(title: String = "") {
    head {
        this.title(title)
        meta("viewport", content = "width=device-width, initial-scale=1")
        styleLink(bootstrapCssCdn)
    }
}

@HtmlTagMarker
fun FlowContent.divContainer(block: DIV.() -> Unit) = DIV(attributesMapOf("class", "container"), consumer).visit(block)

/**
 * 課程列表
 */
@HtmlTagMarker
fun FlowContent.courseGrid(courses: List<ResultRow>) {
    val sortedCourses = courses.sortedBy { it[Courses.courseID] }
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
            sortedCourses.forEach {
                tr {
                    td {
                        val cit = it[Courses.cID].toString()
                        a("/courses/$cit") { +it[Courses.courseID].toString() }
                    }
                    td { +it[Courses.courseName] }
                    td { +it[Courses.coursePoint].toString() }
                    td { +"${AppDatabase.getCourseStudentCount(it[Courses.cID])} / ${it[Courses.studentCount]}" }
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
                    courseTimeTD(r)
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
private fun TR.courseTimeTD(r: Int) {
    td {
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
}

@HtmlTagMarker
fun FlowContent.urlButton(title: String, url: String) {
    input(type = InputType.button, classes = "btn btn-primary m-2") {
        attributes["value"] = title
        attributes["onclick"] = "self.location.href='$url'"
    }
}

@HtmlTagMarker
fun FlowContent.navBar() {
    nav(classes = "navbar sticky-top navbar-expand-lg navbar-light bg-light") {
        div(classes = "container-fluid") {
            a(classes = "navbar-brand", href = "/") { +"選課系統" }
        }
        form(classes = "d-flex px-2", action = "/search", method = FormMethod.post) {
            input(type = InputType.search, classes = "form-control me-2", name = "s") {
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
