package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleDto {
    private String origin;
    private String img;
    private String summary;
    private String title;
    private String press;
}
