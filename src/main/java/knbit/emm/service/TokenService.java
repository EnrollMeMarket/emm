package knbit.emm.service;


import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    public Map<String, String> buildOAuthQueryParams(String code) {
        String CLIENT_ID = "";
        String CLIENT_SECRET = "";
        try {
            CLIENT_ID = new String(Files.readAllBytes(Paths.get("client_id.txt"))).trim();
            CLIENT_SECRET = new String(Files.readAllBytes(Paths.get("client_secret.txt"))).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String REDIRECT_URL = "YOUR REDIRECT URL";

        Map<String, String> map = new LinkedHashMap<>();
        map.put("client_id", CLIENT_ID);
        map.put("client_secret", CLIENT_SECRET);
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", REDIRECT_URL);

        return map;
    }

    public HttpHeaders buildOAuthQueryHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "Java1.8");
        return headers;
    }

    public RestTemplate restTemplateInit() {
        RestTemplate rest = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        rest.setMessageConverters(messageConverters);
        return rest;
    }
}
