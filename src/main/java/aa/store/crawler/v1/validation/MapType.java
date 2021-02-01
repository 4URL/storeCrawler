package aa.store.crawler.v1.validation;

import aa.store.crawler.v1.model.store.Sheets;
import aa.store.crawler.v1.model.store.Store;
import aa.store.crawler.v1.util.Selenium;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MapType {

    public boolean isTypeA(WebDriver driver) {
        try {
            driver.findElement(By.className("biz_name_area"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTypeB(WebDriver driver) {
        try {
            if(driver.getCurrentUrl().indexOf("/entry/place") > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean isTypeC(WebDriver driver) {
        try {
            if(driver.getCurrentUrl().indexOf("/map.naver.com/") > 0) {
                driver.findElement(By.cssSelector("button.btn_go_entry"));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
