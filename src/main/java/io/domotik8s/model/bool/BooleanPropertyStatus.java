package io.domotik8s.model.bool;

import io.domotik8s.model.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BooleanPropertyStatus implements PropertyStatus<BooleanPropertyState> {

    private OffsetDateTime lastUpdated;

    private BooleanPropertyState state;

}
