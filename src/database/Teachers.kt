package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS teachers
(
    teacherID   VARCHAR(8) PRIMARY KEY,
    teacherName VARCHAR(20) NOT NULL
);
 */
object Teachers : Table("teachers") {
    val teacherID = varchar("teacherID", 8)
    val teacherName = varchar("teacherName", 20)

    override val primaryKey = PrimaryKey(teacherID)
}