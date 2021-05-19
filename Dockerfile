FROM openjdk:15

COPY . /project
WORKDIR /project
RUN ./gradlew installDist
CMD ["/project/build/install/GetCourse/bin/GetCourse"]
