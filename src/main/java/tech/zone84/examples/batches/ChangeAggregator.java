package tech.zone84.examples.batches;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeAggregator {
    public Collection<AggregatedPropertyChange> aggregate(List<PropertyChange> batch) {
        var buckets = Maps.<Bucket, Aggregation>newLinkedHashMap();
        for (var item: batch) {
            var aggregation = buckets.computeIfAbsent(new Bucket(item.deviceId(), item.property()), bucket -> new Aggregation());
            aggregation.aggregate(item.change());
        }
        return buckets.entrySet()
            .stream()
            .map(entry -> entry.getValue().toPropertyChange(entry.getKey()))
            .collect(Collectors.toList());
    }

    record Bucket(int deviceId, String propertyName) {}

    private class Aggregation {
        private int change = 0;
        private int mergedChanges = 0;

        void aggregate(int valueChange) {
            change += valueChange;
            mergedChanges++;
        }

        AggregatedPropertyChange toPropertyChange(Bucket bucket) {
            return new AggregatedPropertyChange(
                bucket.deviceId(), bucket.propertyName(), change, mergedChanges
            );
        }
    }
}
