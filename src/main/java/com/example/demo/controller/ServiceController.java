package com.example.demo.controller;

import com.example.demo.dto.ArticleDto;
import com.example.demo.entity.Keywords;
import com.example.demo.repository.KeywordsRepository;
import com.example.demo.service.CrawlService;
import com.example.demo.service.FlaskService;
import com.example.demo.service.SummaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api") //
public class ServiceController {
    @Autowired
    private CrawlService crawlService;
    @Autowired
    private FlaskService flaskService;
    @Autowired
    private SummaryService summaryService;
    @Autowired
    private KeywordsRepository keywordsRepository;

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
    @GetMapping("/ml")
    public void flaskreq(){
        flaskService.iter("0");
    }

    @GetMapping("/keyword")
    @ResponseBody
    public ResponseEntity<String> flask(@RequestParam String mode, String sid1){
        int i;
        LocalDate today = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);

        Optional<Keywords> exists = keywordsRepository.findById(formattedDate+sid1+mode+".txt");
        if(exists.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Keywords keywords = exists.get();
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try{
            json = objectMapper.writeValueAsString(keywords);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(json,HttpStatus.OK);
    }

    @GetMapping("/summary")
    @ResponseBody
    public Mono<String> naver(@RequestParam String url){
        try {
            String content = crawlService.crawling(url);
            log.info(content);
            Mono<String> res =  summaryService.requestAsync(content);
            return res;
        }catch (Exception ex){
            ex.printStackTrace();
            return Mono.empty();
        }
    }
    @GetMapping("/search")
    @ResponseBody
    public List<ArticleDto> search(@RequestParam String keyword, String num){
        try{
            return crawlService.keyWordCrawling(keyword);
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
