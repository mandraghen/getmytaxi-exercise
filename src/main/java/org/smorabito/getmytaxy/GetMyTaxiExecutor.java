package org.smorabito.getmytaxy;

import org.smorabito.getmytaxy.export.dto.BestRoutes;
import org.smorabito.getmytaxy.load.dto.InputFiles;
import org.smorabito.getmytaxy.load.service.InputOutputService;
import org.smorabito.getmytaxy.search.service.TaxiMapSearchService;

import java.util.Optional;
import java.util.logging.Logger;

public class GetMyTaxiExecutor {
    private static final Logger LOG = Logger.getLogger(GetMyTaxiExecutor.class.getName());

    private static final String OUTPUT_FILENAME = "./data/output.json";

    private final InputOutputService inputOutputService = new InputOutputService();
    private final TaxiMapSearchService taxiMapSearchService = new TaxiMapSearchService();

    public void executeTaxiSearch(String taxiMapFilename, String taxiCoordinatesFilename, String requestFilename) {
        Optional<InputFiles> inputFiles = inputOutputService.parseInputFiles(taxiMapFilename, taxiCoordinatesFilename, requestFilename);
        if (inputFiles.isEmpty()) {
            LOG.severe("Error reading the input files");
            return;
        }

        InputFiles inputFilesValue = inputFiles.get();

        Optional<BestRoutes> bestTaxis = taxiMapSearchService.findBestTaxis(inputFilesValue.getTaxiMap(),
                inputFilesValue.getTaxis(), inputFilesValue.getRequest());
        if (bestTaxis.isEmpty()) {
            LOG.severe("Error searching for the best taxis");
            return;
        }
        BestRoutes bestRoutes = bestTaxis.get();

        LOG.info("Response " + bestRoutes);

        //serialize response in a json file
        inputOutputService.writeObjectToFile(bestRoutes, OUTPUT_FILENAME);
    }
}
