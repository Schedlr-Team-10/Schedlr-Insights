package com.schedlr.pinterest_analytics.Controller;

import com.schedlr.pinterest_analytics.service.LinkedInService;
import com.schedlr.pinterest_analytics.dto.LinkedInDataResponse;
import com.schedlr.pinterest_analytics.dto.FilterRequest;
import com.schedlr.pinterest_analytics.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/linkedin")
public class LinkedInController {

    private static final Logger LOGGER = Logger.getLogger(LinkedInController.class.getName());

    @Autowired
    private LinkedInService linkedInService;


    @GetMapping("/data")
    public ResponseEntity<Object> getLinkedInData() {
        LOGGER.log(Level.INFO, "Fetching LinkedIn data...");
        try {
            List<LinkedInDataResponse> data = linkedInService.fetchLinkedInData();
            if (data.isEmpty()) {
                LOGGER.log(Level.WARNING, "No LinkedIn data found.");
                return ResponseEntity.noContent().build();
            }
            LOGGER.log(Level.INFO, "Successfully fetched LinkedIn data.");
            return ResponseEntity.ok(data);
        } catch (CustomException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching LinkedIn data: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }


    @PostMapping("/data/filter")
    public ResponseEntity<Object> getFilteredLinkedInData(@RequestBody @Valid FilterRequest filterRequest) {
        LOGGER.log(Level.INFO, "Fetching LinkedIn data with filters: {0}", filterRequest);
        try {
            List<LinkedInDataResponse> filteredData = linkedInService.fetchFilteredData(filterRequest);
            if (filteredData.isEmpty()) {
                LOGGER.log(Level.WARNING, "No data found for the provided filters.");
                return ResponseEntity.noContent().build();
            }
            LOGGER.log(Level.INFO, "Filtered data successfully fetched.");
            return ResponseEntity.ok(filteredData);
        } catch (CustomException ex) {
            LOGGER.log(Level.SEVERE, "Error applying filters: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }


    @GetMapping("/data/{id}")
    public ResponseEntity<Object> getLinkedInDataById(@PathVariable("id") String id) {
        LOGGER.log(Level.INFO, "Fetching LinkedIn data for ID: {0}", id);
        try {
            Optional<LinkedInDataResponse> data = linkedInService.fetchDataById(id);
            if (data.isEmpty()) {
                LOGGER.log(Level.WARNING, "LinkedIn data not found for ID: {0}", id);
                return ResponseEntity.status(404).body(Map.of("error", "Data not found for ID: " + id));
            }
            LOGGER.log(Level.INFO, "Successfully fetched LinkedIn data for ID: {0}", id);
            return ResponseEntity.ok(data);
        } catch (CustomException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching data by ID: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }


    @DeleteMapping("/data/{id}")
    public ResponseEntity<Object> deleteLinkedInDataById(@PathVariable("id") String id) {
        LOGGER.log(Level.INFO, "Deleting LinkedIn data for ID: {0}", id);
        try {
            boolean isDeleted = linkedInService.deleteDataById(id);
            if (isDeleted) {
                LOGGER.log(Level.INFO, "Successfully deleted LinkedIn data for ID: {0}", id);
                return ResponseEntity.ok(Map.of("message", "Data successfully deleted for ID: " + id));
            } else {
                LOGGER.log(Level.WARNING, "Data not found for ID: {0}", id);
                return ResponseEntity.status(404).body(Map.of("error", "Data not found for ID: " + id));
            }
        } catch (CustomException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting data by ID: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }


    @GetMapping("/status")
    public ResponseEntity<Object> checkServiceStatus() {
        LOGGER.log(Level.INFO, "Checking LinkedIn service status...");
        try {
            boolean isServiceUp = linkedInService.isServiceHealthy();
            if (isServiceUp) {
                LOGGER.log(Level.INFO, "LinkedIn service is healthy.");
                return ResponseEntity.ok(Map.of("status", "Service is healthy"));
            } else {
                LOGGER.log(Level.WARNING, "LinkedIn service is experiencing issues.");
                return ResponseEntity.status(503).body(Map.of("status", "Service is down"));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred while checking service status: {0}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }
}
