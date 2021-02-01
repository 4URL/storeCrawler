package aa.store.crawler.v1.config;

import aa.store.crawler.v1.model.type.LoggingMode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "crawler")
@Slf4j
public class CrawlerConfig {

    @Getter
    @Setter
    public static class Driver {
        private String id;
        private String path;
    }

    @Getter
    @Setter
    public static class Files {
        private String database;
        private String result;
        private String log;
    }

    @Getter
    @Setter
    public static class Logging {
        private LoggingMode mode;
    }

    @Getter
    @Setter
    public static class Search {
        private List<String> restaurant;
        private List<String> cafe;
        private List<String> hotel;
        private List<String> pension;
    }

    private Driver driver = new Driver();

    private Files files = new Files();

    private Logging logging = new Logging();

    private Search search = new Search();

    private boolean ignoreProtectedModeSettings;

    private boolean useAllowedIps;

    private boolean useSecretMode;

    private boolean useHeadlessMode;

    @PostConstruct
    private void init() {
        StringBuilder builder = new StringBuilder();

        builder.append(System.lineSeparator()).append("[Configurations]").append(System.lineSeparator());
        builder.append("\t").append("Driver ID").append(" = ").append(driver.getId()).append(System.lineSeparator());
        builder.append("\t").append("Driver Path").append(" = ").append(driver.getPath()).append(System.lineSeparator());
        builder.append("\t").append("Ignore Protected Mode Settings").append(" = ").append(ignoreProtectedModeSettings).append(System.lineSeparator());
        builder.append("\t").append("Use Allowed IPs").append(" = ").append(useAllowedIps).append(System.lineSeparator());
        builder.append("\t").append("Use Secret Mode").append(" = ").append(useSecretMode).append(System.lineSeparator());
        builder.append("\t").append("Use Headless Mode").append(" = ").append(useHeadlessMode).append(System.lineSeparator());
        builder.append("\t").append("Database File Path").append(" = ").append(files.getDatabase()).append(System.lineSeparator());
        builder.append("\t").append("Result File Path").append(" = ").append(files.getResult()).append(System.lineSeparator());
        builder.append("\t").append("Log File Path").append(" = ").append(files.getLog()).append(System.lineSeparator());
        builder.append("\t").append("Logging Mode").append(" = ").append(logging.getMode()).append(System.lineSeparator());
        builder.append("\t").append("Search Keywords Restaurant").append(" = ").append(search.getRestaurant()).append(System.lineSeparator());
        builder.append("\t").append("Search Keywords Cafe").append(" = ").append(search.getCafe()).append(System.lineSeparator());
        builder.append("\t").append("Search Keywords Hotel").append(" = ").append(search.getHotel()).append(System.lineSeparator());
        builder.append("\t").append("Search Keywords Pension").append(" = ").append(search.getPension()).append(System.lineSeparator());

        log.info("{}", builder.toString());
    }

}
