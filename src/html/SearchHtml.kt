package com.tonypepe.html

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.h1
import org.jetbrains.exposed.sql.ResultRow

fun HTML.searchHTML(s: String, result: List<ResultRow>) {
    bootstrapHead("搜尋")
    body {
        navBar()
        divContainer {
            h1 { +"搜尋結果：$s" }
            courseGrid(result)
        }
    }
}
