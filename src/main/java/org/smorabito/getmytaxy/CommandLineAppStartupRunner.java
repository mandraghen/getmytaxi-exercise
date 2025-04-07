package org.smorabito.getmytaxy;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);

    private final GetMyTaxiExecutor getMyTaxiExecutor;

    @Override
    public void run(String... args) {
        LOG.info("args: {}", Arrays.toString(args));

        if (args.length < 3) {
            LOG.error("Please provide the input files paths: <taxi_map.json> " +
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
            LOG.error("Parameters are not valid");
            LOG.error("Please provide the input files paths: <taxi_map.json> " +
                    "<taxi_coordinates.json> <request.json>");
        }
    }
}
