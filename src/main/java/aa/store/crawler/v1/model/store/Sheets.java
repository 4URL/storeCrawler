package aa.store.crawler.v1.model.store;

import aa.store.crawler.v1.model.type.LoggingMode;
import lombok.Getter;

@Getter
public enum Sheets {

    RESTAURANT("식당"),
    CAFE("카페"),
    HOTEL_AND_RESORT("호텔&리조트"),
    PET_HOTEL("반려동물호텔"),
    PENSION("펜션"),
    PET_PLAY_GROUND("애견놀이터"),
    ETC("기타")
    ;

    private final String sheetName;

    Sheets(String sheetName) {
        this.sheetName = sheetName;
    }

    public static Sheets findSheets(String code) {
        return Sheets.valueOf(code);
    }
}
