package tech.zone84.examples.batches;

public record AggregatedPropertyChange(
    int deviceId,
    String property,
    int change,
    int mergedCount
) {
}
