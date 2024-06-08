FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2023-headless

RUN yum update -y
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

ARG JAR_FILE=build/libs/todo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENV PORT 80
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "app.jar"]