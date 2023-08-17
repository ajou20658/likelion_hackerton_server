package com.example.demo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class Save {
    private String title;
    private String desc;
    private String imgUrl;
    private String summary;
    private String press;
    private String originUrl;

}
