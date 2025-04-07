package org.smorabito.getmytaxy.load.dto;

import lombok.Data;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;

import java.util.List;

@Data
public class InputFiles {
    private TaxiMap taxiMap;
    private List<Taxi> taxis;
    private Request request;
}
