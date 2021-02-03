package aa.store.crawler.v1.model.store;

import aa.store.crawler.v1.model.type.LoggingMode;
import lombok.Getter;

@Getter
public enum Sheets {

    RESTAURANT("식당", 1),
    CAFE("카페", 2),
    HOTEL_AND_RESORT("호텔&리조트", 3),
    PET_HOTEL("반려동물호텔", 4),
    PENSION("펜션", 5),
    PET_PLAY_GROUND("애견놀이터", 6),
    ETC("기타", 99)
    ;

    private final String sheetName;
    private final int category;

    Sheets(String sheetName, int category) {
        this.sheetName = sheetName;
        this.category = category;
    }

    public static Sheets findSheets(String code) {
        return Sheets.valueOf(code);
    }
}
