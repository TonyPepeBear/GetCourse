package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS students
(
    stuID VARCHAR(8) PRIMARY KEY,
    name  VARCHAR(20) NOT NULL,
    dep   VARCHAR(10) NOT NULL,
    grade INT         NOT NULL,
    class INT         NOT NULL
);
 */
object Students : Table("students") {
    val stuID = varchar("stuID", length = 8)
    val stuName = varchar("name", length = 20)
    val dep = varchar("dep", length = 10)
    val grade = integer("grade")
    val cls = integer("class")

    override val primaryKey = PrimaryKey(stuID)
}
