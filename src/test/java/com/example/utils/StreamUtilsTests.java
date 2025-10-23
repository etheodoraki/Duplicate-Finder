package com.example.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

@DisplayName("Stream Utils Tests")
public class StreamUtilsTests {
    
    @DisplayName("Test findDuplicates with char entries")
    @Test
    public void givenCharEntries_whenFindDuplicates_thenReturnDuplicates() {
        Stream<String> inputStream = Stream.of("b", "a", "c", "c", "e", "a", "c", "d", "c", "d");
        List<String> result = StreamUtils.findDuplicates(inputStream).collect(Collectors.toList());
        assertEquals(List.of("a", "c", "d"), result);
    }


    @DisplayName("Test findDuplicates with string entries")
    @Test
    public void givenStringEntries_whenFindDuplicates_thenReturnDuplicates() {
        Stream<String> inputStream = Stream.of("banana", "apple", "cherry", "apple", "kiwi", "banana", "cherry");
        List<String> result = StreamUtils.findDuplicates(inputStream).collect(Collectors.toList());
        assertEquals(List.of("banana", "apple", "cherry"), result);
    }

    @DisplayName("Test findDuplicates with integer entries")
    @Test
    public void givenIntegerEntries_whenFindDuplicates_thenReturnDuplicates() {
        Stream<Integer> inputStream = Stream.of(1, 2, 3, 2, 4, 1, 5, 3);
        List<Integer> expected = List.of(1, 2, 3);
        List<Integer> result = StreamUtils.findDuplicates(inputStream).collect(Collectors.toList());
        assertEquals(expected, result);
    }
    
    @DisplayName("Test findDuplicates with no duplicates")
    @Test
    public void testFindDuplicates_NoDuplicates() {
        Stream<String> inputStream = Stream.of("a", "b", "c", "d", "e");
        List<String> expected = List.of();
        List<String> result = StreamUtils.findDuplicates(inputStream).collect(Collectors.toList());
        assertEquals(expected, result);
    }

    @DisplayName("Test findDuplicates with all duplicates")
    @Test
    public void givenAllDuplicates_whenFindDuplicates_thenReturnAllDuplicates() {
        Stream<String> inputStream = Stream.of("a", "a", "b", "b");
        List<String> expected = List.of("a", "b");
        List<String> result = StreamUtils.findDuplicates(inputStream).collect(Collectors.toList());
        assertEquals(expected, result);
    }

    @DisplayName("Test findDuplicates with empty stream")
    @Test
    public void givenEmptyStream_whenFindDuplicates_thenReturnEmptyList() {
        Stream<String> inputStream = Stream.empty();
        List<String> expected = List.of();
        List<String> result = StreamUtils.findDuplicates(inputStream).collect(Collectors.toList());
        assertEquals(expected, result);
    }

    @DisplayName("Test findDuplicates with null stream")
    @Test
    public void givenNullStream_whenFindDuplicates_thenReturnIllegalArgumentException() {
        Stream<String> inputStream = null;
        assertThrows(IllegalArgumentException.class, () -> StreamUtils.findDuplicates(inputStream));
    }

}
