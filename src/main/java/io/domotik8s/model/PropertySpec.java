package io.domotik8s.model;

import java.util.Map;

public interface PropertySpec<ST extends PropertyState> {

    Map<String, Object> getAddress();

    ST getState();

    Boolean getLocked();

}
