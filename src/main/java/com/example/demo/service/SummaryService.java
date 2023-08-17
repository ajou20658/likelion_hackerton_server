package com.example.demo.service;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SummaryService {
    public static final String url = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";
    @Value("${naver.cloud.id}")
    private String id;

    @Value("${naver.cloud.secret}")
    private String secret;

    public Mono<JsonNode> requestAsync(String doc) {
        WebClient webClient = WebClient.builder().baseUrl(url).build();
        final String finalDoc = doc;
        String request = "{\"document\":{\"content\":\"" + doc + "\"},\"option\":{\"language\":\"ko\",\"model\":\"news\",\"summaryCount\":2}}";
        return webClient.post()
                .uri(url)
                .header("X-NCP-APIGW-API-KEY-ID", id)
                .header("X-NCP-APIGW-API-KEY", secret)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> {
                    System.out.println("400 BAD REQ - doc"+finalDoc);
                    return Mono.empty();
                })
                .bodyToMono(JsonNode.class);
//                .map(jsonNode -> jsonNode.get("summary").asText())

    }
}
