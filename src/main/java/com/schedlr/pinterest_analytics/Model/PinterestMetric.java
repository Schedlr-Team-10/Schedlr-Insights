package com.schedlr.pinterest_analytics.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinterestMetric {
    private String name;
    private int metric;
}