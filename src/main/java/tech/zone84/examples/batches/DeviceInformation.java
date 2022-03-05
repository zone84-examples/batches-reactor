package tech.zone84.examples.batches;

import java.io.StringBufferInputStream;
import java.util.Set;

public record DeviceInformation(
    int id,
    Set<String> supportedProperties
) {
}
