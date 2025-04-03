package org.smorabito.getmytaxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        System.out.print("Hello and welcome!");

        TaxiMap taxiMap = null;
        List<Taxi> taxis = null;
        Request request = null;

        //TODO move in a service
        //read input json file
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //TODO read files from the args
            File file = new File("src/main/resources/mocks/taxi_map.json");
            taxiMap = objectMapper.readValue(file, TaxiMap.class);
            LOG.info("File map red successfully: " + taxiMap);

            file = new File("src/main/resources/mocks/taxi_coordinates.json");
            taxis = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Taxi.class));
            LOG.info("File taxis red successfully: " + taxis);

            file = new File("src/main/resources/mocks/request.json");
            request = objectMapper.readValue(file, Request.class);
            LOG.info("File request red successfully: " + request);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error reading the file", e);
        }

        if(taxiMap != null && taxis != null && request != null) {
            new GetMyTaxiExecutor().findTaxis(taxiMap, taxis, request);
        } else {
            LOG.severe("Error reading the files");
        }
    }
}