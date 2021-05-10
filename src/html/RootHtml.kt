package com.tonypepe.html

import com.tonypepe.database.AppDatabase
import com.tonypepe.database.PickedList
import com.tonypepe.database.Students
import kotlinx.html.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction

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
            urlButton("新增學生", "/add-student")
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
        divContainer {
            h1 { +"HI  ${row[Students.stuName]}" }
            h3 {
                +row[Students.stuClass]
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
