package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS courses
(
    cID          INT AUTO_INCREMENT PRIMARY KEY,
    courseID     INT PRIMARY KEY,
    courseName   VARCHAR(20) NOT NULL,
    teacherName  varchar(8)  NOT NULL,
    courseType   CHAR        NOT NULL,
    coursePoint  INT         NOT NULL,
    studentCount INT         NOT NULL,
    courseClass  varchar(20) NOT NULL,
    courseDep    varchar(20) NOT NULL
);;
 */
object Courses : Table("courses") {
    val cID = integer("cID").autoIncrement()
    val courseID = integer("courseID")
    val courseName = varchar("courseName", 20)
    val teacherName = varchar("teacherName", 8)
    val courseType = char("courseType")
    val coursePoint = integer("coursePoint")
    val studentCount = integer("studentCount")
    val courseClass = varchar("courseClass", 20)
    val courseDep = varchar("courseDep", 20)

    override val primaryKey = PrimaryKey(cID)
}

