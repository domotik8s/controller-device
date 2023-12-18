package io.domotik8s.model.dev;

import io.domotik8s.model.PropertyState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public class DeviceStatus {

    private OffsetDateTime lastUpdated;

    private Map<String, PropertyState<?>> state;

}
