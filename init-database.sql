CREATE TABLE IF NOT EXISTS students
(
    stuID VARCHAR(8) PRIMARY KEY,
    name  VARCHAR(20) NOT NULL,
    class varchar(20) NOT NULL
);



CREATE TABLE IF NOT EXISTS courses
(
    cID          INT AUTO_INCREMENT PRIMARY KEY,
    courseID     INT,
    courseName   VARCHAR(20) NOT NULL,
    teacherName  varchar(8)  NOT NULL,
    courseType   CHAR        NOT NULL,
    coursePoint  INT         NOT NULL,
    studentCount INT         NOT NULL,
    courseClass  varchar(20) NOT NULL,
    courseDep    varchar(20) NOT NULL
);



CREATE TABLE IF NOT EXISTS courseTime
(
    courseTimeID INT PRIMARY KEY AUTO_INCREMENT,
    courseID     INT,
    courseDate   INT,
    coursePeriod INT
);



CREATE TABLE IF NOT EXISTS pickedList
(
    pickedID INT PRIMARY KEY AUTO_INCREMENT,
    stuID    varchar(8) NOT NULL,
    cID INT NOT NULL
);


CREATE TABLE IF NOT EXISTS watchList
(
    watchID INT PRIMARY KEY AUTO_INCREMENT,
    stuID   varchar(8) NOT NULL,
    cID     INT        NOT NULL
);
