package com.tonypepe.routing

import com.tonypepe.database.AppDatabase
import com.tonypepe.respond404
import com.tonypepe.searchHTML
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.routeSearchCourse() {
    post("/search") {
        val s = call.receiveParameters()["s"]
        call.respondHtml {
            if (s == null) {
                respond404()
            } else {
                AppDatabase.searchCourse(s).also {
                    if (it.count() > 0) {
                        searchHTML(s, it)
                    } else respond404("無搜尋結果")
                }
            }
        }
    }
}
