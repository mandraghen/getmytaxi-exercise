package org.smorabito.getmytaxy;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smorabito.getmytaxy.export.domain.BestRoutes;
import org.smorabito.getmytaxy.load.dto.InputFiles;
import org.smorabito.getmytaxy.load.service.InputOutputService;
import org.smorabito.getmytaxy.search.service.TaxiMapSearchService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GetMyTaxiExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(GetMyTaxiExecutor.class);

    private static final String OUTPUT_FILENAME = "./data/output.json";

    private final InputOutputService inputOutputService;
    private final TaxiMapSearchService taxiMapSearchService;

    public void executeTaxiSearch(String taxiMapFilename, String taxiCoordinatesFilename, String requestFilename) {
        Optional<InputFiles> inputFiles = inputOutputService.parseInputFiles(taxiMapFilename, taxiCoordinatesFilename, requestFilename);
        if (inputFiles.isEmpty()) {
            LOG.error("Error reading the input files");
            return;
        }

        InputFiles inputFilesValue = inputFiles.get();

        Optional<BestRoutes> bestTaxis = taxiMapSearchService.findBestTaxis(inputFilesValue.getTaxiMap(),
                inputFilesValue.getTaxis(), inputFilesValue.getRequest());
        if (bestTaxis.isEmpty()) {
            LOG.error("Error searching for the best taxis");
            return;
        }
        BestRoutes bestRoutes = bestTaxis.get();

        LOG.info("Response " + bestRoutes);

        //serialize response in a json file
        inputOutputService.writeObjectToFile(bestRoutes, OUTPUT_FILENAME);
    }
}
