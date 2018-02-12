package knbit.emm.controller;

import knbit.emm.service.TokenService;
import knbit.emm.utilities.TokenProvider;
import knbit.emm.utilities.TokenUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/token")
public class TokenController {

    private static Logger log = Logger.getLogger(SwapController.class.getName());
    private static final long expiresIn = 3600000;
    private final TokenProvider tokenProvider;
    private Environment environment;
    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService, Environment environment, TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
        this.environment = environment;
        this.tokenService = tokenService;
    }

    private static class TokenJSONResponse {
        public String access_token;
        public String token_type;
        public int expires_in;
        public String scope;
        public int created_at;

        public TokenJSONResponse() {}
    }

    private static class TranscriptNumberJSONResponse {
        public String transcript_number;
        public TranscriptNumberJSONResponse() {}
    }

    private static class ExtendedJSONResponse {
        public String first_name;
        public String last_name;
        public ExtendedJSONResponse() {}
    }


    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/debugLogin")
    public ResponseEntity<Map<String, Object>> debugLogin(@RequestBody Map<String, Object> paramsMap) throws IOException {

        if(!environment.acceptsProfiles("dev", "integ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String transcriptNumber = paramsMap.get("transcriptNumber").toString();
        Map<String, Object> responseMap = new HashMap<>();

        String refreshJws = TokenUtils.buildRefreshJWS(transcriptNumber);
        String accessJws = TokenUtils.buildAccessJWS(transcriptNumber, expiresIn);

        try {
            tokenProvider.addToken(refreshJws, transcriptNumber);
        } catch (NullPointerException|ClassCastException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        responseMap.put("expiresIn", expiresIn);
        responseMap.put("accessToken", accessJws);
        responseMap.put("refreshToken", refreshJws);
        responseMap.put("transcript_number", transcriptNumber);
        responseMap.put("first_name", "Name");
        responseMap.put("last_name", "Surname");
        responseMap.put("role", TokenUtils.getUserRole(transcriptNumber));
        return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> paramsMap) throws IOException {

        String code = paramsMap.get("code").toString();

        String TOKEN_ENDPOINT = "YOUR ENDPOINT";
        String DATA_ENDPOINT_IND = "YOUR ENDPOINT";
        String DATA_ENDPOINT_EXT = "YOUR ENDPOINT";
        String DATA_ENDPOINT_PUB = "YOUR ENDPOINT";

        RestTemplate rest = tokenService.restTemplateInit();
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, String> params = tokenService.buildOAuthQueryParams(code);
        HttpHeaders headers = tokenService.buildOAuthQueryHeaders();
        HttpEntity<?> request = new HttpEntity<>(params, headers);

        try {
            TokenJSONResponse tokenResponse = rest.postForObject(TOKEN_ENDPOINT, request, TokenJSONResponse.class);
            request = new HttpEntity<>(headers);

            HttpEntity<TranscriptNumberJSONResponse> transcriptNumberResponse =
                    rest.exchange(DATA_ENDPOINT_IND+"?access_token="+tokenResponse.access_token, HttpMethod.GET, request, TranscriptNumberJSONResponse.class);
            HttpEntity<ExtendedJSONResponse> extendedResponse =
                    rest.exchange(DATA_ENDPOINT_EXT+"?access_token="+tokenResponse.access_token, HttpMethod.GET, request, ExtendedJSONResponse.class);

            // save token
            String transcriptNumber = transcriptNumberResponse.getBody().transcript_number;
            String refreshJws = TokenUtils.buildRefreshJWS(transcriptNumber);
            String accessJws = TokenUtils.buildAccessJWS(transcriptNumber, expiresIn);

            try {
                tokenProvider.addToken(refreshJws, transcriptNumber);
            } catch (NullPointerException|ClassCastException e) {
                log.error(e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            responseMap.put("expiresIn", expiresIn);
            responseMap.put("accessToken", accessJws);
            responseMap.put("refreshToken", refreshJws);
            responseMap.put("transcript_number", transcriptNumber);
            responseMap.put("first_name", extendedResponse.getBody().first_name);
            responseMap.put("last_name", extendedResponse.getBody().last_name);
            responseMap.put("role", TokenUtils.getUserRole(transcriptNumber));
            return new ResponseEntity<>(responseMap, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String,String> refreshTokenMap){
        String refreshToken = refreshTokenMap.get("refreshToken");

        tokenProvider.removeByToken(refreshToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/refresh")
    public ResponseEntity<Map<String, Object>> grantNewAccessToken(@RequestBody Map<String,String> refreshTokenMap){
        String refreshToken = refreshTokenMap.get("refreshToken");
        String studentId = tokenProvider.getStudentId(refreshToken);
        Map<String, Object> responseMap = new HashMap<>();

        if(!tokenProvider.exists(refreshToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String refreshJws = TokenUtils.buildRefreshJWS(studentId);
        String accessJws = TokenUtils.buildAccessJWS(studentId, expiresIn);

        tokenProvider.removeByToken(refreshToken);
        tokenProvider.addToken(refreshJws, studentId);

        responseMap.put("expiresIn", expiresIn);
        responseMap.put("accessToken", accessJws);
        responseMap.put("refreshToken", refreshJws);
        return new ResponseEntity<>(responseMap,HttpStatus.CREATED);
    }


}

