package jin.kisapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KisConfig {
    public static final String REST_BASE_URL = "https://openapi.koreainvestment.com:9443";
    public static final String WS_BASE_URL = "ws://ops.koreainvestment.com:21000";

//    public static final String APPKEY = "PSqiRKrtMWztZDwoadZ1ZCEHyQJhx6h5O3EJ";       // your APPKEY
//    public static final String APPSECRET = "+sb8qRtzStdgVsODhbNpTSFbnGjrzobbPrHiLlmnCf2cu81fcDUafnTI6G6ydZeLsps0eazWvp5I0pfZvMSXM1QkbQufn2hWMmObrp9G5DycTUh3rgcWofWM+5oy3SxkdSam+LUztNBtAGFHpzS091/GJRkoHIGuF1omkGfZm4wYaoSpFnE=";  // your APPSECRET

    // 국내주식 업종 기간 별 시세(일/주/월/년) 거래ID (tr_id = FHKST03030100)
    // 해외주식 종목/지수/환율기간별시세(일/주/월/년) 거래ID (tr_id = FHKST03030100)
    public static final String FHKUP03500100_PATH = "/uapi/domestic-stock/v1/quotations/inquire-daily-indexchartprice";  // 국내 주식 업종기간 별 시세(일/주/월/년) API를 호출
    public static final String FHKST03030100_PATH = "/uapi/overseas-price/v1/quotations/inquire-daily-chartprice";       // 해외주식 종목/지수/환율기간별시세(일/주/월/년) API를 호출

    @Value("${kis.appkey}")
    private String appKey;

    @Value("${kis.appsecret}")
    private String appSecret;

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }
}
