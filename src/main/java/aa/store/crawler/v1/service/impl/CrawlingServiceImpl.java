package aa.store.crawler.v1.service.impl;

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

    @Value("${crawler.driver.id}")
    private String DRIVER_ID;

    @Value("${crawler.driver.path}")
    private String DRIVER_PATH;

    @Override
    public void RunCrawling() {
        log.info("BaseUrl : {}", baseUrl);

        System.setProperty(DRIVER_ID, DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.setCapability("ignoreProtectedModeSettings", true);
        options.addArguments("--incognito");
        options.addArguments("--allowed-ips");
        WebDriver driver = new ChromeDriver(options);

        driver.get(baseUrl);
        System.out.println(driver.getPageSource());

    }

}
