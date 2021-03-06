package aa.store.crawler.v1.model.store;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Store {

    private String placeName;
    private String category;
    private String tel;
    private String businessHour;
    private String homepage;
    private String description;
    private String convenience;
    private String shortAddress;
    private String address;
    private String roadAddress;
    private String latitude;
    private String longitude;
    private String mapUrl;
    private String postTitle;
    private String postUrl;
    private int count;

}
