package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@ToString
@AllArgsConstructor
public class News {
    @Id
    public String title;
    public String content;
    public String[] tags;
}
