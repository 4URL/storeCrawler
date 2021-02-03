package aa.store.crawler.v1.util;

import aa.store.crawler.v1.config.CrawlerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeleniumUtil {

    private final CrawlerConfig config;

    public WebDriver getInstance() {

        System.setProperty(config.getDriver().getId(), config.getDriver().getPath());
        ChromeOptions options = new ChromeOptions();
        options.setCapability("ignoreProtectedModeSettings", config.isIgnoreProtectedModeSettings());
        if(config.isUseAllowedIps()) options.addArguments("--allowed-ips");
        if(config.isUseSecretMode()) options.addArguments("--incognito");
        if(config.isUseHeadlessMode()) options.addArguments("--headless");
        return new ChromeDriver(options);

    }
}
