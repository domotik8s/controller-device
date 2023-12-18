package io.domotik8s.devicecontroller;

import io.domotik8s.model.bool.BooleanProperty;
import io.domotik8s.model.bool.BooleanPropertyList;
import io.domotik8s.model.dev.Device;
import io.domotik8s.model.dev.DeviceList;
import io.domotik8s.model.num.NumberProperty;
import io.domotik8s.model.num.NumberPropertyList;
import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class ClientConfig {

    @Bean
    public ApiClient apiClient() throws IOException {
        ApiClient client = io.kubernetes.client.util.Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        return client;
    }


    @Bean("booleanPropertyClient")
    public GenericKubernetesApi<BooleanProperty, BooleanPropertyList> booleanPropertyClient(ApiClient client) {
        return new GenericKubernetesApi(
                BooleanProperty.class, BooleanPropertyList.class,
                "domotik8s.io", "v1beta1", "booleanproperties",
                client
        );
    }

    @Bean("booleanPropertyInformer")
    public SharedIndexInformer<BooleanProperty> booleanPropertyInformer(
            SharedInformerFactory informerFactory,
            @Qualifier("booleanPropertyClient") GenericKubernetesApi<BooleanProperty, BooleanPropertyList> booleanPropertyClient
    ) {
        return informerFactory.sharedIndexInformerFor(booleanPropertyClient, BooleanProperty.class, 0);
    }


    @Bean("numberPropertyClient")
    public GenericKubernetesApi<NumberProperty, NumberPropertyList> numberPropertyClient(ApiClient client) {
        return new GenericKubernetesApi(
                NumberProperty.class, NumberPropertyList.class,
                "domotik8s.io", "v1beta1", "numberproperties",
                client
        );
    }

    @Bean("numberPropertyInformer")
    public SharedIndexInformer<NumberProperty> numberPropertyInformer(
            SharedInformerFactory informerFactory,
            @Qualifier("numberPropertyClient") GenericKubernetesApi<NumberProperty, NumberPropertyList> numberPropertyClient
    ) {
        return informerFactory.sharedIndexInformerFor(numberPropertyClient, NumberProperty.class, 0);
    }


    @Bean("deviceClient")
    public GenericKubernetesApi<Device, DeviceList> deviceClient(ApiClient client) {
        return new GenericKubernetesApi(
                Device.class, DeviceList.class,
                "domotik8s.io", "v1beta1", "devices",
                client
        );
    }

    @Bean("deviceInformer")
    public SharedIndexInformer<Device> deviceInformer(
            SharedInformerFactory informerFactory,
            @Qualifier("deviceClient") GenericKubernetesApi<Device, DeviceList> deviceClient
    ) {
        return informerFactory.sharedIndexInformerFor(deviceClient, Device.class, 0);
    }

    @Bean("deviceController")
    public Controller deviceController(
            SharedInformerFactory informerFactory,
            DeviceReconciler reconciler,
            @Qualifier("deviceInformer") SharedIndexInformer<Device> deviceInformer
    ) {
        return ControllerBuilder
                .defaultBuilder(informerFactory)
                .watch(workQueue -> ControllerBuilder
                        .controllerWatchBuilder(Device.class, workQueue)
                        .withResyncPeriod(Duration.of(1, ChronoUnit.SECONDS))
                        .build())
                .withWorkerCount(1)
                .withReconciler(reconciler)
                .withReadyFunc(deviceInformer::hasSynced)
                .withName("DeviceController")
                .build();
    }

}