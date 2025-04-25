package org.smorabito.getmytaxy.load.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smorabito.getmytaxy.load.domain.Request;
import org.smorabito.getmytaxy.load.domain.Taxi;
import org.smorabito.getmytaxy.load.domain.TaxiMap;
import org.smorabito.getmytaxy.load.dto.InputFiles;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InputOutputService {
    private static final Logger LOG = LoggerFactory.getLogger(InputOutputService.class);

    private final ObjectMapper objectMapper;

    public Optional<InputFiles> parseInputFiles(String taxiMapFilename, String taxiCoordinatesFilename,
                                                String requestFilename) {
        //read input json file
        try {
            InputFiles inputFiles = new InputFiles();

            inputFiles.setTaxiMap(loadInputFile(taxiMapFilename, TaxiMap.class)
                    .orElseThrow(IllegalArgumentException::new));
            inputFiles.setTaxis(loadInputList(taxiCoordinatesFilename, Taxi.class)
                    .orElseThrow(IllegalArgumentException::new));
            inputFiles.setRequest(loadInputFile(requestFilename, Request.class)
                    .orElseThrow(IllegalArgumentException::new));

            return Optional.of(inputFiles);
        } catch (IOException e) {
            LOG.error("Error reading the input files", e);
            return Optional.empty();
        }
    }

    private <T> Optional<T> loadInputFile(String inputFilepath, Class<T> resultClass) throws IOException {
        return fetchFile(inputFilepath)
                .map(file -> {
                    try {
                        T parsedObject = objectMapper.readValue(file, resultClass);
                        LOG.info("File {} read successfully: {}", inputFilepath, parsedObject);
                        return parsedObject;
                    } catch (IOException e) {
                        LOG.error("Error reading file {}", inputFilepath, e);
                        return null;
                    }
                });
    }

    private <T> Optional<List<T>> loadInputList(String inputFilepath, Class<T> resultClass) throws IOException {
        return fetchFile(inputFilepath)
                .map(file -> {
                    try {
                        List<T> parsedObject = objectMapper.readValue(file,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, resultClass));
                        LOG.info("File {} read successfully: {}", inputFilepath, parsedObject);
                        return parsedObject;
                    } catch (IOException e) {
                        LOG.error("Error reading file {}", inputFilepath, e);
                        return null;
                    }
                });
    }

    private Optional<File> fetchFile(String inputFilepath) {
        File file = new File(inputFilepath);
        //validate the file
        if (!file.exists() && !file.isFile()) {
            LOG.error("File {} not found or not a file", inputFilepath);
            return Optional.empty();
        }
        return Optional.of(file);
    }

    public <T> void writeObjectToFile(T object, String filename) {
        File file = new File(filename);
        File directory = file.getParentFile();
        if (file.getParentFile() != null && !directory.exists() && !directory.mkdirs()) {
            LOG.error("Failed to create directory: {}", directory.getAbsolutePath());
            return;
        }

        try {
            objectMapper.writeValue(file, object);
        } catch (IOException e) {
            LOG.error("Error writing to file: " + filename, e);
        }
    }
}
