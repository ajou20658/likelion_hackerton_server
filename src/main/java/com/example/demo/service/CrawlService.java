package com.example.demo.service;

import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.CrawlDto;
import com.example.demo.entity.News;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CrawlService {
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private SummaryService summaryService;
    private final static int maxlength = 2000;
    /*
    필요한 기능
    1. 일정 시간동안 생성된 기사 크롤링 후 .txt 파일로 저장
    2. 뉴스에 태그다는
     */
    @Scheduled(cron = "0 0 0 * * *")
    public Long collectingNews() throws Exception {
        Long res = 0L;
        Map<Integer, StringBuilder> sidDataMap = new HashMap<>();
        LocalDate today = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        Map<Integer, String[]> sidRanges = new HashMap<>();
        sidRanges.put(100, new String[] {"264","265","268","266","267","269"});//정치
        sidRanges.put(101, new String[] {"258","259","260","261","771","260","262","310","263"});//경제
        sidRanges.put(102, new String[] {"249","250","251","254","252","59b","255","256","276","257"});//사회
        sidRanges.put(103, new String[] {"241","239","240","237","238","376","242","243","244","248","245"});//생활문화
        sidRanges.put(105, new String[] {"731","226","227","230","732","283","229","228"});//IT/과학
        sidRanges.put(104, new String[] {"231","232","233","234","322"});//세계

        for(Map.Entry<Integer,String[]> entry: sidRanges.entrySet()){
            Integer sid1 = entry.getKey();
            String[] sid2Values = entry.getValue();

            StringBuilder dataBuilder = new StringBuilder();

            for (String sid2 : sid2Values){
                int pages = 100;
                String check = "https://news.naver.com/main/list.naver?mode=LS2D&sid2=" + sid2 + "&sid1="+sid1+"&mid=shm&date="+formattedDate+"&page=" + pages;
                String page_check = Jsoup.connect(check).get().select("#main_content > div.paging > strong").get(0).text();
                log.info(page_check);
                pages = Integer.parseInt(page_check);
                while (pages!=0) {
                    String url = "https://news.naver.com/main/list.naver?mode=LS2D&sid2=" + sid2 + "&sid1="+sid1+"&mid=shm&date="+formattedDate+"&page=" + pages;
                    Document doc = Jsoup.connect(url).get();
                    Elements element = doc.select("#main_content > div.list_body.newsflash_body > ul.type06_headline");

                    if (element.isEmpty()) {
                        break; // 페이지가 조회되지 않으면 반복문 종료
                    }

                    for (Element news : element.select("li")) {
                        log.info(news.text());
                        res += 1;
                        dataBuilder.append(news.text()).append("\n");
                    }

                    pages--;
                }
            }
            sidDataMap.put(sid1, dataBuilder);
        }

        // 각 sid에 해당하는 데이터를 파일로 저장
        for (Map.Entry<Integer, StringBuilder> entry : sidDataMap.entrySet()) {
            saveToTxtFile(entry.getValue().toString(), formattedDate + entry.getKey() + ".txt");
        }

        return res;
    }

    public void saveToTxtFile(String content, String fileName) {
        try {
            // Resolve the file path within the "static" folder
            Path filePath = Path.of("/home/ubuntu/git/likelion_hackerton_server/src/main/resources/static/" + fileName);

            // Create the parent directories if they don't exist
            Files.createDirectories(filePath.getParent());

            // Create the file if it doesn't exist
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            // Write content to the file
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CrawlDto crawlingContent(String url) throws Exception{
        Document doc = Jsoup.connect(url).get();
        String content;
        String title;
        if(url.contains("n.news.naver.com")) {
            content = doc.select(".newsct_article._article_body").text();
            title = doc.select("#title_area").text();
        } else if (url.contains("sports.news.naver.com")) {
            content = doc.select("#newsEndContents").text();
            title = doc.select("#content > div > div.content > div > div.news_headline > h4").text();

        } else{
            content = doc.select("#articeBody").text();
            title = doc.select("#content > div.end_ct > div > h2").text();
        }

        content = content.replace("\""," ");
        content = content.replace("\n"," ");
        content = content.replace("\\"," ");
        if (content.length()>maxlength){
            content = content.substring(0,maxlength);
            return CrawlDto.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
        return CrawlDto.builder()
                .title(title)
                .content(content)
                .build();
    }
    public String crawling(String url) throws Exception{
        Document doc = Jsoup.connect(url).get();
        String content;
        if(url.contains("n.news.naver.com")) {
            content = doc.select(".newsct_article._article_body").text();
        } else if (url.contains("sports.news.naver.com")) {
            content = doc.select("#newsEndContents").text();
        } else{
            content = doc.select("#articeBody").text();
        }

        content = content.replace("\""," ");
        content = content.replace("\n"," ");
        content = content.replace("\\"," ");
        if (content.length()>maxlength){
            content = content.substring(0,maxlength);
            return content;
        }
        return content;
    }
    public List<ArticleDto> keyWordCrawling(String keyword) throws Exception{
        String url= "https://search.naver.com/search.naver?where=news&query="+keyword+"&sm=tab_opt&sort=0&photo=0&field=0&pd=0&ds=&de=&docid=&related=0&mynews=0&office_type=0&office_section_code=0&news_office_checked=&nso=so%3Ar%2Cp%3Aall&is_sug_officeid=0";
        Elements elements = Jsoup.connect(url).get().select("#main_pack > section > div > div.group_news > ul > li");
        List<ArticleDto> lists = new ArrayList<>();
        for(Element e:elements){
//            System.out.println("elements"+ e.text());
            Elements li = e.select("div.news_wrap.api_ani_send > div > div.news_info > div.info_group");
            if(li.text().contains("네이버뉴스")){
                Element secondA = li.select("a").last();
                String press = li.select("a").first().text();
                String img = e.select("div.news_wrap.api_ani_send > a > img").attr("data-lazysrc");

                if(press.contains("언론사 선정")){
                    press = press.replace("언론사 선정","");
                }
                String origin = secondA.attr("href");
                try {
                    CrawlDto crawlDto = crawlingContent(origin);
                    Mono<String> res =  summaryService.requestAsync(crawlDto.getContent()).map(jsonNode -> jsonNode.get("summary").asText());
                    String summary = res.block();
                    lists.add(ArticleDto.builder()
                            .summary(summary)
                            .press(press)
                            .title(crawlDto.getTitle())
                            .origin(origin)
                            .img(img)
                            .build());
//                    System.out.println("lists : " + lists);
                }catch (Exception ex){
                    ex.printStackTrace();
                    return null;
                }
            }

        }
        return lists;
    }
}
