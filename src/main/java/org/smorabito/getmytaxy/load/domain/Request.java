package org.smorabito.getmytaxy.load.domain;

import lombok.Data;

@Data
public class Request {
    private Coordinates source;
    private Coordinates destination;
}
