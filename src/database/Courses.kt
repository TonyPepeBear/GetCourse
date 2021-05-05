package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS courses
(
    courseID     INT PRIMARY KEY AUTO_INCREMENT,
    courseName   VARCHAR(20) NOT NULL,
    courseType   INT         NOT NULL, # 0: 必修, 1: 選修, 2: 通識
    coursePoint  INT         NOT NULL,
    studentCount INT         NOT NULL,
    courseDep    varchar(20) NOT NULL,
    courseGrade  INT         NOT NULL,
    courseClass  INT         NOT NULL
);
 */
object Courses : Table("courses") {
    val courseID = integer("courseID").autoIncrement()
    val courseName = varchar("courseName", 20)
    val courseType = integer("courseType")
    val coursePoint = integer("coursePoint")
    val studentCount = integer("studentCount")
    val courseDep = varchar("courseDep", 20)
    val courseGrade = integer("courseGrade")
    val courseClass = integer("courseClass")

    override val primaryKey = PrimaryKey(courseID)
}

