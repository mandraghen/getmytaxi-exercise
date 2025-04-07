package org.smorabito.getmytaxy.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smorabito.getmytaxy.GetMyTaxiExecutor;
import org.smorabito.getmytaxy.export.domain.BestRoutes;
import org.smorabito.getmytaxy.export.domain.TaxiRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GetMyTaxiIntegrationTests {
    private static final Logger LOG = LoggerFactory.getLogger(GetMyTaxiIntegrationTests.class);
    public static final String DATA_OUTPUT_PATH = "data/output.json";

    @Autowired
    private GetMyTaxiExecutor getMyTaxiExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        File resultFile = new File(DATA_OUTPUT_PATH);
        if (resultFile.exists() && resultFile.delete()) {
            LOG.info("File {} is deleted.", resultFile.getPath());
        }
    }

    @Test
    public void shouldCalculateBestTaxis() throws IOException {
        testExecution("src/test/resources/mocks/test/taxi_map.json",
                "src/test/resources/mocks/test/taxi_coordinates.json",
                "src/test/resources/mocks/test/request.json",
                "src/test/resources/mocks/test/expected_output.json");
    }

    @Test
    public void shouldCalculateBestTaxis2() throws IOException {
        testExecution("src/test/resources/mocks/test1/taxi_map.json",
                "src/test/resources/mocks/test1/taxi_coordinates.json",
                "src/test/resources/mocks/test1/request.json",
                "src/test/resources/mocks/test1/expected_output.json");
    }

    private void testExecution(String taxiMapFilePath, String taxiCoordinatesFilePath, String requestFilePath,
                               String outputFilePath) throws IOException {
        getMyTaxiExecutor.executeTaxiSearch(taxiMapFilePath, taxiCoordinatesFilePath, requestFilePath);

        File resultFile = new File(DATA_OUTPUT_PATH);
        assertTrue(resultFile.exists());

        BestRoutes resultObject = objectMapper.readValue(resultFile, BestRoutes.class);
        BestRoutes expectedResultObject = objectMapper.readValue(
                new File(outputFilePath), BestRoutes.class);

        checkTaxiRouteEquality(resultObject.getCheapest(), expectedResultObject.getCheapest());
        checkTaxiRouteEquality(resultObject.getQuickest(), expectedResultObject.getQuickest());
    }

    void checkTaxiRouteEquality(TaxiRoute result, TaxiRoute expected) {
        assertEquals(expected.getTaxi(), result.getTaxi());
        assertEquals(expected.getDistance(), result.getDistance());
        assertEquals(expected.getCost(), result.getCost());
        assertEquals(expected.getWaitTime(), result.getWaitTime());
        assertEquals(expected.getTravelTime(), result.getTravelTime());
        assertNotNull(result.getRoute());
        assertEquals(expected.getRoute().size(), result.getRoute().size());
    }
}
