package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrawlDto {
    private String img;
    private String content;
    private String title;
    private String press;
}
