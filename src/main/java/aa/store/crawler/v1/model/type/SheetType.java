package aa.store.crawler.v1.model.type;

public enum SheetType {

    ORIGINAL("기존 시트"),
    NEW("신규시트")
    ;

    private final String description;

    SheetType(String description) {
        this.description = description;
    }

    public static SheetType findMapType(String code) {
        return SheetType.valueOf(code);
    }

}
