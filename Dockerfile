# Perform the extraction in a separate builder container
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /builder
# This points to the built jar file in the target folder
# Adjust this to 'build/libs/*.jar' if you're using Gradle
ARG JAR_FILE=build/libs/*.jar
# Copy the jar file to the working directory and rename it to application.jar
COPY ${JAR_FILE} application.jar
# Extract the jar file using an efficient layout
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# This is the runtime container
FROM eclipse-temurin:17-jdk
WORKDIR /application
# Copy the extracted jar contents from the builder container into the working directory in the runtime container
# Every copy step creates a new docker layer
# This allows docker to only pull the changes it really needs
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
# Execute the CDS training run
RUN java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -jar application.jar
# Start the application jar with CDS enabled - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and CDS friendly
ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa", "-jar", "application.jar"]
