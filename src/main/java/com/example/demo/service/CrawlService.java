package com.example.demo.service;

import com.example.demo.entity.News;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CrawlService {
    /*
    필요한 기능
    1. 일정 시간동안 생성된 기사 크롤링 후 .txt 파일로 저장
    2. 뉴스에 태그다는
     */
    public Long collectingNews() throws Exception {
        Long res = 0L;
        Map<Integer, StringBuilder> sidDataMap = new HashMap<>();

        for (int sid2 = 249; sid2 <= 257; sid2++) {
            int pages = 100;
            StringBuilder dataBuilder = new StringBuilder(); //제목과 기사가 아예 같은 경우도 있음 -> 해쉬셋으로 변경 필요
            String check = "https://news.naver.com/main/list.naver?mode=LS2D&sid2=" + sid2 + "&sid1=102&mid=shm&date=20230808&page=" + pages;
            String page_check = Jsoup.connect(check).get().select("#main_content > div.paging > strong").get(0).text();
            log.info(page_check);
            pages = Integer.parseInt(page_check);
            while (pages!=0) {
                String url = "https://news.naver.com/main/list.naver?mode=LS2D&sid2=" + sid2 + "&sid1=102&mid=shm&date=20230808&page=" + pages;
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

            sidDataMap.put(sid2, dataBuilder);
        }

        // 각 sid에 해당하는 데이터를 파일로 저장
        for (Map.Entry<Integer, StringBuilder> entry : sidDataMap.entrySet()) {
            saveToTxtFile(entry.getValue().toString(), "20230808_" + entry.getKey() + ".txt");
        }

        return res;
    }

    private void saveToTxtFile(String content, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
