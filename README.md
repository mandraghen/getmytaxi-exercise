# getmytaxi-exercise
Implementation of the code challenge provided by akamas.
This application requires 3 inputs from the command line, that are paths to json files that contains:
1) the information about the map and its structure, including walls and checkpoints
2) the location of the cabs (taxi)
3) the location of the starting point and the destination point

A set of example files are provided in the src/test/resources/mocks folder inside the project.

## Consideration
I had initially chosen to implement the solution with plain java 17 JDK and Gradle, since it doesn't require any server or web capabilities, however I found out that I could use spring boot including only the core part (DI most important), without starting a web container.
In a more realistic scenario, I would have probably implemented an API within a web application, using spring boot or similar framework, to expose the functionality of the application to any client. Depending on the requirements, it could have been deployed as a serverless application.

## Assumptions
In the provided json, there are some mistakes I fixed:
- The attribute "height" has a typo, I changed it from "hight" to "height".
- One wall segment is not consistent: 
  {
  "x1": 10,
  "y1": 8,
  "x2": 11,
  "y2": 9
  }
changed to
  {
  "x1": 10,
  "y1": 8,
  "x2": 10,
  "y2": 9
  }
based on the consistent assumption that movements are only allowed in the x or y axis and not diagonally and as 
consequence walls are also only vertical or horizontal.

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:
- Java 17 or higher

### Build and test
1. Clone the repository:
   ```bash
   git clone https://github.com/mandraghen/getmytaxi-exercise.git
   cd getmytaxi-exercise
   ```
2. Run build and tests
   ```bash
   ./gradlew build
   ```
This will create executables in the ./build/libs folder, including the jar file with all dependencies.

### Running the application from command line
   ```bash
   cd ./build/libs
   java -jar getmytaxi-exercise-1.0-SNAPSHOT.jar ../../src/test/resources/mocks/test1/taxi_map.json ../../src/test/resources/mocks/test1/taxi_coordinates.json ../../src/test/resources/mocks/test1/request.json
   ```
or from the project root folder:
   ```bash
   ./gradlew bootRun --args="./src/test/resources/mocks/test1/taxi_map.json ./src/test/resources/mocks/test1/taxi_coordinates.json ./src/test/resources/mocks/test1/request.json"
   ```
this will also run the build and tests if needed.

### Running the application using docker


## Points of improvement
- Use interfaces: despite nowadays someone thinks that interfaces are not needed anymore when there is only one implementation, I think they are useful to decouple the implementation from the declaration, also from the DI perspective. This makes also clearer which methods can be exposed to other classes.
- Complete test coverage: unit tests should cover ideally 100% of the code. This is not really useful for this exercise.
