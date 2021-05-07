CREATE TABLE IF NOT EXISTS students
(
    stuID VARCHAR(8) PRIMARY KEY,
    name  VARCHAR(20) NOT NULL,
    class varchar(20) NOT NULL
);

INSERT INTO students(stuID, name, class)
VALUES ('D1234567', '王小明', '資訊一甲');

INSERT INTO students(stuID, name, class)
VALUES ('D2345678', '王大明', '資訊二丙');

INSERT INTO students(stuID, name, class)
VALUES ('D3456789', '王中明', '資訊三乙');



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
    courseID INT NOT NULL
);


CREATE TABLE IF NOT EXISTS watchList
(
    watchID  INT PRIMARY KEY AUTO_INCREMENT,
    stuID    varchar(8) NOT NULL,
    courseID INT NOT NULL
);
