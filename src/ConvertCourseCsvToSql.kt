package com.tonypepe

import com.opencsv.CSVReader
import com.tonypepe.database.AppDatabase
import java.io.File


fun main() {
    println(System.getProperty("user.dir"))
    AppDatabase.initDatabase()
    val dbBufferReader = File("./CourseData/DB_Table_course.csv").bufferedReader()
    var b = true
    CSVReader(dbBufferReader).readAll().forEach { row ->
        if (b) {
            b = false
            return@forEach
        }
        AppDatabase.insertCourse(
            row[2].toInt(), row[1], row[8], row[4][0], row[3].toInt(), row[6].toInt(), row[0], row[5]
        )
    }
}
