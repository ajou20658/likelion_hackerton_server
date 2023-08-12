package com.example.demo.service;

import com.example.demo.entity.Keywords;
import com.example.demo.repository.KeywordsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class FlaskService {
    @Autowired
    private KeywordsRepository keywordsRepository;

    public static final String URL = "http://localhost:5000/req?uri=";
    public static final String URI = "C:/Users/kwy/Documents/2023하계/HackerTon/src/main/resources/static/";

    public void iter(String mode) {
        LocalDate today = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);

        Flux.range(100, 6)  // 100부터 6개의 범위를 Flux로 생성
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(sid1 -> sendGetRequest(mode, String.valueOf(sid1), formattedDate))
                .sequential()
                .subscribe();
    }

    public Mono<Void> sendGetRequest(String mode, String sid1, String date) {
        String urll = URL;
        String urii = "";

        if (mode.equals("0")) {
            urii = date + sid1 + ".txt";
        }

        urll += URI;
        urll += urii;
        WebClient webClient = WebClient.create(urll);

        return webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                        Keywords keywords = Keywords.builder()
                                .uri(date + sid1 + mode + ".txt")
                                .response(responseMap)
                                .build();
                        return keywordsRepository.save(keywords)
                                .then().then(); // 실패 시 fail 반환
                    } catch (IOException e) {
                        return Mono.empty();  // 예외 발생 시 fail 반환
                    }
                });
    }
}
