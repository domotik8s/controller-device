package io.domotik8s.model.dev;

import io.domotik8s.model.PropertyState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class DeviceSpec {

    private Map<String, PropertyTemplate> properties;

    private Map<String, PropertyState<?>> state;

}
