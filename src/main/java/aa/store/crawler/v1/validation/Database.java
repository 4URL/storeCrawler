package aa.store.crawler.v1.validation;

import aa.store.crawler.v1.model.store.Sheets;
import aa.store.crawler.v1.model.store.Store;
import aa.store.crawler.v1.util.Selenium;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Database {

    private final MapType mapTypeValidation;

    public Map<String, List<Store>> checkDatabase(WebDriver driver, Map<String, List<Store>> database) {
        Map<String, List<Store>> newDatabase = new HashMap<>();

        log.info("=========== 데이터 검증 전 ===========");
        for(Sheets sheet : Sheets.values()) {
            log.info("[{}] 업체 수 : {}", sheet.getSheetName(), database.get(sheet.getSheetName()).size());
            List<Store> stores = database.get(sheet.getSheetName());
            List<Store> newStores = new ArrayList<>();
            for(Store store : stores) {
                driver.get(store.getMapUrl());
                if(!mapTypeValidation.isTypeA(driver)
                        && !mapTypeValidation.isTypeB(driver)
                        && !mapTypeValidation.isTypeC(driver)) {
                    log.info("[업체 제외] {}", store);
                    continue;
                }
                newStores.add(store);
            }
            newDatabase.put(sheet.getSheetName(), newStores);
        }

        return newDatabase;
    }

}
