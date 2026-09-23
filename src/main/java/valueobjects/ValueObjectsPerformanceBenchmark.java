package valueobjects;

import jdk.internal.value.ValueClass;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ValueObjectsPerformanceBenchmark {
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

    private TraditionalPoint[] legacyArray;
    private ValhallaPoint[] flatArray;

    @Setup
    public void setup() {
        // Initialize Traditional Array
        legacyArray = new TraditionalPoint[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            legacyArray[i] = new TraditionalPoint(i, i);
        }

        // Initialize Valhalla Flat Array using the explicit internal API
        flatArray = (ValhallaPoint[]) ValueClass.newNullRestrictedNonAtomicArray(
                ValhallaPoint.class, ARRAY_SIZE, new ValhallaPoint(0, 0)
        );
        for (int i = 0; i < ARRAY_SIZE; i++) {
            flatArray[i] = new ValhallaPoint(i, i);
        }
    }

    @Benchmark
    public long sumTraditional() {
        var sum = 0L;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sum += legacyArray[i].x + legacyArray[i].y;
        }
        return sum;
    }

    @Benchmark
    public long sumValhalla() {
        var sum = 0L;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sum += flatArray[i].x + flatArray[i].y;
        }
        return sum;
    }

    @Benchmark
    public long sumValhallaOptimized() {
        long sum = 0;
        // Local copy to encourage local loop optimization
        final var localArray = this.flatArray;

        for (int i = 0; i < ARRAY_SIZE; i++) {
            // By pulling the element into a local block variable, we signal
            // to the JIT compiler to scalarize the values into registers immediately.
            ValhallaPoint point = localArray[i];
            sum += point.x + point.y;
        }
        return sum;
    }

    static void main() throws Exception {
        var opt = new OptionsBuilder().include(ValueObjectsPerformanceBenchmark.class.getSimpleName()).build();
        new Runner(opt).run();
    }
}
