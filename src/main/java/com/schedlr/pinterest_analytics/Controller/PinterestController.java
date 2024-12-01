package com.schedlr.pinterest_analytics.Controller;

import com.schedlr.pinterest_analytics.Model.PinterestMetric;
import com.schedlr.pinterest_analytics.service.PinterestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pinterest")
public class PinterestController {

    private final PinterestService pinterestService;

    public PinterestController(PinterestService pinterestService) {
        this.pinterestService = pinterestService;
    }

    @GetMapping("/likes")
    public List<PinterestMetric> getLikes() {
        return pinterestService.getLikes();
    }

    @GetMapping("/shares")
    public List<PinterestMetric> getShares() {
        return pinterestService.getShares();
    }

    @GetMapping("/comments")
    public List<PinterestMetric> getComments() {
        return pinterestService.getComments();
    }
}