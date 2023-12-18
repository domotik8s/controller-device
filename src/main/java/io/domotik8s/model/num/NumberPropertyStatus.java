package io.domotik8s.model.num;

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
public class NumberPropertyStatus implements PropertyStatus<NumberPropertyState> {

    private OffsetDateTime lastUpdated;

    private NumberPropertyState state;

}
