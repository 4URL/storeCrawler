package aa.store.crawler;

import aa.store.crawler.v1.controller.RunController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CrawlerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrawlerApplication.class, args);
        context.getBean(RunController.class).run();
        context.close();
    }

}
