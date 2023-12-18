package io.domotik8s.model.num;

import io.domotik8s.model.PropertyList;
import io.domotik8s.model.bool.BooleanProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(description = "List of NumberProperties")
@NoArgsConstructor
@SuperBuilder
public class NumberPropertyList extends PropertyList<NumberProperty> {
}
