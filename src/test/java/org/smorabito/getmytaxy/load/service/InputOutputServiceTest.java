package org.smorabito.getmytaxy.load.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.load.dto.InputFiles;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class InputOutputServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private InputOutputService inputOutputService;

    @BeforeEach
    void setUp() {
//        inputParserService = new InputParserService(objectMapper);
    }

    @Test
    void testParseInputFiles() throws IOException {
        // Mocking the file reading and object mapping
        TaxiMap mockTaxiMap = new TaxiMap();
        List<Taxi> mockTaxis = List.of(new Taxi());
        Request mockRequest = new Request();

//        when(objectMapper.readValue(any(File.class), any(Class.class)))
//                .thenReturn(mockTaxiMap)
//                .thenReturn(mockRequest);
//        when(objectMapper.readValue(any(File.class), any()))
//                .thenReturn(mockTaxis);

        Optional<InputFiles> result = inputOutputService.parseInputFiles("taxiMap.json", "taxiCoordinates.json", "request.json");

//        assertTrue(result.isPresent());
//        assertNotNull(result.get().getTaxiMap());
//        assertNotNull(result.get().getTaxis());
//        assertNotNull(result.get().getRequest());
    }
}
