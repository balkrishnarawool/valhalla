package valueobjects;

import jdk.internal.value.ValueClass;

import java.util.Objects;
import java.util.function.Supplier;

public class FlattenedObjectsDemo {
    private static final int ARRAY_SIZE = 10_000_000;

    static class TraditionalPoint {
        final int x;
        final int y;

        public TraditionalPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static value class ValhallaPoint {
        int x;
        int y;

        public ValhallaPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static <T> long profile(Supplier<T[]> arraySupplier) throws InterruptedException {
        cleanGarbageCollector();
        var memoryBefore = getUsedMemory();

        T[] array = arraySupplier.get();

        var memoryAfter = getUsedMemory();
        var bytesUsed = memoryAfter - memoryBefore;

        // Prevent the JIT compiler from optimizing the array away early
        Objects.requireNonNull(array[0]);
        return bytesUsed;
    }

    // Calculates current heap utilization
    private static long getUsedMemory() {
        var runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    // Stabilizes memory baseline readings before running allocations
    private static void cleanGarbageCollector() throws InterruptedException {
        System.gc();
        Thread.sleep(100);
        System.gc();
        Thread.sleep(100);
    }

    private static long profileTraditionalArray() throws InterruptedException {
        return profile(() -> {
            // Allocate the array AND instantiate the separate objects (pointer soup)
            var legacyArray = new TraditionalPoint[ARRAY_SIZE];
            for (int i = 0; i < ARRAY_SIZE; i++) {
                legacyArray[i] = new TraditionalPoint(i, i);
            }
            return legacyArray;
        });
    }

    private static long profileValhallaArray() throws InterruptedException {
        return profile(() -> {
            // This ensures the JVM treats it like a raw primitive array (int[] / long[])
            var flatArray = (ValhallaPoint[]) ValueClass.newNullRestrictedNonAtomicArray(
                    ValhallaPoint.class, ARRAY_SIZE, new ValhallaPoint(0, 0)
            );
            for (int i = 0; i < ARRAY_SIZE; i++) {
                flatArray[i] = new ValhallaPoint(i, i);
            }
            return flatArray;
        });
    }

    static void main() throws InterruptedException {
        var traditionalBytesUsed = profileTraditionalArray();
        var valhallaBytesUsed = profileValhallaArray();

        var efficiency = (double) (traditionalBytesUsed - valhallaBytesUsed) / traditionalBytesUsed;
        System.out.printf("Traditional Identity Array: %.2f MB%n", traditionalBytesUsed / (1024.0 * 1024.0));
        System.out.printf("Valhalla Value Array:  %.2f MB%n", valhallaBytesUsed / (1024.0 * 1024.0));
        System.out.printf("Valhalla memory efficiency: %.2f%%!%n", efficiency * 100);
    }
}
