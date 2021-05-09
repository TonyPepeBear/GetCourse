package com.tonypepe.html

import kotlinx.html.*

fun HTML.addStudentHTML() {
    bootstrapHead("新增學生")
    body {
        divContainer {
            h1 { +"新增學生" }
            form(action = "/add-student", method = FormMethod.post) {
                div(classes = "form-group") {
                    label {
                        attributes["for"] = "cls"
                        +"班級"
                    }
                    input(classes = "form-control", type = InputType.text, name = "cls") {
                        id = "cls"
                        placeholder = "班級"
                    }
                }
                div(classes = "form-group") {
                    label {
                        attributes["for"] = "count"
                        +"數量"
                    }
                    input(classes = "form-control", type = InputType.text, name = "count") {
                        id = "count"
                        placeholder = "數量"
                    }
                }
                button(type = ButtonType.submit, classes = "btn btn-primary") { +"新增" }
            }
        }
    }
}
