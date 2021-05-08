package com.tonypepe.database

import org.jetbrains.exposed.sql.Table
import kotlin.random.Random

/*
CREATE TABLE IF NOT EXISTS students
(
    stuID VARCHAR(8) PRIMARY KEY,
    name  VARCHAR(20) NOT NULL,
    class varchar(20) NOT NULL
);
;
 */
object Students : Table("students") {
    val stuID = varchar("stuID", length = 8)
    val stuName = varchar("name", length = 20)
    val stuClass = varchar("class", 20)

    override val primaryKey = PrimaryKey(stuID)

    fun createStudentID(): String {
        val builder = StringBuilder()
        builder.append("D08")
        repeat(5) {
            builder.append(Random.nextInt(0, 10))
        }
        return builder.toString()
    }
}
