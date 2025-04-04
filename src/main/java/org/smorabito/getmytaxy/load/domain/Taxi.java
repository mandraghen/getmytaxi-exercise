package org.smorabito.getmytaxy.load.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Taxi extends Coordinates {
    private String id;
}
