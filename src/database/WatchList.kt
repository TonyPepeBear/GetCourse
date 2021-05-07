package com.tonypepe.database

import org.jetbrains.exposed.sql.Table

/*
CREATE TABLE IF NOT EXISTS watchList
(
    watchID  INT PRIMARY KEY AUTO_INCREMENT,
    stuID    varchar(8) NOT NULL,
    cID INT        NOT NULL,

    FOREIGN KEY (stuID) REFERENCES students (stuID),
    FOREIGN KEY (courseID) REFERENCES courses (courseID)
);
 */
object WatchList : Table("watchList") {
    val watchID = integer("watchID").autoIncrement()
    val stuID = varchar("stuID", 8) references Students.stuID
    val cID = integer("cID") references Courses.courseID

    override val primaryKey = PrimaryKey(watchID)
}