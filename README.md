# Stage 1 - Duplicate Finder

A simple Java library and command-line application to detect duplicate elements in a stream, while maintaining the order of their first appearance. 
This project features a standalone library and a Command-Line Interface (CLI) for easy usage. It can be run with a default set of data, or with user-provided input.

## Library: `StreamUtils`

The main functionality of the library is the `findDuplicates` method located in the `com.example.utils.StreamUtils` class.

### Method Signature
```java
public static <T extends Serializable> Stream<T> findDuplicates(Stream<T> list)
```

### Functionality
Given a `Stream` of objects, this method returns a new `Stream` that contains only the elements that appear more than once in the original stream. The returned stream contains the duplicate elements in the order of their first appearance.

## Prerequisites & Compatibility

To build and run this project, you will need:

*   Java (JDK 17+)
*   Apache Maven 3.x

## Build

To compile the source code, run the tests, and package the application into an executable JAR file, run the following command from the project's root directory:

```bash
mvn clean install
```

This will generate a `duplicatefinder-1.0-SNAPSHOT.jar` file in the `target/` directory.

## Usage

You can run the application from the command line using the generated JAR file in one of 2 modes:

- `java -jar target/duplicatefinder-1.0-SNAPSHOT.jar [options]`: To display the help message or to use a pre-defined default data stream (any other arguments will be ignored.)

- `java -jar target/duplicatefinder-1.0-SNAPSHOT.jar [data...]`: With a data stream provided directly as arguments. Any arguments that are not recognized as options will be treated as input data.

### Options

| Option      | Short Form | Description                                       |
|-------------|------------|---------------------------------------------------|
| `--help`    | `-h`       | Display the help message.                         |
| `--default` | `-d`       | Run with a default sample data stream.            |
| `<data...>` |            | Use your own space-separated data as input.       |

### Examples

#### 1. Display Help Message

To see all available options, use the `--help` or `-h` flag:

```shell
java -jar target/duplicatefinder-1.0-SNAPSHOT.jar --help
```
**Output:**
```
============================================
                   Help
============================================

Description:
This program detects duplicate elements in a stream of data.

Usage:
java com.example.App [options] 
or:
java com.example.App [data...]

Argument options:
-h, --help      Display this help message.
-d, --default   Use default sample data. Runs the program with a pre-defined list of strings.
<data...>       Use your own data (separated by spaces) as input.

Example:
java com.example.App -h
java com.example.App -d
java com.example.App 1 2 3 2 4 1 5
```

#### 2. Run with Default Data

To run the application with a pre-defined list of characters, use the `--default` or `-d` flag:

```shell
java -jar target/duplicatefinder-1.0-SNAPSHOT.jar --default
```
**Output:**
```
Original stream: 
[b, a, c, c, e, a, c, d, c, d]
Detected duplicates:
[a, c, d]
```

#### 3. Run with User Provided Data

To find duplicates in your own data, use your own data as input separated by spaces:
```shell
java -jar target/duplicatefinder-1.0-SNAPSHOT.jar banana kiwi apple cherry cherry orange  banana apple
```
**Output:**
```
Original stream: 
[banana, kiwi, apple, cherry, cherry, orange, banana, apple]
Detected duplicates:
[banana, apple, cherry]
```

## Running Tests

To run the automated tests for this project, the following Maven command should be run from the project's root directory:

```shell
mvn test
```
