package com.tonypepe.database

import com.tonypepe.MYSQL_PW
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object AppDatabase {
    fun initDatabase(testing: Boolean = false) {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/test_db",
            user = "root", password = MYSQL_PW
        )
    }

    fun getAllStudentID() = transaction { Students.selectAll().map { it[Students.stuID] } }

    fun getStudentByStuID(stuId: String) = try {
        transaction {
            val row = Students.select { Students.stuID eq stuId }
            if (row.empty()) null else row.first()
        }
    } catch (e: Exception) {
        println(e.message)
        null
    }

    /**
     * 回傳所有課程
     */
    fun getAllCourse() = transaction { Courses.selectAll().toList() }

    /**
     * 回傳一學生的必修課
     */
    fun getCompulsoryCourses(stuID: String) = try {
        transaction {
            val stu = getStudentByStuID(stuID) ?: throw IllegalArgumentException()
            val dep = stu[Students.dep]
            val grade = stu[Students.grade]
            val cls = stu[Students.cls]
            Courses.select {
                (Courses.courseType eq 0) and
                        (Courses.courseDep eq dep) and
                        (Courses.courseGrade eq grade) and
                        (Courses.courseClass eq cls)
            }
                .toList()
        }
    } catch (e: Exception) {
        println(e.message)
        listOf()
    }
}

fun toClassName(dep: String, grade: Int, cls: Int): String {
    val g = when (grade) {
        1 -> "ㄧ"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        else -> ""
    }
    val c = when (cls) {
        1 -> "甲"
        2 -> "乙"
        3 -> "丙"
        4 -> "丁"
        else -> ""
    }
    return "$dep$g$c"
}
