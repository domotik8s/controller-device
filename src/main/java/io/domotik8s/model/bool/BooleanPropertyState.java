package io.domotik8s.model.bool;

import io.domotik8s.model.PropertyState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BooleanPropertyState implements PropertyState<Boolean> {

    private Boolean value;

}
