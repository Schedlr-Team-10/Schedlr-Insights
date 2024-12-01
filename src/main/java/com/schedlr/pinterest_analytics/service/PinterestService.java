package com.schedlr.pinterest_analytics.service;

import com.schedlr.pinterest_analytics.Model.PinterestMetric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PinterestService {

    @Value("${https://api-sandbox.pinterest.com/v5/}")
    private String baseUrl;

    @Value("${https://api-sandbox.pinterest.com/v5/}")
    private String accessToken;

    private final RestTemplate restTemplate;

    public PinterestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PinterestMetric> getLikes() {
        String url = baseUrl + "/pins/likes?https://api-sandbox.pinterest.com/v5/" + accessToken;
        PinterestMetric[] response = restTemplate.getForObject(url, PinterestMetric[].class);
        return Arrays.stream(response).collect(Collectors.toList());
    }

    public List<PinterestMetric> getShares() {
        String url = baseUrl + "/pins/shares?https://api-sandbox.pinterest.com/v5/" + accessToken;
        PinterestMetric[] response = restTemplate.getForObject(url, PinterestMetric[].class);
        return Arrays.stream(response).collect(Collectors.toList());
    }

    public List<PinterestMetric> getComments() {
        String url = baseUrl + "/pins/comments?https://api-sandbox.pinterest.com/v5/" + accessToken;
        PinterestMetric[] response = restTemplate.getForObject(url, PinterestMetric[].class);
        return Arrays.stream(response).collect(Collectors.toList());
    }
}