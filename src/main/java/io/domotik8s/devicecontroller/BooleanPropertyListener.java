package io.domotik8s.devicecontroller;

import io.domotik8s.model.bool.BooleanProperty;
import io.domotik8s.model.bool.BooleanPropertyList;
import io.domotik8s.model.dev.Device;
import io.domotik8s.model.dev.DeviceList;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1OwnerReference;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BooleanPropertyListener implements ResourceEventHandler<BooleanProperty> {


    private Logger logger = LoggerFactory.getLogger(BooleanPropertyListener.class);


    @Qualifier("deviceClient")
    private final GenericKubernetesApi<Device, DeviceList> deviceClient;

    @Qualifier("booleanPropertyClient")
    private final GenericKubernetesApi<BooleanProperty, BooleanPropertyList> booleanPropertyclient;

    @Qualifier("booleanPropertyInformer")
    private final SharedIndexInformer<BooleanProperty> informer;

    private final DeviceReconciler reconciler;


    @PostConstruct
    private void register() {
        informer.addEventHandler(this);
    }


    @Override
    public void onAdd(BooleanProperty booleanProperty) {

    }

    @Override
    public void onUpdate(BooleanProperty booleanProperty, BooleanProperty apiType1) {

    }

    @Override
    public void onDelete(BooleanProperty booleanProperty, boolean b) {
        System.out.println("DELETED");

        V1ObjectMeta metadata = booleanProperty.getMetadata();
        List<V1OwnerReference> ownerRefs = metadata.getOwnerReferences();

        if (ownerRefs == null || ownerRefs.size() == 0) return;

        List<V1OwnerReference> deviceOwnerRefs = ownerRefs.stream().filter(ref -> {
            boolean apiVersion = "domotik8s.io/v1beta1".equals(ref.getApiVersion());
            boolean kind = "Device".equals(ref.getKind());
            return apiVersion == true && kind == true;
        }).collect(Collectors.toList());

        deviceOwnerRefs.forEach(deviceRef -> {
            KubernetesApiResponse<Device> resp = deviceClient.get(deviceRef.getName());
            Device dev = resp.getObject();

            Request req = new Request(dev.getMetadata().getName());
            reconciler.reconcile(req);
        });
    }


}
