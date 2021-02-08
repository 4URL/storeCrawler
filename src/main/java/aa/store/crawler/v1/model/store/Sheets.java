package aa.store.crawler.v1.model.store;

import aa.store.crawler.v1.model.type.LoggingMode;
import aa.store.crawler.v1.model.type.SheetType;
import lombok.Getter;

@Getter
public enum Sheets {

    RESTAURANT("식당", 1, SheetType.ORIGINAL),
    CAFE("카페", 2, SheetType.ORIGINAL),
    HOTEL_AND_RESORT("호텔&리조트", 3, SheetType.ORIGINAL),
    PET_HOTEL("반려동물호텔", 4, SheetType.ORIGINAL),
    PENSION("펜션", 5, SheetType.ORIGINAL),
    PET_PLAY_GROUND("애견놀이터", 6, SheetType.ORIGINAL),
    ETC("기타", 99, SheetType.ORIGINAL),

    RESTAURANT_NEW("식당_신규", 1, SheetType.NEW),
    CAFE_NEW("카페_신규", 2, SheetType.NEW),
    HOTEL_AND_RESORT_NEW("호텔&리조트_신규", 3, SheetType.NEW),
    PENSION_NEW("펜션_신규", 5, SheetType.NEW),
    ;

    private final String sheetName;
    private final int category;
    private final SheetType sheetType;

    Sheets(String sheetName, int category, SheetType sheetType) {
        this.sheetName = sheetName;
        this.category = category;
        this.sheetType = sheetType;
    }

    public static Sheets findSheets(String code) {
        return Sheets.valueOf(code);
    }
}
