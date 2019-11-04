package dev.todaka.thetasketchbenchmark;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import com.yahoo.memory.Memory;
import com.yahoo.sketches.theta.Intersection;
import com.yahoo.sketches.theta.SetOperation;
import com.yahoo.sketches.theta.Sketch;
import com.yahoo.sketches.theta.Sketches;
import com.yahoo.sketches.theta.UpdateSketch;

public final class BenchmarkApplication {
    public static void main(String[] args) throws Exception {
        final long entries = 4_000_000;
        final long offset = 2_000_000;
        final int nominalEntries = 1 << 16;

        testWithNativeSet(entries, offset);
        testWithThetaSketch(entries, offset, nominalEntries);
    }

    private static void testWithNativeSet(long entries, long offset) throws Exception {
        // write file
        try (
                final OutputStream out1 = new BufferedOutputStream(new FileOutputStream("nativeSet1.bin"));
                final OutputStream out2 = new BufferedOutputStream(new FileOutputStream("nativeSet2.bin"))
        ) {
            for (long i = 0; i < entries; i++) {
                out1.write(ByteBuffer.allocate(Long.BYTES).putLong(i).array());
                out2.write(ByteBuffer.allocate(Long.BYTES).putLong(i + offset).array());
            }
        }

        // read file
        final long nativeSetTime = executeNTimes(10, () -> {
            final Set<Long> set1 = new HashSet<>();
            final Set<Long> set2 = new HashSet<>();
            try (
                    final InputStream in1 = new BufferedInputStream(new FileInputStream("nativeSet1.bin"));
                    final InputStream in2 = new BufferedInputStream(new FileInputStream("nativeSet2.bin"))
            ) {
                for (long i = 0; i < entries; i++) {
                    final byte[] buffer = new byte[8];
                    in1.read(buffer);
                    set1.add(ByteBuffer.wrap(buffer).getLong());
                    in2.read(buffer);
                    set2.add(ByteBuffer.wrap(buffer).getLong());
                }
            }
            set1.retainAll(set2);
            System.out.println("Intersection is " + set1.size());
            return null;
        });
        System.out.println("Avg time of native set : " + nativeSetTime + "ms");
    }

    private static void testWithThetaSketch(long entries, long offset, int nominalEntries) throws Exception {
        // write file
        try (
                final OutputStream out1 = new BufferedOutputStream(new FileOutputStream("sketch1.bin"));
                final OutputStream out2 = new BufferedOutputStream(new FileOutputStream("sketch2.bin"))
        ) {
            final UpdateSketch sketch1 = UpdateSketch
                    .builder().setNominalEntries(nominalEntries).build();
            final UpdateSketch sketch2 = UpdateSketch
                    .builder().setNominalEntries(nominalEntries).build();
            for (long i = 0; i < entries; i++) {
                sketch1.update(i);
                sketch2.update(i + offset);
            }
            out1.write(sketch1.compact().toByteArray());
            out2.write(sketch2.compact().toByteArray());
        }

        // read file
        final long sketchTime = executeNTimes(10, () -> {
            final Sketch sketch1;
            final Sketch sketch2;
            try (
                    final InputStream in1 = new BufferedInputStream(new FileInputStream("sketch1.bin"));
                    final InputStream in2 = new BufferedInputStream(new FileInputStream("sketch2.bin"))
            ) {
                final byte[] buffer1 = new byte[in1.available()];
                final byte[] buffer2 = new byte[in2.available()];
                in1.read(buffer1);
                in2.read(buffer2);
                sketch1 = Sketches.wrapSketch(Memory.wrap(buffer1));
                sketch2 = Sketches.wrapSketch(Memory.wrap(buffer2));
            }
            final Intersection intersection = SetOperation
                    .builder().setNominalEntries(nominalEntries).buildIntersection();
            intersection.intersect(sketch1, sketch2);
            System.out.println("Intersection is " + intersection.getResult().getEstimate());
            return null;
        });

        System.out.println("Avg time of theta sketch : " + sketchTime + "ms");
    }

    private static long executeNTimes(int times, Callable<Void> callable) throws Exception {
        final long started = System.currentTimeMillis();
        for(int i = 0; i < times; i++) {
            callable.call();
        }
        return (System.currentTimeMillis() - started) / times;
    }

    private BenchmarkApplication() {}
}
