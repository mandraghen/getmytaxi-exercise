package org.smorabito.getmytaxy.load.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class Taxi extends Coordinates {
    private String id;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Taxi taxi)) return false;
        return Objects.equals(getId(), taxi.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
