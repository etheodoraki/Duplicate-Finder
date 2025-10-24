package com.example.utils;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

	/**
	 * Detects duplicate elements in a stream using a two-pass, on-disk algorithm.
	 * This approach is designed for streams where the number of unique items is too large
	 * to fit in memory, satisfying the strict Stage 2 memory constraint.
	 *
	 * @param <T>  Type of the stream elements.
	 * @param list Input stream to be checked for duplicates.
	 * @return A stream of duplicate elements ordered by their first appearance.
	 */
	public static <T extends Serializable> Stream<T> findDuplicates(Stream<T> list) {
		if (list == null) {
			throw new IllegalArgumentException("Null input stream detected.");
		}

		/* alternative approach - efficient for small datasets */
        // /* Use a linked hash map to store the duplicates, preserving the order of the first appearance */
        // Map<T, Boolean> duplicateMap = new LinkedHashMap<>();
        // /* Iterate over the stream and put the items in the map */
        // list.forEach(item -> {
		// 	/* If the item is already in the map, set the value to true */
		// 	duplicateMap.put(item, duplicateMap.containsKey(item));
        // });
        // /* return the keys of the duplicateMap where the value is true */
        // return duplicateMap.entrySet().stream().filter(entry -> entry.getValue()).map(Map.Entry::getKey);

		Path bufferFile = null;
		try {
			/* Pass 0: buffer the stream to a file to make it replayable for the two passes */
			bufferFile = bufferStreamToFile(list);

			/* Pass 1: identify all values that appear more than once. Order doesn't matter here */
			Set<T> duplicateValues = findDuplicateValues(streamFromFile(bufferFile));

			/* Pass 2: stream from the buffer to find the first appearance of the identified duplicates */
			Set<T> orderedDuplicates = findFirstAppearances(streamFromFile(bufferFile), duplicateValues);

			return orderedDuplicates.stream();

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			/* ensure the buffer file is deleted */
			if (bufferFile != null) {
				try {
					Files.deleteIfExists(bufferFile);
				} catch (IOException e) {
					/* log a warning if deletion fails, without crashing the application */
					System.err.println("Warning: Failed to delete buffer file: " + bufferFile);
				}
			}
		}
	}


	/**
	 * find the duplicate values in the stream
	 * @param <T>
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static <T extends Serializable> Set<T> findDuplicateValues(Stream<T> stream) throws IOException {
		Path seenFile = null;
		try {
			seenFile = Files.createTempFile("pass1-seen-", ".dat");
			Set<T> duplicates = new HashSet<>();

			final Path finalSeenFile = seenFile;
			stream.forEach(element -> {
				try {
					if (isElementInFile(finalSeenFile, element)) {
						duplicates.add(element);
					} else {
						appendElementToFile(finalSeenFile, element);
					}
				} catch (IOException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			});
			return duplicates;
		} finally {
			if (seenFile != null) {
				Files.deleteIfExists(seenFile);
			}
		}
	}

	/**
	 * find the first appearance of the identified duplicates
	 * @param <T>
	 * @param stream
	 * @param duplicateValues
	 * @return
	 */
	private static <T extends Serializable> Set<T> findFirstAppearances(Stream<T> stream, Set<T> duplicateValues) {
		Set<T> orderedDuplicates = new LinkedHashSet<>();
		stream.filter(element -> duplicateValues.contains(element))
				.forEach(element -> orderedDuplicates.add(element));
		return orderedDuplicates;
	}


	/**
	 * buffer the stream to a file
	 * @param <T>
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static <T extends Serializable> Path bufferStreamToFile(Stream<T> stream) throws IOException {
		Path tempFile = Files.createTempFile("stream-buffer-", ".dat");
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
			stream.forEach(item -> {
				try {
					oos.writeObject(item);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
		return tempFile;
	}

	/**
	 * stream from a file
	 * @param <T>
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static <T extends Serializable> Stream<T> streamFromFile(Path file) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file));
		Iterator<T> iterator = new Iterator<>() {
			T nextItem = null;
			boolean hasNextCalled = false;

			@Override
			public boolean hasNext() {
				if (hasNextCalled) {
					return nextItem != null;
				}
				hasNextCalled = true;
				try {
					nextItem = (T) ois.readObject();
					return true;
				} catch (EOFException e) {
					nextItem = null;
					return false;
				} catch (IOException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public T next() {
				/* To conform to the Iterator contract, hasNext() must be called before next().
				This ensures the next item is loaded or we know the stream is exhausted. */
				if (!hasNextCalled) {
					hasNext();
				}
				if (nextItem == null) {
					throw new NoSuchElementException();
				}
				
				T itemToReturn = nextItem;
				/* Reset state for the next cycle */
				nextItem = null;
				hasNextCalled = false;
				return itemToReturn;
			}
		};

		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
				.onClose(() -> {
					try {
						ois.close();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
	}


	/**
	 * check if the element is in the file
	 * @param <T>
	 * @param file
	 * @param target
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static <T extends Serializable> boolean isElementInFile(Path file, T target)
			throws IOException, ClassNotFoundException {
		if (Files.size(file) == 0) return false;

		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
			while (true) {
				try {
					if (target.equals(ois.readObject())) return true;
				} catch (EOFException e) {
					return false;
				}
			}
		}
	}

	/**
	 * append the element to the file
	 * @param <T>
	 * @param file
	 * @param element
	 * @return
	 * @throws IOException
	 */
	private static <T extends Serializable> void appendElementToFile(Path file, T element) throws IOException {
		long size = Files.exists(file) ? Files.size(file) : 0;
		try (ObjectOutputStream oos = (size == 0)
				? new ObjectOutputStream(Files.newOutputStream(file))
				: new AppendingObjectOutputStream(Files.newOutputStream(file, StandardOpenOption.APPEND))) {
			oos.writeObject(element);
		}
	}

	/**
	 * appending object output stream
	 * @param <T>
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private static class AppendingObjectOutputStream extends ObjectOutputStream {
		public AppendingObjectOutputStream(OutputStream out) throws IOException {
			super(out);
		}

		@Override
		protected void writeStreamHeader() throws IOException {
			reset();
		}
	}
}