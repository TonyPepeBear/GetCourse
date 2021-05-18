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
            withdrawModal()
            navBar()
            val stuCount = AppDatabase.getCourseStudentCount(cID)
            val cTime = AppDatabase.getCourseTime(cID)

            val conflict = AppDatabase.isCourseConflict(sID, course[Courses.courseID])

            div(classes = "container") {
                h1 { +"${course[Courses.courseID]}  ${course[Courses.courseName]}" }
                h3 { +"老師：${course[Courses.teacherName]}" }
                h3 { +"課程班級：${course[Courses.courseClass]}" }
                h3 { +"課程人數：$stuCount / ${course[Courses.studentCount]}" }

                form(action = "/courses/$cID", method = FormMethod.post) {
                    if (
                        AppDatabase.getPickedList(sID).filter { it[PickedList.cID] == cID }
                            .count() > 0
                    ) {
                        if (course[Courses.courseType] == 'M') {
                            button(type = ButtonType.button, classes = "btn btn-danger m-2") {
                                attributes["data-bs-toggle"] = "modal"
                                attributes["data-bs-target"] = "#withdraw-modal"
                                +"退選"
                            }
                        } else {
                            button(type = ButtonType.submit, classes = "btn btn-danger m-2") {
                                +"退選"
                            }
                        }
                    } else {
                        if (stuCount >= course[Courses.studentCount]) {
                            h3(classes = "text-danger") { +"人數已滿無法加選" }
                        }
                        if (conflict) {
                            h3(classes = "text-danger") { +"課程時間衝突無法加選" }
                        }
                        button(type = ButtonType.submit, classes = "btn btn-primary m-2") {
                            if (stuCount >= course[Courses.studentCount] || conflict) {
                                attributes["disabled"] = ""
                            }
                            +"加選"
                        }
                    }
                }
            }
            script(src = bootstrapJsCdn) {}
        }
    }
}

@HtmlTagMarker
fun FlowContent.withdrawModal() {
    div(classes = "modal fade") {
        id = "withdraw-modal"
        div(classes = "modal-dialog") {
            div(classes = "modal-content") {
                div(classes = "modal-header") {
                    h5(classes = "modal-title") { +"確定退選？" }
                }
                div(classes = "modal-body") {
                    p { +"此課程為必修課" }
                }
                form(classes = "modal-footer", method = FormMethod.post) {
                    button(classes = "btn btn-secondary") {
                        attributes["data-bs-dismiss"] = "modal"
                        +"取消"
                    }
                    button(classes = "btn btn-danger") {
                        +"退選"
                    }
                }
            }
        }
    }
}
