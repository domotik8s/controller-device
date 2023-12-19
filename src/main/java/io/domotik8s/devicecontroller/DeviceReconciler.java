package io.domotik8s.devicecontroller;

import io.domotik8s.model.Property;
import io.domotik8s.model.bool.BooleanProperty;
import io.domotik8s.model.bool.BooleanPropertyList;
import io.domotik8s.model.bool.BooleanPropertySpec;
import io.domotik8s.model.bool.BooleanSemantic;
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

        return new Result(false);
    }

    private void createProperties(Device resource) {
        Optional<String> deviceNameOpt = Optional.ofNullable(resource)
                .map(Device::getMetadata)
                .map(V1ObjectMeta::getName);
        if (deviceNameOpt.isEmpty()) return;
        String deviceName = deviceNameOpt.get();

        Optional<DeviceProperties> propertiesOpt = Optional.ofNullable(resource)
                .map(Device::getSpec)
                .map(DeviceSpec::getProperties);

        if (propertiesOpt.isEmpty()) return;

        Map<String, BooleanPropertySpec> booleanProperties = propertiesOpt.map(DeviceProperties::getBooleanProperties).orElse(Map.of());
        for (String propertyName: booleanProperties.keySet()) {
            BooleanPropertySpec spec = booleanProperties.get(propertyName);
            if (spec == null) continue;

            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(deviceName + "-" + propertyName);
            metadata.setNamespace(resource.getMetadata().getNamespace());

            BooleanProperty property = new BooleanProperty();
            property.setSpec(spec);
            property.setMetadata(metadata);

            // Labels
            for (Map.Entry<String, String> labelEntry: resource.getMetadata().getLabels().entrySet())
                metadata.putLabelsItem(labelEntry.getKey(), labelEntry.getValue());
            metadata.putLabelsItem("property", propertyName);

            // Owner Reference
            List<V1OwnerReference> refs = Optional.ofNullable(metadata.getOwnerReferences()).orElse(new ArrayList<>());
            metadata.setOwnerReferences(refs);

            V1OwnerReference ref = new V1OwnerReference();
            ref.apiVersion("domotik8s.io/v1beta1");
            ref.kind("Device");
            ref.name(deviceName);
            ref.uid(resource.getMetadata().getUid());
            refs.add(ref);

            booleanPropertyClient.create(property);
        }

        Map<String, NumberPropertySpec> numberProperties = propertiesOpt.map(DeviceProperties::getNumberProperties).orElse(Map.of());
        for (String propertyName: numberProperties.keySet()) {
            NumberPropertySpec spec = numberProperties.get(propertyName);
            if (spec == null) continue;

            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(deviceName + "-" + propertyName);
            metadata.setNamespace(resource.getMetadata().getNamespace());

            NumberProperty property = new NumberProperty();
            property.setSpec(spec);
            property.setMetadata(metadata);

            // Labels
            for (Map.Entry<String, String> labelEntry: resource.getMetadata().getLabels().entrySet())
                metadata.putLabelsItem(labelEntry.getKey(), labelEntry.getValue());
            metadata.putLabelsItem("property", propertyName);

            // Owner Reference
            List<V1OwnerReference> refs = Optional.ofNullable(metadata.getOwnerReferences()).orElse(new ArrayList<>());
            metadata.setOwnerReferences(refs);

            V1OwnerReference ref = new V1OwnerReference();
            ref.apiVersion("domotik8s.io/v1beta1");
            ref.kind("Device");
            ref.name(deviceName);
            ref.uid(resource.getMetadata().getUid());
            refs.add(ref);

            numberPropertyClient.create(property);
        }
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
