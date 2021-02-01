package aa.store.crawler.v1.controller;

import aa.store.crawler.v1.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "RunController")
public class RunController {

    private final CrawlingService crawlingService;

    public void run() {
        try {
            crawlingService.RunCrawling();
        } catch (Exception e) {
            log.info("{}", e);
        }
    }
}
