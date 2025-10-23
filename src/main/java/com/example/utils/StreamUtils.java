package com.example.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class StreamUtils {

	/**
	 * Detects all duplicate elements in the provided stream.
	 * 
	 * @param <T>  Type of the stream elements.
	 * @param list Input stream to be checked for duplicates.
	 * @return A stream of duplicate elements ordered by their first appearance.
	 */
	public static <T extends Serializable> Stream<T> findDuplicates(Stream<T> list) {
		/* STAGE 1 */
		if (list == null) {
			throw new IllegalArgumentException("Null input stream detected.");
		}
		/* a list to keep the elements of the stream (in their original order) to avoid consuming the stream */
		List<T> elements = list.toList();
		/* a map to keep the count of appearances of each element */
		HashMap<T, Integer> appearancesMap = new HashMap<>();
		elements.forEach(element -> appearancesMap.put(element, appearancesMap.getOrDefault(element, 0) + 1));

		/* filter out the elements with count = 1 */
		return elements.stream().filter(element -> appearancesMap.get(element) > 1).distinct();
	}
}
