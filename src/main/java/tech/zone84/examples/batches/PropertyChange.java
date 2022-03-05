package tech.zone84.examples.batches;

public record PropertyChange(
    int deviceId,
    String property,
    int change
) {
}
