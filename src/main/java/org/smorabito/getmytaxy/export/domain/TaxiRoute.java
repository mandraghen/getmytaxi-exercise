package org.smorabito.getmytaxy.export.domain;

import lombok.Data;
import org.smorabito.getmytaxy.load.domain.Coordinates;

import java.util.List;

@Data
public class TaxiRoute {
    private String taxi;
    private int cost;
    private int distance;
    private float travelTime;
    private float waitTime;
    private List<Coordinates> route;
}
