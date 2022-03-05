package tech.zone84.examples.batches.internal;

import com.google.common.collect.ImmutableList;
import tech.zone84.examples.batches.PropertyChange;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class DataGenerator implements Iterable<PropertyChange> {
    private static final int GENERATED_CHANGES = 20000;
    private static final List<String> PROPERTY_NAMES = ImmutableList.of(
        "temperature", "humidity", "pressure", "noise"
    );
    private final Random random = new Random(3823674);

    @Override
    public Iterator<PropertyChange> iterator() {
        final AtomicInteger counter = new AtomicInteger(0);
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return counter.getAndIncrement() < GENERATED_CHANGES;
            }

            @Override
            public PropertyChange next() {
                var value = counter.get();

                if (value < 5000 || value > 8000) {
                    try {
                        Thread.sleep(random.nextInt(0, 5));
                    } catch (InterruptedException exception) {
                        // intentionally empty
                    }
                }
                return new PropertyChange(
                    random.nextInt(1, 4),
                    PROPERTY_NAMES.get(random.nextInt(0, PROPERTY_NAMES.size())),
                    random.nextInt(-10, 10)
                );
            }
        };
    }
}
