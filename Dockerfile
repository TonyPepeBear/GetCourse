FROM openjdk:15 as builder

COPY . /project
WORKDIR /project
RUN ./gradlew installDist

FROM openjdk

COPY --from=builder /project/build/install/GetCourse /project
CMD ["/project/bin/GetCourse"]
