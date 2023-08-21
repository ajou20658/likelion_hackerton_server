package com.example.demo.controller;

import com.example.demo.entity.Keywords;
import com.example.demo.entity.MongoSave;
import com.example.demo.entity.Save;
import com.example.demo.repository.KeywordsRepository;
import com.example.demo.repository.SaveRepository;
import com.example.demo.service.CrawlService;
import com.example.demo.service.FlaskService;
import com.example.demo.service.SummaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private SaveRepository saveRepository;

    @GetMapping("/hello")
    public String hello(){
        return "index";
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
    @GetMapping("/extract-keyword")
    public void flaskreq(){
        flaskService.iter("0");
    }

    @GetMapping("/keyword")
    @ResponseBody
    public ResponseEntity<String> flask(@RequestParam String mode, String sid1){
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
    @GetMapping("/save-news")//{keyword:[{뉴스1},{뉴스2}]}
    public void save(@RequestParam String sid1){
        LocalDate today = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        Set<Object> keywords = new HashSet<>();
        //키워드 저장

        try{
            Optional<Keywords> exists = keywordsRepository.findById(formattedDate+sid1+"0"+".txt");
            if(exists.isEmpty()){
                return;
            }
            keywords.addAll(exists.get().getResponse().values());
        }catch(Exception ex){
            ex.printStackTrace();
        }

//        System.out.println("keywords = " + keywords);
//        List<Object> TenKeyword = keywords.stream().limit(10).collect(Collectors.toList());
        for(Object value: keywords){
//            System.out.println("value = " + value);

            try{
                Thread.sleep(1000);
                List<Save> save = crawlService.keyWordCrawling((String)value);
                System.out.println("saved count : " + save.size());
                saveRepository.save(MongoSave.builder()
                                .id((String)value)
                                .response(save)
                        .build())
                ;
                System.out.println("completed");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        //키워드들 조회, 뉴스 검색후 저장

    }
    @GetMapping("/save-summary")//{keyword:[{뉴스1},{뉴스2}]}<-각각에 summary추가
    public void save2(@RequestParam String sid1){
        LocalDate today = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        Set<Object> keywords = new HashSet<>();
        //키워드 저장

        try{
            Optional<Keywords> exists = keywordsRepository.findById(formattedDate+sid1+"0"+".txt");
            if(exists.isEmpty()){
                return;
            }
            keywords.addAll(exists.get().getResponse().values());
        }catch(Exception ex){
            ex.printStackTrace();
        }


//        List<Object> TenKeyword = keywords.stream().limit(10).collect(Collectors.toList());
        System.out.println("TenKeyword = " + keywords);
        for(Object value: keywords){
//            System.out.println("value = " + value);
                MongoSave save = saveRepository.findById((String) value).get();
                List<Save> save2 = save.getResponse();
                List<Save> updated = new ArrayList<>();
                for(Save a:save2){
                    try{
                        Thread.sleep(100);
                        String content = crawlService.crawling(a.getOriginUrl());
                        String[] sentences = content.split("[.!?]");
                        boolean shouldRemove = false;
                        log.info("OK");
                        for (String sentence : sentences) {
                            if (wordCount(sentence) < 5) {
                                shouldRemove = true;
                                break;
                            }
                        }
                        log.info("OK");
                        if (shouldRemove) {
//                            System.out.println("save2 = " + save2);
                            continue;
                        }
                        log.info("OK");
                        String summary = summaryService.requestAsync(content).block().get("summary").asText();
//                        System.out.println("summary = " + summary);
                        a.setSummary(summary);
                        updated.add(a);
                    }catch (Exception ex){
                        continue;
                    }
                }
                save.setResponse(updated);
                saveRepository.save(save);
        }
    }
    private int wordCount(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
    @GetMapping("/summary")
    @ResponseBody
    public Mono<JsonNode> naver(@RequestParam String url){
        try {
            String content = crawlService.crawling(url);
//            log.info(content);
            if(content.isEmpty()){
                log.info("Empty content = {}" + content,url);
                return Mono.empty();
            }
            return summaryService.requestAsync(content);
        }catch (Exception ex){
            ex.printStackTrace();
            return Mono.empty();
        }
    }
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Object> search(@RequestParam String keyword){
        try{
            Optional<MongoSave> repo = saveRepository.findById(keyword);
            if(repo.isPresent()){
                System.out.println("hello");
                List<Save> result = repo.get().getResponse();
//                System.out.println("result = " + result);
                Map<String,Object> response = new HashMap<>();
                response.put("response",result);
                return new ResponseEntity<>(response,HttpStatus.OK);
            } else {
                List<Save> save = crawlService.keyWordCrawling(keyword);
                List<Save> updated = new ArrayList<>();
                for(Save a:save){
                    try{
                        Thread.sleep(100);
                        String content = crawlService.crawling(a.getOriginUrl());
                        String[] sentences = content.split("[.!?]");
                        boolean shouldRemove = false;
                        log.info("OK");
                        for (String sentence : sentences) {
                            if (wordCount(sentence) < 5) {
                                shouldRemove = true;
                                break;
                            }
                        }
                        log.info("OK");
                        if (shouldRemove) {
//                            System.out.println("save = " + save);
                            continue;
                        }
                        log.info("OK");
                        String summary = summaryService.requestAsync(content).block().get("summary").asText();
//                        System.out.println("summary = " + summary);
                        a.setSummary(summary);
                        updated.add(a);
                    }catch (Exception ex){
                        continue;
                    }
                }
                saveRepository.save(MongoSave.builder()
                        .id(keyword)
                        .response(updated)
                        .build());
                Map<String,Object> response = new HashMap<>();
                response.put("response",updated);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    @GetMapping("/headline")
    public ResponseEntity<Object> headline(@RequestParam String sid1){
        List<Save> result = crawlService.Headline(sid1);
        List<Save> updated = new ArrayList<>();
        for(Save a:result){
            try{
                Thread.sleep(50);
                String content = crawlService.crawling(a.getOriginUrl());
                String[] sentences = content.split("[.!?]");
                boolean shouldRemove = false;
                log.info("OK");
                for (String sentence : sentences) {
                    if (wordCount(sentence) < 5) {
                        shouldRemove = true;
                        break;
                    }
                }
                log.info("OK");
                if (shouldRemove) {
//                            System.out.println("save = " + save);
                    continue;
                }
                log.info("OK");
                String summary = summaryService.requestAsync(content).block().get("summary").asText();
//                        System.out.println("summary = " + summary);
                a.setSummary(summary);
                updated.add(a);
            }catch (Exception ex){
                continue;
            }
        }
        Map<String,Object> response = new HashMap<>();
        response.put("response",updated);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
