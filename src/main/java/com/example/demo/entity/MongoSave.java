package com.example.demo.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;
@Builder
@Getter
@Setter
public class MongoSave {
    //id는 Keyword자료형의 value값(키워드)으로 설정
    @Id
    private String id;
    private List<Save> response;
}
