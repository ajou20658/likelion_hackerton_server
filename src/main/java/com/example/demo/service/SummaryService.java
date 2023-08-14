package com.example.demo.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SummaryService {
    public static final String url = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";
    @Value("${naver.cloud.id}")
    private String id;

    @Value("${naver.cloud.secret}")
    private String secret;
    public String request(String doc){
        String request = "{\"document\":[\"content\":\""+doc+"\"],\"option\":[\"language\":\"ko\",\"model\":\"news\",\"summaryCount\":3]}";
        WebClient webClient=
                WebClient.builder()
                        .baseUrl(url)
                        .build();
        Mono<String> Response = webClient
                .post()
                .headers(
                        httpHeaders -> {
                            httpHeaders.add("X-NCP-APIGW-API-KEY-ID",id);
                            httpHeaders.add("X-NCP-APIGW-API-KEY",secret);
                            httpHeaders.add("Content-Type","application/json");
                        }
                )
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        String responseString = Response.block();

        return responseString;
    }
}
