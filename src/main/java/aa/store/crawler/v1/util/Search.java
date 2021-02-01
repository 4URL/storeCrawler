package aa.store.crawler.v1.util;

import aa.store.crawler.v1.config.CrawlerConfig;
import aa.store.crawler.v1.model.store.NewSheets;
import aa.store.crawler.v1.model.store.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Search {

    private final CrawlerConfig config;

    private static final String NAVER_URL = "https://www.naver.com";

    public void getNewDatabase(WebDriver driver, Map<String, List<Store>> database) {

        for(NewSheets sheets : NewSheets.values()) {
            List<String> searchKeywords;
            switch (sheets) {
                case RESTAURANT -> searchKeywords = config.getSearch().getRestaurant();
                case CAFE -> searchKeywords = config.getSearch().getCafe();
                case HOTEL -> searchKeywords = config.getSearch().getHotel();
                case PENSION -> searchKeywords = config.getSearch().getPension();
                default -> searchKeywords = new ArrayList<>();
            }

            for(String keyword : searchKeywords) {
                searchNaver(driver, keyword);
                collecData(driver, database, sheets);
            }

        }
    }

    private void searchNaver(WebDriver driver, String text) {
        driver.get(NAVER_URL);
        if(!"NAVER".equals(driver.getTitle())) return;

        WebElement element = driver.findElement(By.name("query"));
        element.clear();
        element.sendKeys(text);
        element.submit();
    }

    private void collecData(WebDriver driver, Map<String, List<Store>> database, NewSheets sheets) {

    }
}
