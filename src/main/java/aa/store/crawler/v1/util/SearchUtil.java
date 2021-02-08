package aa.store.crawler.v1.util;

import aa.store.crawler.v1.config.CrawlerConfig;
import aa.store.crawler.v1.model.naver.Detail;
import aa.store.crawler.v1.model.naver.StoreInfo;
import aa.store.crawler.v1.model.store.Sheets;
import aa.store.crawler.v1.model.store.Store;
import aa.store.crawler.v1.model.type.MapType;
import aa.store.crawler.v1.model.type.SheetType;
import aa.store.crawler.v1.validation.MapTypeValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchUtil {

    private final CrawlerConfig config;

    private final LoggingUtil loggingUtil;

    private final MapTypeValidation mapTypeValidation;

    private static final String NAVER_URL = "https://www.naver.com";

    public void getNewDatabase(WebDriver driver, Map<String, List<Store>> database) {

        for(Sheets sheet : Sheets.values()) {
            if(sheet.getSheetType() == SheetType.ORIGINAL) continue;

            List<String> searchKeywords;
            switch (sheet) {
                case RESTAURANT_NEW -> searchKeywords = config.getSearch().getRestaurant();
                case CAFE_NEW -> searchKeywords = config.getSearch().getCafe();
                case HOTEL_AND_RESORT_NEW -> searchKeywords = config.getSearch().getHotel();
                case PENSION_NEW -> searchKeywords = config.getSearch().getPension();
                default -> searchKeywords = new ArrayList<>();
            }

            for(String keyword : searchKeywords) {
                searchNaver(driver, keyword);
                collectData(driver, database, sheet);
            }
        }
    }

    private void searchNaver(WebDriver driver, String text) {
        // 이동
        driver.get(NAVER_URL);
        if(!"NAVER".equals(driver.getTitle())) return;

        // 키워드 입력 후 검색 실행
        WebElement element = driver.findElement(By.name("query"));
        element.clear();
        element.sendKeys(text);
        element.submit();
    }

    private void collectData(WebDriver driver, Map<String, List<Store>> database, Sheets sheet) {
        List<WebElement> elements = driver.findElements(By.cssSelector("a.tab"));
        for(WebElement element : elements) {
            if(element.getText().contains("VIEW")) {
                element.click();
                break;
            }
        }

        elements = driver.findElements(By.cssSelector("a.item"));
        for(WebElement element : elements) {
            if(element.getText().contains("블로그")) {
                element.click();
                break;
            }
        }

        boolean bSuccess = false;

        for(int i = 0; i < 5; i ++) {
            if(setStartDate(driver)) {
                bSuccess = true;
                break;
            } else {
                loggingUtil.writeFile("[Failed Set Start Date] Try Count : " + i);
            }
        }
        if(!bSuccess) {
            return;
        }

        String firstUrl = driver.getCurrentUrl();
        List<String> baseUrls = new ArrayList<>();
        for(long i = config.getSearch().getParams().getStartPage(); i <= config.getSearch().getParams().getEndPage(); i ++) {
            String url = firstUrl + "&start=" + ((i - 1) * 10 + 1);
            baseUrls.add(url);
        }

        List<String> links = new ArrayList<>();
        int nBaseCnt = 0;
        for(String baseUrl : baseUrls) {
            driver.get(baseUrl);
            for(int i = 1; i < 11; i ++) {
                WebElement element = driver.findElement(By.id("sp_blog_" + ((10 * nBaseCnt) + i)));
                List<WebElement> elementChilds = element.findElements(By.xpath("//div[contains(@class,'total_wrap api_ani_send')]/a"));
                for(WebElement elementChild : elementChilds) {
                    String link = elementChild.getAttribute("href");
                    if(!links.contains(link)) {
                        links.add(link);
                        break;
                    }
                }
            }
            nBaseCnt ++;
        }

        for(String link : links) {
            driver.get(link);
            searchMapLink(driver, database, sheet);
        }
    }

    private boolean setStartDate(WebDriver driver) {
        boolean bOpenOpt = false;
        try {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector("a.m._tab_option_"));
                for (WebElement element : elements) {
                    if (element.getText().contains("기간")) {
                        bOpenOpt = true;
                        break;
                    }
                }
            } catch (Exception e) {
                loggingUtil.writeFile("[Failed Set Start Date (1)] - Failed to Open Check");
                return false;
            }

            try {
                if (!bOpenOpt) {
                    WebElement element = driver.findElement(By.className("bt_option"));
                    element.click();
                }
            } catch (Exception e) {
                loggingUtil.writeFile("[Failed Set Start Date (2)] - Failed to Open Opt");
                return false;
            }

            try {
                List<WebElement> elements = driver.findElements(By.cssSelector("a.m._tab_option_"));
                for (WebElement element : elements) {
                    if (element.getText().contains("기간")) {
                        element.click();
                        break;
                    }
                }
            } catch (Exception e) {
                loggingUtil.writeFile("[Failed Set Start Date (3)] - Failed to Open Date Opt");
                return false;
            }

            try {
                WebElement element = driver.findElement(By.cssSelector("div.input_box._box_min_"));
                List<WebElement> elements = element.findElements(By.xpath("//input"));
                for (WebElement input : elements) {
                    if (input.getAttribute("title").contains("검색기간 시작일")) {
                        input.click();
                        input.sendKeys(config.getSearch().getParams().getStartDate());
                        break;
                    }
                }
            } catch (Exception e) {
                loggingUtil.writeFile("[Failed Set Start Date (4)] - Failed to Set Start Date");
                return false;
            }

            WebElement element = driver.findElement(By.className("tx"));
            element.click();
            return true;
        } catch (Exception e) {
            loggingUtil.writeFile("[Failed Set Start Date (5)] - Finally Failed to Set Start Date");
            return false;
        }
    }

    private void searchMapLink(WebDriver driver, Map<String, List<Store>> database, Sheets sheet) {
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for(WebElement iframe : iframes) {
            try {
                driver.switchTo().frame(iframe);
                List<WebElement> tags = driver.findElements(By.cssSelector("a.se-map-info.__se_link"));
                for(WebElement tag : tags) {
                    if("map".equals(tag.getAttribute("data-linktype"))) {
                        tag.click();
                    }
                }
                driver.switchTo().defaultContent();
            } catch (Exception e) {
                driver.switchTo().defaultContent();
                continue;
            }
        }
        getMapInfo(driver, database, sheet);
    }

    private void getMapInfo(WebDriver driver, Map<String, List<Store>> database, Sheets sheet) {
        String postUrl = driver.getCurrentUrl();
        String postTitle = driver.getTitle();
        log.info("Post Title : {}, Post Url : {}", postTitle, postUrl);

        String parent = driver.getWindowHandle();
        Set<String> childs = driver.getWindowHandles();
        Iterator<String> childIterator = childs.iterator();
        while (childIterator.hasNext()) {
            String childWindow = childIterator.next();

            if(!parent.equals(childWindow)) {
                driver.switchTo().window(childWindow);

                MapType mapType = mapTypeValidation.checkMapType(driver);
                if(mapType == MapType.INVALID) {
                    loggingUtil.writeFile("[Invalid Map Type] Post URL : " + postUrl + ", Map URL : " + driver.getCurrentUrl());
                    continue;
                }
                getStoreInfo(driver, database, mapType, sheet, postUrl, postTitle);
                driver.close();
            }
        }

        driver.switchTo().window(parent);
    }

    private void getStoreInfo(WebDriver driver, Map<String, List<Store>> database, MapType mapType, Sheets sheet, String postUrl, String postTitle) {
        Store store = new Store();
        store.setMapUrl(driver.getCurrentUrl());
        store.setCount(1);
        store.setPostUrl(postUrl);
        store.setPostTitle(postTitle);

        if (mapType == MapType.A) {
            WebElement nameAreaElement = driver.findElement(By.className("biz_name_area"));

            // 업체명
            store.setPlaceName(nameAreaElement.findElement(By.cssSelector("strong[class='name']")).getText());
            // 카테고리
            store.setCategory(nameAreaElement.findElement(By.xpath("//span[contains(@class,'category')]")).getText());

            // 편의시설
            List<WebElement> elements = driver.findElements(By.className("list_item"));
            for(WebElement element : elements) {
                try {
                    if(element.getText().contains("편의시설")) {
                        String rawConvenience = element.findElement(By.cssSelector("div.txt")).getText();
                        store.setConvenience("#" + rawConvenience.replace(", ", " #"));
                    }
                } catch (Exception e) {
                    log.error("Map A Convenience Error : {}", e);
                }
            }

        } else if(mapType == MapType.B) {
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for(WebElement iframe : iframes) {
                try {
                    driver.switchTo().frame(iframe);

                    store.setPlaceName(driver.findElement(By.className("_3XamX")).getText());
                    store.setCategory(driver.findElement(By.className("_3ocDE")).getText());
                    try {
                        List<WebElement> elemUndefineds = driver.findElements(By.cssSelector("li._1M_Iz.undefined"));
                        for(WebElement elemUndefined : elemUndefineds) {
                            if(elemUndefined.getText().contains("편의")) {
                                String convinience = elemUndefined.getText().replace("편의\n", "");
                                if(!"".equals(convinience)) {
                                    store.setConvenience("#" + convinience.replace(", ", " #"));
                                }
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Map B Error : {}", e);
                    }
                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                    log.error("Map B Error : {}", e);
                    driver.switchTo().defaultContent();
                }
            }
        } else if(mapType == MapType.INVALID) {
            loggingUtil.writeFile("Invalid Map Type : Map URL : " + store.getMapUrl());
            return;
        }

        if(store.getPlaceName() == null || !getDetailInfo(driver, store)) {
            return;
        }

        boolean updateYn = checkExistStore(database, sheet.getSheetName(), store);
        if(!updateYn) {
            insertStore(database, sheet.getSheetName(), store);
        }
    }

    private boolean getDetailInfo(WebDriver driver, Store store) {
        String storeInfoUrl = "https://map.naver.com/v5/api/search?query=" + store.getPlaceName().replace(" ", "%20");
        try {
            driver.get(storeInfoUrl);

            String infoJson = driver.findElement(By.tagName("pre")).getText();
            ObjectMapper om = new ObjectMapper();
            StoreInfo info = om.readValue(infoJson, StoreInfo.class);
            List<Detail> details = info.getResult().getPlace().getList();

            boolean isExistStore = false;
            Detail detail = new Detail();
            for(Detail tmpDetail : details) {
                if(store.getPlaceName().equals(tmpDetail.getName())) {
                    isExistStore = true;
                    detail = new Detail(tmpDetail);
                }
            }

            if(!isExistStore) {
                loggingUtil.writeFile("[Not Exist Detail Data] Place Name : " + store.getPlaceName());
                return false;
            }

            String tel = null;
            if(!StringUtils.isEmpty(detail.getVirtualTel())) {
                tel = detail.getVirtualTel();
            } else if(!StringUtils.isEmpty(detail.getTel())) {
                tel = detail.getTel();
            }
            store.setTel(tel);
            store.setBusinessHour(!StringUtils.isEmpty(detail.getBizhourInfo()) ? detail.getBizhourInfo() : null);
            store.setHomepage(!StringUtils.isEmpty(detail.getHomePage()) ? detail.getHomePage() : null);
            store.setDescription((detail.getMicroReview() != null && detail.getMicroReview().size() > 0) ? detail.getMicroReview().get(0) : null);
            store.setShortAddress((detail.getShortAddress() != null && detail.getShortAddress().size() > 0) ? detail.getShortAddress().get(0) : null);
            store.setAddress(!StringUtils.isEmpty(detail.getAddress()) ? detail.getAddress() : null);
            store.setRoadAddress(!StringUtils.isEmpty(detail.getRoadAddress()) ? detail.getRoadAddress() : null);
            store.setLatitude(detail.getX());
            store.setLongitude(detail.getY());
            log.info("[Store] {}", store);
            return true;
        } catch (Exception e) {
                loggingUtil.writeFile("[Failed Get Store Info] API URL - " + storeInfoUrl);
            return false;
        }
    }

    private boolean checkExistStore(Map<String, List<Store>> database, String sheetName, Store store) {
        if(database.get(sheetName) != null) {
            for(Store data : database.get(sheetName)) {
                if(store.getPlaceName().equals(data.getPlaceName())) {
                    if(!store.getPostUrl().contains(data.getPostUrl())) {
                        data.setPostTitle(data.getPostTitle() + "\n" + store.getPostTitle());
                        data.setPostUrl(data.getPostUrl() + "\n" + store.getPostUrl());
                        data.setCount(data.getCount() + 1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void insertStore(Map<String, List<Store>> database, String sheetName, Store store) {
        if(database.get(sheetName) != null) {
            boolean checkExist = false;
            for(Store data : database.get(sheetName)) {
                if(store.getPlaceName().equals(data.getPlaceName()) &&
                        !store.getPostUrl().contains(data.getPostUrl())) {
                    data.setPostTitle(data.getPostTitle() + "\n" + store.getPostTitle());
                    data.setPostUrl(data.getPostUrl() + "\n" + store.getPostUrl());
                    data.setCount(data.getCount() + 1);
                    checkExist = true;
                    break;
                }
            }
            if(!checkExist) {
                List<Store> tmpList = database.get(sheetName) == null ? new ArrayList<>() : database.get(sheetName);
                tmpList.add(store);
                database.put(sheetName, tmpList);
            }
        } else {
            List<Store> stores = new ArrayList<>();
            stores.add(store);
            database.put(sheetName, stores);
        }
    }

}
