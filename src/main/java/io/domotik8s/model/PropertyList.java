package io.domotik8s.model;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ListMeta;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@ApiModel(description = "List of Objects")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PropertyList<T extends KubernetesObject> implements KubernetesListObject {

    @SerializedName("apiVersion")
    private final String apiVersion = "domotik8s.io/v1beta1";

    @SerializedName("items")
    private List<T> items;

    @SerializedName("kind")
    private String kind;

    @SerializedName("metadata")
    private V1ListMeta metadata;

}
