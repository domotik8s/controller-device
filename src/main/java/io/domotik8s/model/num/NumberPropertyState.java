package io.domotik8s.model.num;

import io.domotik8s.model.PropertyState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.measure.Unit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumberPropertyState implements PropertyState<Number> {

    private Number value;

    private String unit;

}
