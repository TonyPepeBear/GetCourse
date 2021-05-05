package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS courseTime
(
    courseTimeID INT PRIMARY KEY AUTO_INCREMENT,
    courseID     INT,
    courseDate   INT,
    coursePeriod INT,

    FOREIGN KEY (courseID) REFERENCES courses (courseID)
);
 */
object CourseTime : Table("courseTime") {
    val courseTimeID = integer("courseTimeID").autoIncrement()
    val courseID = integer("courseID") references Courses.courseID
    val courseDate = integer("courseDate")
    val coursePeriod = integer("coursePeriod")

    override val primaryKey = PrimaryKey(courseTimeID)
}