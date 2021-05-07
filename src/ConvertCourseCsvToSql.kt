package com.tonypepe

import com.opencsv.CSVReader
import com.tonypepe.database.AppDatabase
import com.tonypepe.database.CourseTime
import kotlinx.coroutines.*
import java.io.File


fun main() = runBlocking {
    AppDatabase.initDatabase()
    val courseJob = GlobalScope.launch(Dispatchers.IO) {
        val courseBuffer = File("./CourseData/DB_Table_course.csv").bufferedReader()
        var b = true
        CSVReader(courseBuffer).readAll().forEach { row ->
            if (b) {
                b = false
                return@forEach
            }
            AppDatabase.insertCourse(
                row[2].toInt(), row[1], row[8], row[4][0], row[3].toInt(), row[6].toInt(), row[0], row[5]
            )
        }
        courseBuffer.close()
    }
    val courseTimeJob = GlobalScope.launch(Dispatchers.IO) {
        val timeBuffer = File("./CourseData/DB_Table_time.csv").bufferedReader()
        var b = true
        CSVReader(timeBuffer).readAll().forEach {
            if (b) {
                b = false
                return@forEach
            }
            AppDatabase.insertCourseTime(
                it[0].toInt(), CourseTime.convertTime(it[1]), it[2].toInt()
            )
        }

    }
    courseJob.join()
    courseTimeJob.join()
}
