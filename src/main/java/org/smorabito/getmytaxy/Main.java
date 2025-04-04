package org.smorabito.getmytaxy;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private static final GetMyTaxiExecutor getMyTaxiExecutor = new GetMyTaxiExecutor();

    public static void main(String[] args) {
        if (args.length < 3) {
            LOG.severe("Please provide the input files paths: <taxi_map.json> " +
                    "<taxi_coordinates.json> <request.json>");
            return;
        }

        String taxiMapFilename = args[0];
        String taxiCoordinatesFilename = args[1];
        String requestFilename = args[2];

        if (!taxiMapFilename.isBlank() &&
                !taxiCoordinatesFilename.isBlank() &&
                !requestFilename.isBlank()) {
            getMyTaxiExecutor.executeTaxiSearch(taxiMapFilename, taxiCoordinatesFilename, requestFilename);
        } else {
            LOG.severe("Parameters are not valid");
            LOG.severe("Please provide the input files paths: <taxi_map.json> " +
                    "<taxi_coordinates.json> <request.json>");
        }
    }
}