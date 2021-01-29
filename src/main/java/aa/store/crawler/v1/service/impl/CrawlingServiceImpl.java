package aa.store.crawler.v1.service.impl;

import aa.store.crawler.v1.selenium.util.SeleniumUtil;
import aa.store.crawler.v1.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingServiceImpl implements CrawlingService  {

    @Value("${crawler.baseUrl}")
    private String baseUrl;

    private final SeleniumUtil seleniumUtil;

    @Override
    public void RunCrawling() {
        log.info("BaseUrl : {}", baseUrl);

        WebDriver driver = seleniumUtil.getInstance();
        log.info("driver : {}", driver);
        driver.get(baseUrl);
        System.out.println(driver.getPageSource());

        driver.close();
    }

}
