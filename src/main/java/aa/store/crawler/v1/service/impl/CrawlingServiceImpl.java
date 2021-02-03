package aa.store.crawler.v1.service.impl;

import aa.store.crawler.v1.config.CrawlerConfig;
import aa.store.crawler.v1.model.store.Sheets;
import aa.store.crawler.v1.model.store.Store;
import aa.store.crawler.v1.util.ExcelUtil;
import aa.store.crawler.v1.util.LoggingUtil;
import aa.store.crawler.v1.util.SearchUtil;
import aa.store.crawler.v1.util.SeleniumUtil;
import aa.store.crawler.v1.service.CrawlingService;
import aa.store.crawler.v1.validation.DatabaseValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingServiceImpl implements CrawlingService  {

    private final CrawlerConfig config;

    private final SeleniumUtil seleniumUtil;

    private final LoggingUtil loggingUtil;

    private final ExcelUtil excelUtil;

    private final SearchUtil searchUtil;

    private final DatabaseValidation databaseValidation;

    private final String BASE_URL = "https://www.naver.com";

    @Override
    public void RunCrawling() throws IOException {

        // 로그 초기화
        loggingUtil.init();

        // Selenium 드라이브 생성
        WebDriver driver = seleniumUtil.getInstance();

        // database.xlsx 읽음
        Map<String, List<Store>> database = excelUtil.getStoreListFromDatabase();

        // Database 검증
//        database = databaseValidation.checkDatabase(driver, database);
        log.info("=========== 데이터 검증 후 ===========");
        for(Sheets sheet : Sheets.values()) {
            log.info("[{}] 업체 수 : {}", sheet.getSheetName(), database.get(sheet.getSheetName()).size());
        }
        log.info("====================================");

        // 새로운 정보 조회
        searchUtil.getNewDatabase(driver, database);

        excelUtil.createResult(database);

        // Selenium 드라이브 종료
        driver.close();
    }

}
