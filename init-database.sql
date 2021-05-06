CREATE TABLE IF NOT EXISTS students
(
    stuID VARCHAR(8) PRIMARY KEY,
    name  VARCHAR(20) NOT NULL,
    dep   VARCHAR(10) NOT NULL,
    grade INT         NOT NULL,
    class INT         NOT NULL
);

INSERT INTO students(stuID, name, dep, grade, class)
VALUES ('D1234567', '王小明', '資訊', 1, 1);

INSERT INTO students(stuID, name, dep, grade, class)
VALUES ('D2345678', '王大明', '資訊', 2, 3);

INSERT INTO students(stuID, name, dep, grade, class)
VALUES ('D3456789', '王中明', '資訊', 3, 2);



CREATE TABLE IF NOT EXISTS teachers
(
    teacherID   VARCHAR(8) PRIMARY KEY,
    teacherName VARCHAR(20) NOT NULL
);

INSERT INTO teachers(teacherID, teacherName)
VALUES ('T001', '許懷中');

INSERT INTO teachers(teacherID, teacherName)
VALUES ('T002', '劉宗杰');


CREATE TABLE IF NOT EXISTS courses
(
    courseID     INT PRIMARY KEY,
    courseName   VARCHAR(20) NOT NULL,
    teacherID    varchar(8)  NOT NULL,
    courseType   INT         NOT NULL, # 0: 必修, 1: 選修, 2: 通識
    coursePoint  INT         NOT NULL,
    studentCount INT         NOT NULL,
    courseDep    varchar(20) NOT NULL,
    courseGrade  INT         NOT NULL,
    courseClass  INT         NOT NULL,

    FOREIGN KEY (teacherID) REFERENCES teachers (teacherID)
);

INSERT INTO courses(courseID, courseName, teacherID, courseType, coursePoint, studentCount, courseDep, courseGrade,
                    courseClass)
VALUES (1311, '資料庫系統', 'T001', 0, 3, 50, '資訊', 2, 3);

INSERT INTO courses(courseID, courseName, teacherID, courseType, coursePoint, studentCount, courseDep, courseGrade,
                    courseClass)
VALUES (1310, '系統程式', 'T002', 0, 3, 50, '資訊', 2, 3);



CREATE TABLE IF NOT EXISTS courseTime
(
    courseTimeID INT PRIMARY KEY AUTO_INCREMENT,
    courseID     INT,
    courseDate   INT,
    coursePeriod INT,

    FOREIGN KEY (courseID) REFERENCES courses (courseID)
);

INSERT INTO courseTime(courseID, courseDate, coursePeriod)
VALUES (1311, 1, 2);

INSERT INTO courseTime(courseID, courseDate, coursePeriod)
VALUES (1311, 4, 6);

INSERT INTO courseTime(courseID, courseDate, coursePeriod)
VALUES (1311, 4, 7);

INSERT INTO courseTime(courseID, courseDate, coursePeriod)
VALUES (1310, 1, 3);

INSERT INTO courseTime(courseID, courseDate, coursePeriod)
VALUES (1310, 3, 8);

INSERT INTO courseTime(courseID, courseDate, coursePeriod)
VALUES (1310, 3, 9);



CREATE TABLE IF NOT EXISTS pickedList
(
    pickedID INT PRIMARY KEY AUTO_INCREMENT,
    stuID    varchar(8) NOT NULL,
    courseID INT        NOT NULL,

    FOREIGN KEY (stuID) REFERENCES students (stuID),
    FOREIGN KEY (courseID) REFERENCES courses (courseID)
);

INSERT INTO pickedList(stuID, courseID)
VALUES ('D2345678', 1311);



CREATE TABLE IF NOT EXISTS watchList
(
    watchID  INT PRIMARY KEY AUTO_INCREMENT,
    stuID    varchar(8) NOT NULL,
    courseID INT        NOT NULL,

    FOREIGN KEY (stuID) REFERENCES students (stuID),
    FOREIGN KEY (courseID) REFERENCES courses (courseID)
);
