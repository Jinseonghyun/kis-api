package jin.kisapi.config;

import jin.kisapi.model.OauthInfo;
import jin.kisapi.model.TokenInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccessTokenManager {
    private final WebClient webClient;
    public static String ACCESS_TOKEN;
    public static long last_auth_time = 0;
    private final KisConfig kisConfig;

    public AccessTokenManager(WebClient.Builder webClientBuilder, KisConfig kisConfig) {
        this.webClient = webClientBuilder.baseUrl(KisConfig.REST_BASE_URL).build();
        this.kisConfig = kisConfig;
    }

    public String getAccessToken() {
        if (ACCESS_TOKEN == null) {
            ACCESS_TOKEN = generateAccessToken();
            System.out.println("generate ACCESS_TOKEN: " + ACCESS_TOKEN);
        }

        return ACCESS_TOKEN;
    }

    // Mono<TokenInfo>는 비동기적으로 HTTP POST 요청을 보내고, 응답을 TokenInfo 객체로 변환
    public String generateAccessToken() {
        String url = KisConfig.REST_BASE_URL + "/oauth2/tokenP";
        OauthInfo bodyOauthInfo = new OauthInfo();
        bodyOauthInfo.setGrant_type("client_credentials");
        bodyOauthInfo.setAppkey(kisConfig.getAppKey());
        bodyOauthInfo.setAppsecret(kisConfig.getAppSecret());

        // Mono는 비동기적으로 데이터를 처리하고, 0개 또는 1개의 결과를 반환하는 리액티브 타입
        Mono<TokenInfo> mono = webClient.post()
                .uri(url)
                .header("content-type", "application/json")
                .bodyValue(bodyOauthInfo)
                .retrieve() // 응답을 비동기적으로 가져오는 메서드
                .bodyToMono(TokenInfo.class);

        TokenInfo tokenInfo = mono.block(); // mono.block()는 Mono의 결과가 준비될 때까지 블로킹(대기)하고, 결과를 반환
        if (tokenInfo == null) {
            throw new RuntimeException("액세스 토큰을 가져올 수 없습니다.");
        }

        ACCESS_TOKEN = tokenInfo.getAccess_token();

        return ACCESS_TOKEN;
    }

}
