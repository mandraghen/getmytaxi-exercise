package org.smorabito.getmytaxy.load.domain;

import lombok.Data;

import java.util.List;

@Data
public class TaxiMap {
    private String city;
    private int width;
    private int height;
    private List<Wall> walls;
    private List<Checkpoint> checkpoints;
}
