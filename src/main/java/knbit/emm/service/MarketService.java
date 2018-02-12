package knbit.emm.service;

import knbit.emm.model.Course;
import knbit.emm.model.Market;
import knbit.emm.model.Swap;
import knbit.emm.parser.EnrollOutputParser;
import knbit.emm.parser.ParsingTermsAlgorithmFromJSONString;
import knbit.emm.parser.ParsingTimetablesAlgorithmFromJSONString;
import knbit.emm.repository.MarketRepository;
import knbit.emm.utilities.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MarketService {
    private final MarketPermissionService marketPermissionService;
    private final EnrollOutputParser parser;
    private final MarketRepository marketRepository;

    @Autowired
    public MarketService(MarketPermissionService marketPermissionService, EnrollOutputParser parser, MarketRepository marketRepository) {
        this.marketPermissionService = marketPermissionService;
        this.parser = parser;
        this.marketRepository = marketRepository;
    }

    public List<Market> findAllMarkets() {
        return marketRepository.findAll();
    }

    public Market findMarket(String name) {
        return marketRepository.findOne(name);
    }

    public Market save(Market market) {
        return marketRepository.save(market);
    }

    public Market createNewMarket(Map<String,Object> marketMap, String token) throws IllegalStateException {
        Market market = provideNewMarketFromMap(marketMap, token);
        if(findMarket(market.getName()) != null) {
            throw new IllegalStateException();
        }
        market = save(market);
        runParser(market, marketMap);
        return save(market);
    }

    Market provideNewMarketFromMap(Map<String, Object> marketMap, String token) throws IllegalStateException {
        String name = marketMap.get("name").toString();
        long begDate = Long.valueOf(marketMap.get("beg").toString());
        long endDate = Long.valueOf(marketMap.get("end").toString());
        if (begDate > 0 && endDate > 0 && begDate >= endDate) {
            throw new IllegalStateException();
        }
        String createdBy = TokenUtils.getStudentId(token);
        return new Market(name, begDate, endDate, createdBy);
    }

    private void runParser(Market market, Map<String, Object> marketMap) {
        String terms = marketMap.get("termFile").toString();
        String timetable = marketMap.get("planFile").toString();
        @SuppressWarnings("unchecked")
        ArrayList<String> activeCourses = (ArrayList) marketMap.get("subjects");
        ParsingTermsAlgorithmFromJSONString termsAlgorithm = new ParsingTermsAlgorithmFromJSONString(terms);
        ParsingTimetablesAlgorithmFromJSONString timetableAlgorithm = new ParsingTimetablesAlgorithmFromJSONString(timetable);
        parser.setTermsAlgorithm(termsAlgorithm);
        parser.setTimetableAlgorithm(timetableAlgorithm);
        parser.parse(market, activeCourses);
    }

    public Map<String, Object> getMapWithMarketData(Market market) {
        Map<String, Object> map = new TreeMap<>();
        List<Map<String, Object>> subjects = new ArrayList<>();
        market.getCourses().forEach(e -> subjects.add(gainInfoOfMarketCourses(e)));
        List<Swap> doneSwaps = marketRepository.findAllDoneSwapsForMarkets(market.getName());
        map.put("name", market.getName());
        map.put("beg", market.getProperBegTime());
        map.put("end", market.getProperEndTime());
        map.put("state", market.getState().toString());
        map.put("subjects", subjects);
        map.put("done_swaps", doneSwaps);
        map.put("state", market.getState());
        return map;
    }

    private Map<String, Object> gainInfoOfMarketCourses(Course e) {
        Map<String, Object> map = new TreeMap<>();
        map.put("name", e.getTitle());
        map.put("enabled", e.isOpenForChanges());
        return map;
    }

    public ResponseEntity changeMarketStateFromTo(String marketName, String token, Market.MarketState currentMarketState, Market.MarketState newMarketState) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }
        Market market = marketRepository.findOne(marketName);
        String userId = TokenUtils.getStudentId(token);

        if (!marketPermissionService.hasUserPermissionToSeeOrEditMarket(market, userId) && market.getState() != currentMarketState) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        market.setState(newMarketState);
        handleMarketTime(market, newMarketState);
        marketRepository.save(market);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void handleMarketTime (Market market, Market.MarketState marketState){
        if (marketState == Market.MarketState.OPEN) {
            market.setBeginningTime(new Timestamp(System.currentTimeMillis()));
        } else if (marketState == Market.MarketState.FINISHED) {
            market.setEndTime(new Timestamp(System.currentTimeMillis()));
        }

    }

    public List<Market> findPossibleForUserMarkets(List<Market> markets, String token) {
        String userId = TokenUtils.getStudentId(token);
        return markets.stream()
                .filter(market -> marketPermissionService.hasUserPermissionToSeeOrEditMarket(market, userId))
                .collect(Collectors.toList());
    }

    public List<Swap> findAllDoneSwaps(Market market) {
       return market.getCourses()
                .stream()
                .flatMap(c -> c.getClasses().stream())
                .flatMap(c -> c.getSwapsGive().stream())
                .collect(Collectors.toList());
    }

    public List<String> findAllMarketNames() {
        return findAllMarkets().stream()
                .map(Market::getName)
                .collect(Collectors.toList());
    }


}
