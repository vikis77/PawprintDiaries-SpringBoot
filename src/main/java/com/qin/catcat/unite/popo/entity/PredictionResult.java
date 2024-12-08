package com.qin.catcat.unite.popo.entity;

import lombok.Data;
import java.util.List;

@Data
public class PredictionResult {
    private Boolean success;
    private String message;
    private PredictionData data;

    @Data
    public static class PredictionData {
        private String filename;
        private List<Prediction> predictions;
        private String warning;
    }

    @Data
    public static class Prediction {
        private Integer rank;
        private String breed_en;
        private String breed_cn;
        private Double confidence;
    }
} 