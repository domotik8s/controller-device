package io.domotik8s.model;

import io.kubernetes.client.common.KubernetesObject;

public interface Property<SP extends PropertySpec, ST extends PropertyStatus> extends KubernetesObject {

    SP getSpec();

    ST getStatus();

}
