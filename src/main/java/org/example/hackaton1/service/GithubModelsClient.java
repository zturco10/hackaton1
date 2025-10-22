package org.example.hackaton1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class GithubModelsClient {

    private final RestClient rest;

    @Value("${MODEL_ID}")
    private String modelId;

    public GithubModelsClient(@Value("${GITHUB_MODELS_URL}") String baseUrl,
                              @Value("${GITHUB_TOKEN}") String token) {
        this.rest = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    public String generateSummary(int totalUnits, BigDecimal totalRevenue, String topSku, String topBranch) {
        String userPrompt = "Con estos datos: totalUnits=" + totalUnits +
                ", totalRevenue=" + totalRevenue + ", topSku=" + topSku +
                ", topBranch=" + topBranch + ". Devuelve un resumen ≤120 palabras en español para email.";

        Map<String, Object> body = Map.of(
                "model", modelId,
                "messages", new Object[]{
                        Map.of("role", "system", "content",
                                "Eres un analista que escribe resúmenes breves y claros para emails corporativos."),
                        Map.of("role", "user", "content", userPrompt)
                },
                "max_tokens", 200
        );

        // Ajusta la ruta concreta del endpoint de GitHub Models si es distinta:
        var response = rest.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        // Asumiendo estructura similar a OpenAI:
        try {
            var choices = (java.util.List<Map<String, Object>>) response.get("choices");
            var msg = (Map<String, Object>) choices.get(0).get("message");
            return String.valueOf(msg.get("content"));
        } catch (Exception e) {
            throw new IllegalStateException("Respuesta inesperada del LLM");
        }
    }
}