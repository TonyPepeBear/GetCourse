package com.tonypepe.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object AppDatabase {
    fun initDatabase(
        testing: Boolean = false,
        mysqlPW: String = System.getenv("mysql_pw"),
        mysqlURL: String = System.getenv("mysql_url"),
        mysqlDB: String = System.getenv("mysql_db")
    ) {
        Database.connect(
            url = "jdbc:mysql://$mysqlURL:3306/$mysqlDB",
            user = "root", password = mysqlPW
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

    fun getCourse(cID: Int) = try {
        transaction {
            Courses.select { Courses.cID eq cID }.first()
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

    /**
     * Return picked course list with inner join detail
     */
    fun getPickedList(stuId: String): List<ResultRow> = try {
        transaction {
            PickedList.innerJoin(Courses, { cID }, { cID })
                .select { PickedList.stuID eq stuId }.toList()
        }
    } catch (e: Exception) {
        println(e.message)
        listOf()
    }

    fun getWatchedList(stuId: String): List<ResultRow> = try {
        transaction {
            WatchList.innerJoin(Courses, { courseID }, { cID })
                .select { WatchList.stuID eq stuId }.toList()
        }
    } catch (e: Exception) {
        println(e.message)
        listOf()
    }

    /**
     *  回傳已選此課人數
     */
    fun getCourseStudentCount(cID: Int) = transaction {
        PickedList.select { PickedList.cID eq cID }.count().toInt()
    }

    /**
     * 選課
     */
    fun pickCourse(sID: String, c: Int) {
        transaction {
            PickedList.insert {
                it[stuID] = sID
                it[cID] = c
            }
        }
    }

    fun pickAllCompulsoryCourses(sID: String) {
        val compulsoryCourses = getCompulsoryCourses(sID)
        compulsoryCourses.forEach {
            pickCourse(sID, it[Courses.cID])
        }
    }

    fun isCourseConflict(sID: String, courseID: Int, pickedList: List<ResultRow> = getPickedList(sID)): Boolean {
        val ct = getCourseTime(courseID)

        pickedList.forEach { p ->
            getCourseTime(getCourse(p[PickedList.cID])!![Courses.courseID]).forEach { i ->
                ct.forEach { j ->
                    if (i[CourseTime.courseDate] == j[CourseTime.courseDate] && i[CourseTime.coursePeriod] == j[CourseTime.coursePeriod]) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 退選
     */
    fun withdrawCourse(sID: String, cID: Int) {
        transaction {
            PickedList.deleteWhere {
                (PickedList.stuID eq sID) and (PickedList.cID eq cID)
            }
        }
    }

    fun withdrawWatchedCourse(sID: String, cID: Int) {
        transaction {
            WatchList.deleteWhere {
                (PickedList.stuID eq sID) and (PickedList.cID eq cID)
            }
        }
    }

    fun getCourseTime(courseID: Int) = transaction {
        CourseTime.select { CourseTime.courseID eq courseID }.toList()
    }

    fun insertStudent(sID: String, sName: String, sCls: String) {
        transaction {
            Students.insert {
                it[stuID] = sID
                it[stuClass] = sCls
                it[stuName] = sName
            }
        }
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

    fun searchCourse(s: String) = try {
        val ss = "%$s%"
        transaction {
            Courses.select {
                (Courses.courseID eq (s.toIntOrNull() ?: 0)) or
                        (Courses.courseName like ss) or
                        (Courses.teacherName like ss) or
                        (Courses.courseDep like ss) or
                        (Courses.courseClass like ss)
            }.toList()
        }
    } catch (e: Exception) {
        println(e.message)
        listOf()
    }

    fun watchCourse(sid: String, cid: Int) {
        transaction {
            WatchList.insert {
                it[stuID] = sid
                it[courseID] = cid
            }
        }
    }
}
