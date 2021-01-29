package aa.store.crawler.v1.selenium.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SeleniumUtil {

    @Value("${crawler.driver.id}")
    private String DRIVER_ID;

    @Value("${crawler.driver.path}")
    private String DRIVER_PATH;

    public WebDriver getInstance() {

        System.setProperty(DRIVER_ID, DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.setCapability("ignoreProtectedModeSettings", true);
        options.addArguments("--incognito");
        options.addArguments("--allowed-ips");
        return new ChromeDriver(options);

    }
}
