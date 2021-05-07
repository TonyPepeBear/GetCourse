package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS courseTime
(
    courseTimeID INT PRIMARY KEY AUTO_INCREMENT,
    courseID     INT,
    courseDate   INT,
    coursePeriod INT,
);
 */
object CourseTime : Table("courseTime") {
    val courseTimeID = integer("courseTimeID").autoIncrement()
    val courseID = integer("courseID")
    val courseDate = integer("courseDate")
    val coursePeriod = integer("coursePeriod")

    override val primaryKey = PrimaryKey(courseTimeID)

    fun convertTime(s: String) = when (s) {
        "一" -> 1
        "二" -> 2
        "三" -> 3
        "四" -> 4
        "五" -> 5
        "六" -> 6
        "七" -> 7
        else -> 0
    }
}