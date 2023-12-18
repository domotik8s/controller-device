package io.domotik8s.model.bool;

import io.domotik8s.model.PropertyAccess;
import io.domotik8s.model.PropertySpec;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
public class BooleanPropertySpec implements PropertySpec<BooleanPropertyState> {

    private Set<PropertyAccess> access;

    private Map<String, Object> address;

    private BooleanPropertyState state;

    private Boolean locked;

}
