package io.domotik8s.model.bool;

import io.domotik8s.model.PropertyList;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(description = "List of BooleanProperties")
@NoArgsConstructor
@SuperBuilder
public class BooleanPropertyList extends PropertyList<BooleanProperty> {
}
