package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.utils.StreamUtils;

/**
 * A command-line application to find duplicate elements in a stream of data.
 * This application can be run with default data or with user-provided input.
 */
public class App {
    /**
     * The main entry point for the application.
     * Parses command-line arguments to determine the mode of operation.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No command-line arguments detected. Reading from standard input...");
            readFromStdIn();
            System.exit(0);
        }
        boolean helpFlag = false;
        boolean defaultFlag = false;
        List<String> inData = new ArrayList<>();

        /* parse the arguments to determine the mode of operation */
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                case "-h":
                    helpFlag = true;
                    break;
                case "--default":
                case "-d":
                    defaultFlag = true;
                    break;
                default:
                    inData.add(args[i]);
                    break;
            }
        }

        /*
         * if no arguments or the help argument was provided,
         * then ignore any other arguments, display the help message and exit
         */
        if (helpFlag) {
            displayHelpMessage();
            System.exit(0);
        }

        /*
         * if the default argument was provided, ignore any other arguments and
         * find the duplicates in the default stream and exit
         */
        if (defaultFlag) {
            if (!inData.isEmpty()) {
                System.out.println("Default argument (-d) was provided." +
                        " Ignoring additional arguments and using default stream.");
            }
            duplicateFinder(List.of("b", "a", "c", "c", "e", "a", "c", "d", "c", "d"));
            System.exit(0);
        }

        /**
         * if input data was provided, find the duplicates in the stream and exit
         */
        if (!inData.isEmpty()) {
            duplicateFinder(inData);
            System.exit(0);
        }
    }

    /**
     * Displays the help message.
     */
    private static void displayHelpMessage() {
        System.out.println("============================================");
        System.out.println("                   Help");
        System.out.println("============================================");
        System.out.println("\nDescription:");
        System.out.println("This program detects duplicate elements in a " +
                "stream of data.");
        System.out.println("\nUsage:");
        System.out.println("java com.example.App [options]");
        System.out.println("or:");
        System.out.println("java com.example.App [data...]");
        System.out.println("\nArgument options:");
        System.out.println("-h, --help \tDisplay this help message.");
        System.out.println("-d, --default \tUse default sample data. Runs the" +
                " program with a pre-defined list of strings.");
        System.out.println("<data...> \tUse your own data (separated by spaces) as input.");
        System.out.println("\nExample:");
        System.out.println("java -jar target/duplicatefinder-1.0-SNAPSHOT.jar -h");
        System.out.println("java -jar target/duplicatefinder-1.0-SNAPSHOT.jar -d");
        System.out.println("java -jar target/duplicatefinder-1.0-SNAPSHOT.jar 1 2 3 2 4 1 5");
    }

    /**
     * Finds the duplicates in the given input stream.
     * 
     * @param inputStream The list of strings provided by the user.
     */
    private static void duplicateFinder(List<String> inputStream) {
        System.out.println("Original stream: ");
        System.out.println(inputStream);
        System.out.println("Detected duplicates:");
        List<String> duplicates = StreamUtils.findDuplicates(inputStream
                .stream()).collect(Collectors.toList());
        System.out.println(duplicates);
    }

    private static void readFromStdIn() {
        try (Scanner sc = new Scanner(System.in)) {
            /* Get the input stream from the standard input */
            Stream<String> inputStream = sc.tokens();
            /* Use findDuplicates directly on the stream */
            Stream<String> duplicates = StreamUtils.findDuplicates(inputStream);
            /* Print the duplicates, assuming they fit in memory - if not, comment this out */
            System.out.println("Detected duplicates: ");
            System.out.println(duplicates.collect(Collectors.toList()));
        } 
    }
}
