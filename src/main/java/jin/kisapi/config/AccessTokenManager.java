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


    public String generateAccessToken() {
        String url = KisConfig.REST_BASE_URL + "/oauth2/tokenP";
        OauthInfo bodyOauthInfo = new OauthInfo();
        bodyOauthInfo.setGrant_type("client_credentials");
        bodyOauthInfo.setAppkey(kisConfig.getAppKey());
        bodyOauthInfo.setAppsecret(kisConfig.getAppSecret());

        Mono<TokenInfo> mono = webClient.post()
                .uri(url)
                .header("content-type", "application/json")
                .bodyValue(bodyOauthInfo)
                .retrieve()
                .bodyToMono(TokenInfo.class);

        TokenInfo tokenInfo = mono.block();
        if (tokenInfo == null) {
            throw new RuntimeException("액세스 토큰을 가져올 수 없습니다.");
        }

        ACCESS_TOKEN = tokenInfo.getAccess_token();

        return ACCESS_TOKEN;
    }

}
