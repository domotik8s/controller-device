package io.domotik8s.model.dev;

import com.google.gson.annotations.SerializedName;
import io.domotik8s.model.bool.BooleanPropertySpec;
import io.domotik8s.model.num.NumberPropertySpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceProperties {

    @SerializedName("boolean")
    private Map<String, BooleanPropertySpec> booleanProperties;


    @SerializedName("number")
    private Map<String, NumberPropertySpec> numberProperties;

}
