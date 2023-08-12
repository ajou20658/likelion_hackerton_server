package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@ToString
@AllArgsConstructor
@Builder
public class Keywords {
    @Id
    private String uri;
    private Map<String,Object> response;

    @JsonProperty("response") // Specify serialization name for the field
    public Map<String, Object> getResponse() {
        return response;
    }
    public Flux<String> map(Object o) {
        return Flux.just("example1","example2");
    }

    public <R> Mono<? extends R> then() {
        return Mono.empty();
    }
}
