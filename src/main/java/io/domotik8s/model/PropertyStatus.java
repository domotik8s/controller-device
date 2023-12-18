package io.domotik8s.model;

import java.time.OffsetDateTime;

public interface PropertyStatus<ST extends PropertyState> {

    OffsetDateTime getLastUpdated();

    ST getState();

}
