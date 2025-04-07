package org.smorabito.getmytaxy.load.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smorabito.getmytaxy.export.domain.BestRoutes;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.load.dto.InputFiles;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputOutputServiceTest {
    public static final String VALID_TAXI_MAP_FILENAME = "./src/test/resources/mocks/taxi_map.json";
    public static final String VALID_TAXI_COORDINATES_FILENAME = "./src/test/resources/mocks/taxi_coordinates.json";
    public static final String VALID_REQUEST_FILENAME = "./src/test/resources/mocks/request.json";

    public static final String NOT_VALID_TAXI_MAP_FILENAME = "not_valid_taxi_map.json";

    @Mock
    private ObjectMapper objectMapper;

    private InputOutputService inputOutputService;

    @BeforeEach
    void setUp() {
        inputOutputService = new InputOutputService(objectMapper);
    }

    @Test
    public void shouldParseValidFiles() throws IOException {
        // Mocking the file reading and object mapping
        TaxiMap mockTaxiMap = new TaxiMap();
        List<Taxi> mockTaxis = List.of(new Taxi());
        Request mockRequest = new Request();

        when(objectMapper.readValue(any(File.class), eq(TaxiMap.class)))
                .thenReturn(mockTaxiMap);
        when(objectMapper.readValue(any(File.class), eq(Request.class)))
                .thenReturn(mockRequest);
        when(objectMapper.readValue(any(File.class), any(CollectionType.class)))
                .thenReturn(mockTaxis);
        TypeFactory typeFactoryMock = mock(TypeFactory.class);
        when(objectMapper.getTypeFactory())
                .thenReturn(typeFactoryMock);
        when(typeFactoryMock.constructCollectionType(List.class, Taxi.class))
                .thenReturn(mock(CollectionType.class));

        Optional<InputFiles> result = inputOutputService.parseInputFiles(
                VALID_TAXI_MAP_FILENAME,
                VALID_TAXI_COORDINATES_FILENAME,
                VALID_REQUEST_FILENAME);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getTaxiMap());
        assertNotNull(result.get().getTaxis());
        assertNotNull(result.get().getRequest());
    }

    @Test
    public void shouldThrowExceptionWhenFileNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                inputOutputService.parseInputFiles(
                NOT_VALID_TAXI_MAP_FILENAME,
                VALID_TAXI_COORDINATES_FILENAME,
                VALID_REQUEST_FILENAME));
    }

    @Test
    void shouldWriteObjectToFileSuccessfully() throws IOException {
        String filename = "./data/test.json";
        BestRoutes testObject = new BestRoutes();

        inputOutputService.writeObjectToFile(testObject, filename);

        File resultFile = new File(filename);
        verify(objectMapper).writeValue(resultFile, testObject);
        assertTrue(resultFile.getParentFile().exists());
    }

}
