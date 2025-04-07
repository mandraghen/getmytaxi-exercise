package org.smorabito.getmytaxy.export.domain;

import lombok.Data;

@Data
public class BestRoutes {
    private TaxiRoute quickest;
    private TaxiRoute cheapest;
}
