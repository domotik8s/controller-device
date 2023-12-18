package io.domotik8s.model.num;

import io.domotik8s.model.Property;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NumberProperty implements Property<NumberPropertySpec, NumberPropertyStatus> {

    private final String apiVersion = "domotik8s.io/v1beta1";

    private final String kind = "NumberProperty";

    private V1ObjectMeta metadata;

    private NumberPropertySpec spec;

    private NumberPropertyStatus status;

}
