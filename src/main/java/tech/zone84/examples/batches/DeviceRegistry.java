package tech.zone84.examples.batches;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import reactor.core.publisher.Mono;

public class DeviceRegistry {
    private Multimap<Integer, String> support = ImmutableSetMultimap.<Integer, String>builder()
        .putAll(1, "temperature", "humidity")
        .putAll(2, "pressure", "noise")
        .putAll(3, "temperature", "pressure")
        .build();

    public Mono<Boolean> supportsProperty(int deviceId, String propertyName) {
        return Mono.fromSupplier(() -> support.containsEntry(deviceId, propertyName));
    }
}
