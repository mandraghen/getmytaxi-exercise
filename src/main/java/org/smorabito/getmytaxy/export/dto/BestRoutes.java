package org.smorabito.getmytaxy.export.dto;

import lombok.Data;

@Data
public class BestRoutes {
    private TaxiRoute quickest;
    private TaxiRoute cheapest;
}
