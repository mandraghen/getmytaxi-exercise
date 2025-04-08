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
        InputFiles inputFiles;

        //read input json file
        try {
            inputFiles = new InputFiles();

            inputFiles.setTaxiMap(loadInputFile(taxiMapFilename, TaxiMap.class)
                    .orElseThrow(IllegalArgumentException::new));
            inputFiles.setTaxis(loadInputList(taxiCoordinatesFilename, Taxi.class)
                    .orElseThrow(IllegalArgumentException::new));
            inputFiles.setRequest(loadInputFile(requestFilename, Request.class)
                    .orElseThrow(IllegalArgumentException::new));
        } catch (IOException e) {
            LOG.error("Error reading the input files", e);
            return Optional.empty();
        }

        return Optional.of(inputFiles);
    }

    private <T> Optional<T> loadInputFile(String inputFilepath, Class<T> resultClass) throws IOException {
        Optional<File> file = fetchFile(inputFilepath);
        if (file.isPresent()) {
            T parsedObject = objectMapper.readValue(file.get(), resultClass);
            LOG.info("File " + inputFilepath + " red successfully: " + parsedObject);
            return Optional.ofNullable(parsedObject);
        }
        return Optional.empty();
    }

    private <T> Optional<List<T>> loadInputList(String inputFilepath, Class<T> resultClass) throws IOException {
        Optional<File> file = fetchFile(inputFilepath);
        if (file.isPresent()) {
            List<T> parsedObject = objectMapper.readValue(file.get(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, resultClass));
            LOG.info("File " + inputFilepath + " red successfully: " + parsedObject);
            return Optional.ofNullable(parsedObject);
        }
        return Optional.empty();
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
        if (directory != null && !directory.exists()) {
            if (directory.mkdirs()) {
                LOG.info("Directory created successfully: {}", directory.getAbsolutePath());
            } else {
                LOG.error("Failed to create directory: {}", directory.getAbsolutePath());
            }
        }

        try {
            objectMapper.writeValue(new File(filename), object);
        } catch (IOException e) {
            LOG.error("Error writing to file: " + filename, e);
        }
    }
}
