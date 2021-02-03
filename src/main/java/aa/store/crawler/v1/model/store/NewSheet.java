package aa.store.crawler.v1.model.store;

import lombok.Getter;

@Getter
public enum NewSheet {

    RESTAURANT("식당_NEW", "식당", 1),
    CAFE("카페_NEW", "카페", 2),
    HOTEL("호텔_NEW", "호텔&리조트", 3),
    PENSION("펜션_NEW", "펜션",5),
    ;

    private final String sheetName;
    private final String originalSheetName;
    private final int category;

    NewSheet(String sheetName, String originalSheetName, int category) {
        this.sheetName = sheetName;
        this.originalSheetName = originalSheetName;
        this.category = category;
    }

    public static NewSheet findSheets(String code) {
        return NewSheet.valueOf(code);
    }
}
