# Duplicate Finder

A simple Java library and command-line application to detect duplicate elements in a stream.
The application is designed to handle both small and potentially very large (unbounded) streams while preserving the first-appearance order of the duplicates.

## Library: `StreamUtils`

The main functionality of the library is the `findDuplicates` method located in the `com.example.utils.StreamUtils` class.

### Method Signature
```java
public static <T extends Serializable> Stream<T> findDuplicates(Stream<T> list)
```

### Functionality
Given a `Stream` of objects, this method returns a new `Stream` that contains only the elements that appear more than once in the original stream. The returned stream contains the duplicate elements in the order of their first appearance. 

## Approach

To satisfy strict memory constraints, where both the total number of items (`N`) and the number of unique items (`U`) can be too large to fit in memory,this project implements a two-pass, on-disk algorithm.

This approach uses temporary files as external storage, leveraging the `<T extends Serializable>` constraint to handle data that exceeds available RAM.

#### The Process

The algorithm is executed in three main steps:

1.  **Pass 0: Stream Buffering**
    *   Since a Java `Stream` can only be consumed once, the entire incoming stream is first written to a temporary file on disk. This `buffer` file makes the stream accessible for the next passes, which is essential for a multi-pass algorithm.

2.  **Pass 1: Identification of Duplicate Values**
    *   A new stream is created from the `buffer` file.
    *   This pass iterates through the stream, using a *second* temporary file (`seen-elements`) as a database of unique items encountered so far.
    *   For each element, it performs a slow but low-memory check:
        *   If the element is already in the `seen-elements` file, it is a duplicate. Its value is added to an in-memory `HashSet` of confirmed duplicate values.
        *   If the element is not in the file, it is written to the file.
    *   The result of this pass is a `Set` containing all values that appear more than once in the stream. The order does not matter in this pass.

3.  **Pass 2: First-Appearance Order Discovery**
    *   A third stream is created from the `buffer` file to re-process the data in its original order.
    *   This stream is filtered, keeping only the elements that are present in the set of duplicate values from Pass 1.
    *   These elements are collected into a `LinkedHashSet`. Because a `LinkedHashSet` preserves insertion order and only stores each item once, this step efficiently captures each duplicate the *first* time it appears in the original stream.

#### Complexity and Trade-offs

This two-pass algorithm was chosen to correctly solve the problem with very low memory usage, but this comes at a significant cost to performance.

*   **Performance:** The process of repeatedly checking for elements in a growing temporary file (in Pass 1) is very slow for large inputs.
*   **Memory Usage:** The main advantage is its low memory footprint. It avoids storing all unique items in memory, only requiring enough space to hold the final set of duplicate values. It uses temporary disk space to store the full stream and the set of unique items seen so far.


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

This project features a standalone library and a Command-Line Interface (CLI) for easy usage. It can be run with a default set of data, or with user-provided input.
You can run the application from the command line using the generated JAR file in one of 3 modes:

- `java -jar target/duplicatefinder-1.0-SNAPSHOT.jar [options]`: To display the help message or to use a pre-defined default data stream (any other arguments will be ignored.)

- `java -jar target/duplicatefinder-1.0-SNAPSHOT.jar [data...]`: With a data stream provided directly as arguments. Any arguments that are not recognized as options will be treated as input data.

- `java -jar target/duplicatefinder-1.0-SNAPSHOT.jar < file.txt`: To read from standard input. If the program is run with no command-line arguments, it will automatically listen for standard input. 
**Note:** There is a file (input.txt) included in the project that can be used to test the application, which is the content of a book.

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
or:
java com.example.App < <file>

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
