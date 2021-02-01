package aa.store.crawler.v1.model.store;

import lombok.Getter;

@Getter
public enum NewSheets {

    RESTAURANT("식당_NEW"),
    CAFE("카페_NEW"),
    HOTEL("호텔_NEW"),
    PENSION("펜션_NEW"),
    ;

    private final String sheetName;

    NewSheets(String sheetName) {
        this.sheetName = sheetName;
    }

    public static NewSheets findSheets(String code) {
        return NewSheets.valueOf(code);
    }
}
