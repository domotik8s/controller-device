package io.domotik8s.model.dev;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Device implements KubernetesObject {

    private final String apiVersion = "domotik8s.io/v1beta1";

    private final String kind = "Device";

    private V1ObjectMeta metadata;

    private DeviceSpec spec;

    private DeviceStatus status;

}
