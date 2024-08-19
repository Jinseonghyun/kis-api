package jin.kisapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jin.kisapi.config.AccessTokenManager;
import jin.kisapi.config.KisConfig;
import jin.kisapi.model.Body;
import jin.kisapi.model.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
public class KisController {
    @Autowired
    private AccessTokenManager accessTokenManager;

    private final WebClient webClient;
    private final KisConfig kisConfig;

    private String path;
    private String tr_id;

    public KisController(WebClient.Builder webClientBuilder, KisConfig kisConfig) {
        this.webClient = webClientBuilder.baseUrl(KisConfig.REST_BASE_URL).build();
        this.kisConfig = kisConfig;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/indices") // 주요 지수
    public String majorIndices(Model model) {

        // 주요 지수 데이터를 담고 있는 리스트
        List<Tuple2<String, String>> iscdsAndOtherVariable1 = Arrays.asList(
                Tuples.of("0001", "U"),
                Tuples.of("2001", "U"),
                Tuples.of("1001", "U")
        );

        // 비동기적으로 데이터 요청을 처리 (Flux는 리스트의 각 항목을 비동기적으로 처리)
        // 여러 개의 데이터를 비동기적으로 처리(Flux는 0개 이상의 데이터를 처리)
        // concatMap은 튜플의 각 항목에 대해 getMajorIndex를 호출하고, 모든 Mono 결과를 순차적으로 처리하여 Flux
        Flux<IndexData> indicesFlux = Flux.fromIterable(iscdsAndOtherVariable1)  // Flux를 생성
                .concatMap(tuple -> getMajorIndex(tuple.getT1(), tuple.getT2())) // 각 튜플을 처리하여 Mono<String>을 반환하는 getMajorIndex 메서드를 호출 (비동기적으로 API 요청을 수행하고 결과를 Mono로 반환)
                .map(jsonData -> {                                               // map: Flux의 각 항목을 변환하는 연산입니다. 여기서는 JSON 데이터를 IndexData 객체로 변환
                    ObjectMapper objectMapper = new ObjectMapper();              // ObjectMapper를 사용하여 JSON 문자열을 IndexData 객체로 변환
                    try {
                        return objectMapper.readValue(jsonData, IndexData.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        //collectList(): Flux의 모든 데이터를 수집하여 List<IndexData>로 만듭니다. 이 연산은 최종 결과를 List로 모읍니다.
        //block(): 비동기 작업을 동기적으로 차단하고 결과를 반환합니다. 즉, 비동기 흐름을 기다려서 결과를 동기적으로 받아옵니다.
        List<IndexData> indicesList = indicesFlux.collectList().block();   // Flux의 모든 데이터를 수집하여 List<IndexData>로 만듭니다. 이 연산은 최종 결과를 List로 모읍니다.
        model.addAttribute("indicesKor", indicesList);  //뷰에 데이터를 전달

        model.addAttribute("jobDate", getJobDateTime());

        return "indices";
    }

    public String getStringToday() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return localDate.format(formatter);
    }

    public String getJobDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public Mono<String> getMajorIndex(String iscd, String fid_cond_mrkt_div_code) {

        if (fid_cond_mrkt_div_code.equals("U")) {
            path = KisConfig.FHKUP03500100_PATH;  // 거래ID
            tr_id = "FHKUP03500100";
        } else {
            path = KisConfig.FHKST03030100_PATH;
            tr_id = "FHKST03030100";
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("fid_cond_mrkt_div_code", fid_cond_mrkt_div_code)
                        .queryParam("fid_input_iscd", iscd)
                        .queryParam("fid_input_date_1", getStringToday())
                        .queryParam("fid_input_date_2", getStringToday())
                        .queryParam("fid_period_div_code", "D")
                        .build())
                .header("content-type","application/json")
                .header("authorization","Bearer " + accessTokenManager.getAccessToken())
                .header("appkey", kisConfig.getAppKey())  // KisConfig의 인스턴스를 통해 값 참조
                .header("appsecret",kisConfig.getAppSecret())
                .header("tr_id",tr_id)
                .retrieve()
                .bodyToMono(String.class);

    }

    @GetMapping("/equities/{id}")
    public Mono<String> CurrentPrice(@PathVariable("id") String id, Model model) {
        String url = KisConfig.REST_BASE_URL + "/uapi/domestic-stock/v1/quotations/inquire-price?fid_cond_mrkt_div_code=J&fid_input_iscd=" + id;

        return webClient.get()
                .uri(url)
                .header("content-type","application/json")
                .header("authorization","Bearer " + accessTokenManager.getAccessToken())
                .header("appkey",kisConfig.getAppKey())
                .header("appsecret",kisConfig.getAppSecret())
                .header("tr_id","FHKST01010100")
                .retrieve()
                .bodyToMono(Body.class)
                .doOnSuccess(body -> {
                    model.addAttribute("equity", body.getOutput());
                    model.addAttribute("jobDate", getJobDateTime());
                })
                .doOnError(result -> System.out.println("*** error: " + result))
                .thenReturn("equities");
    }

}