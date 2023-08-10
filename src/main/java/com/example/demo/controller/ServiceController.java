package com.example.demo.controller;

import com.example.demo.service.CrawlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api") //
public class ServiceController {
    @Autowired
    private CrawlService crawlService;
    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }
    @GetMapping("/crawl")
    public Long crawl(){
        try{
            Long res = crawlService.collectingNews();
            return res;
        }catch(Exception ex){
            log.error("Crawl err");
            return -1l;
        }

    }
}
