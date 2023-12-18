package io.domotik8s.devicecontroller;

import io.domotik8s.model.Property;
import io.domotik8s.model.bool.BooleanProperty;
import io.domotik8s.model.bool.BooleanPropertyList;
import io.domotik8s.model.bool.BooleanPropertySpec;
import io.domotik8s.model.dev.*;
import io.domotik8s.model.num.NumberProperty;
import io.domotik8s.model.num.NumberPropertyList;
import io.domotik8s.model.num.NumberPropertySpec;
import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1OwnerReference;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceReconciler implements Reconciler {

    private Logger logger = LoggerFactory.getLogger(DeviceReconciler.class);


    @Qualifier("booleanPropertyClient")
    private final GenericKubernetesApi<BooleanProperty, BooleanPropertyList> booleanPropertyClient;

    @Qualifier("numberPropertyClient")
    private final GenericKubernetesApi<NumberProperty, NumberPropertyList> numberPropertyClient;

    @Qualifier("deviceClient")
    private final GenericKubernetesApi<Device, DeviceList> client;

    @Qualifier("deviceInformer")
    private final SharedIndexInformer<Device> informer;


    @Override
    public Result reconcile(Request request) {
        String key = createKey(request);
        logger.info("Handling resource {}", key);

        Device resource = informer.getIndexer().getByKey(key);

        if (resource == null) {
            logger.warn("Received reconciliation request for device {} but could not find it in indexer.", key);
            return new Result(false);
        }

        // Create all properties of the device
        createProperties(resource);

        // TODO: patch desired state into properties

        return new Result(false);
    }

    private void createProperties(Device resource) {
        Set<Map.Entry<String, PropertyTemplate>> entries = Optional.ofNullable(resource)
                .map(Device::getSpec)
                .map(DeviceSpec::getProperties)
                .map((map) -> map.entrySet())
                .orElse(Set.of());

        String deviceName = resource.getMetadata().getName();

        for (Map.Entry<String, PropertyTemplate> entry: entries) {
            String name = entry.getKey();
            PropertyTemplate template = entry.getValue();

            Property<?, ?> property = null;

            PropertyType type = template.getType();
            if (type == null) continue;

            if (type == PropertyType.BOOLEAN) {
                property = createBooleanProperty(deviceName, name, template.getAddressSpec());
            } else if (type == PropertyType.NUMBER) {
                property = createNumberProperty(deviceName, name, template.getAddressSpec());
            }

            // Labels
            V1ObjectMeta metadata = property.getMetadata();
            for (Map.Entry<String, String> labelEntry: resource.getMetadata().getLabels().entrySet()) {
                metadata.putLabelsItem(labelEntry.getKey(), labelEntry.getValue());
            }
            metadata.putLabelsItem("property", name);

            // Owner Reference
            List<V1OwnerReference> refs = Optional.ofNullable(metadata.getOwnerReferences()).orElse(new ArrayList<>());
            metadata.setOwnerReferences(refs);

            V1OwnerReference ref = new V1OwnerReference();
            ref.apiVersion("domotik8s.io/v1beta1");
            ref.kind("Device");
            ref.name(deviceName);
            ref.uid(resource.getMetadata().getUid());
            refs.add(ref);

            if (property instanceof BooleanProperty) {
                booleanPropertyClient.create((BooleanProperty) property);
            } else if (property instanceof NumberProperty) {
                numberPropertyClient.create((NumberProperty) property);
            }
        }
    }

    private Property<?,?> createNumberProperty(String deviceName, String propertyName, Map<String, Object> addressSpec) {
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(deviceName + "-" + propertyName);

        NumberPropertySpec spec = new NumberPropertySpec();
        spec.setAddress(addressSpec);

        NumberProperty property = new NumberProperty();
        property.setSpec(spec);
        property.setMetadata(metadata);
        return property;
    }

    private Property<?,?> createBooleanProperty(String deviceName, String propertyName, Map<String, Object> addressSpec) {
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(deviceName + "-" + propertyName);

        BooleanPropertySpec spec = new BooleanPropertySpec();
        spec.setAddress(addressSpec);

        BooleanProperty property = new BooleanProperty();
        property.setSpec(spec);
        property.setMetadata(metadata);
        return property;
    }


    protected String createKey(Request request) {
        StringBuilder key = new StringBuilder();
        if (request.getNamespace() != null) {
            key.append(request.getNamespace());
            key.append("/");
        }
        key.append(request.getName());
        return key.toString();
    }

}
