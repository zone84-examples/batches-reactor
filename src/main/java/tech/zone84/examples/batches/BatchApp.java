package tech.zone84.examples.batches;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import tech.zone84.examples.batches.internal.DataGenerator;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchApp {
    private static final int BUFFER_SIZE = 50;
    private static final Duration BUFFER_TIMESPAN = Duration.ofMillis(100);
    private static final Logger LOG = LoggerFactory.getLogger(BatchApp.class);

    private static final Duration DIAGRAM_TIME_WINDOW = Duration.ofSeconds(1);
    private static final AtomicInteger secondCounter = new AtomicInteger(0);

    public static void main(String args[]) {
        var registry = new DeviceRegistry();
        var aggregator = new ChangeAggregator();
        var latch = new CountDownLatch(1);

        Flux.fromIterable(new DataGenerator())
            .subscribeOn(Schedulers.boundedElastic())
            .filterWhen(change -> registry.supportsProperty(change.deviceId(), change.property()))
            .bufferTimeout(BUFFER_SIZE, BUFFER_TIMESPAN)
            .flatMapSequential(batch -> Flux.fromIterable(aggregator.aggregate(batch)))
            .buffer(DIAGRAM_TIME_WINDOW)
            .doOnNext(element -> System.out.println(secondCounter.incrementAndGet()+";"+element.size()))
            .flatMapSequential(list -> Flux.fromIterable(list))
            .subscribe(
                change -> LOG.trace("Change received: " + change),
                error -> {
                    LOG.error("Processing failed", error);
                    latch.countDown();
                },
                () -> {
                    LOG.info("Finished");
                    latch.countDown();
                }
            );

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error("Sudden interruption", e);
        }
    }
}
