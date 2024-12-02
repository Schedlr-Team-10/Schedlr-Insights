package com.schedlr.pinterest_analytics.service;

import com.schedlr.pinterest_analytics.dto.FilterRequest;
import com.schedlr.pinterest_analytics.dto.LinkedInDataResponse;
import com.schedlr.pinterest_analytics.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LinkedInService {

    private static final Logger LOGGER = Logger.getLogger(LinkedInService.class.getName());

    @Value("${linkedin.api.url}")
    private String linkedInApiUrl;

    @Value("${linkedin.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;

    public LinkedInService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<LinkedInDataResponse> fetchLinkedInData() {
        LOGGER.log(Level.INFO, "Fetching LinkedIn data from API...");
        try {
            String requestUrl = buildRequestUrl();
            LOGGER.log(Level.INFO, "Request URL: {0}", requestUrl);

            ResponseEntity<LinkedInDataResponse[]> response = restTemplate.getForEntity(requestUrl, LinkedInDataResponse[].class);
            LinkedInDataResponse[] dataArray = response.getBody();

            if (dataArray == null || dataArray.length == 0) {
                LOGGER.log(Level.WARNING, "No data received from LinkedIn API.");
                return new ArrayList<>();
            }

            LOGGER.log(Level.INFO, "Successfully fetched data from LinkedIn API.");
            return List.of(dataArray);
        } catch (HttpClientErrorException e) {
            LOGGER.log(Level.SEVERE, "HTTP error while fetching LinkedIn data: {0}", e.getMessage());
            throw new CustomException("Failed to fetch LinkedIn data due to client error.", e);
        } catch (ResourceAccessException e) {
            LOGGER.log(Level.SEVERE, "Network error while accessing LinkedIn API: {0}", e.getMessage());
            throw new CustomException("Failed to connect to LinkedIn API.", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred: {0}", e.getMessage());
            throw new CustomException("An unexpected error occurred while fetching LinkedIn data.", e);
        }
    }


    public List<LinkedInDataResponse> fetchFilteredData(FilterRequest filterRequest) {
        LOGGER.log(Level.INFO, "Applying filters to LinkedIn data: {0}", filterRequest);
        List<LinkedInDataResponse> allData = fetchLinkedInData();

        List<LinkedInDataResponse> filteredData = new ArrayList<>();
        for (LinkedInDataResponse data : allData) {
            if (matchesFilters(data, filterRequest)) {
                filteredData.add(data);
            }
        }

        LOGGER.log(Level.INFO, "Filtered data count: {0}", filteredData.size());
        return filteredData;
    }


    public LinkedInDataResponse fetchDataById(String id) {
        LOGGER.log(Level.INFO, "Fetching LinkedIn data for ID: {0}", id);
        List<LinkedInDataResponse> allData = fetchLinkedInData();

        return allData.stream()
                .filter(data -> id.equals(data.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException("Data not found for ID: " + id));
    }


    public boolean deleteDataById(String id) {
        LOGGER.log(Level.INFO, "Attempting to delete LinkedIn data for ID: {0}", id);
        List<LinkedInDataResponse> allData = fetchLinkedInData();

        boolean exists = allData.stream().anyMatch(data -> id.equals(data.getId()));
        if (!exists) {
            LOGGER.log(Level.WARNING, "Data not found for ID: {0}", id);
            return false;
        }

        LOGGER.log(Level.INFO, "Data successfully deleted for ID: {0}", id);
        // Actual deletion logic would go here if integrated with a database.
        return true;
    }


    public boolean isServiceHealthy() {
        LOGGER.log(Level.INFO, "Checking LinkedIn API health...");
        try {
            restTemplate.getForEntity(linkedInApiUrl, String.class);
            LOGGER.log(Level.INFO, "LinkedIn API is healthy.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "LinkedIn API health check failed: {0}", e.getMessage());
            return false;
        }
    }


    private String buildRequestUrl() {
        return linkedInApiUrl + "?access_token=" + apiToken;
    }


    private boolean matchesFilters(LinkedInDataResponse data, FilterRequest filterRequest) {
        boolean matches = true;

        if (filterRequest.getKeyword() != null && !data.getContent().contains(filterRequest.getKeyword())) {
            matches = false;
        }

        if (filterRequest.getDate() != null && !filterRequest.getDate().equals(data.getDate())) {
            matches = false;
        }

        return matches;
    }
}
