package com.tonypepe.database

import com.tonypepe.MYSQL_PW
import org.jetbrains.exposed.sql.*
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

    fun getCourse(courseID: Int) = try {
        transaction {
            Courses.select { Courses.courseID eq courseID }.first()
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
            Courses.select {
                (Courses.courseType eq 'M') and (Courses.courseClass eq stu[Students.stuClass])
            }.toList()
        }
    } catch (e: Exception) {
        println(e.message)
        listOf()
    }

    fun getPickedList(stuId: String): List<ResultRow> = try {
        transaction {
            PickedList.select { PickedList.stuID eq stuId }.toList()
        }
    } catch (e: Exception) {
        println(e.message)
        listOf()
    }

    /**
     *  回傳已選此課人數
     */
    fun getCourseStudentCount(courseID: Int) = transaction {
        PickedList.select { PickedList.courseID eq courseID }.count().toInt()
    }

    /**
     * 選課
     */
    fun pickCourse(sID: String, cID: Int) {
        transaction {
            PickedList.insert {
                it[stuID] = sID
                it[courseID] = cID
            }
        }
    }

    /**
     * 退選
     */
    fun withdrawCourse(sID: String, cID: Int) {
        transaction {
            PickedList.deleteWhere {
                (PickedList.stuID eq sID) and (PickedList.courseID eq cID)
            }
        }
    }

    fun getCourseTime(cID: Int) = transaction {
        CourseTime.select { CourseTime.courseID eq cID }.toList()
    }

    fun insertCourse(
        id: Int,
        name: String,
        teacher: String,
        type: Char,
        point: Int,
        count: Int,
        cls: String,
        dep: String
    ) {
        transaction {
            Courses.insert {
                it[courseID] = id
                it[courseName] = name
                it[teacherName] = teacher
                it[courseType] = type
                it[coursePoint] = point
                it[studentCount] = count
                it[courseClass] = cls
                it[courseDep] = dep
            }
        }
    }

    fun insertCourseTime(id: Int, date: Int, period: Int) {
        transaction {
            CourseTime.insert {
                it[courseID] = id
                it[courseDate] = date
                it[coursePeriod] = period
            }
        }
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
