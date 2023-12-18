package io.domotik8s.model.dev;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class PropertyTemplate {

    private PropertyType type;

    private Map<String, Object> addressSpec;

}
