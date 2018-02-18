package knbit.emm.controller;

import knbit.emm.service.CourseService;
import knbit.emm.service.MarketService;
import knbit.emm.utilities.TokenUtils;
import knbit.emm.algo.services.AlgorithmSolverService;
import knbit.emm.model.Course;
import knbit.emm.model.Market;
import knbit.emm.model.Market.MarketState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;
    private final AlgorithmSolverService algorithmService;
    private final CourseService courseService;

    private static final Logger log = Logger.getLogger(MarketController.class.getName());

    @Autowired
    public MarketController(AlgorithmSolverService algorithmService, MarketService marketService, CourseService courseService) {
        this.marketService = marketService;
        this.algorithmService = algorithmService;
        this.courseService = courseService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllMarkets(
            @RequestHeader(value = "Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }
        List<Market> markets = marketService.findAllMarkets();

        if (markets == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(marketService.findPossibleForUserMarkets(markets, token), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}/students")
    public ResponseEntity getAllStudents(
            @PathVariable("name") String name,
            @RequestHeader(value = "Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Market market = marketService.findMarket(name);
        return ResponseUtils.getValueOrError(market);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{name}/courses")
    public ResponseEntity getAllCourses(
            @PathVariable("name") String name,
            @RequestHeader(value = "Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Market market = marketService.findMarket(name);
        if (market == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Course> courses = courseService.findCoursesInMarket(name);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}/done_swaps")
    public ResponseEntity getAllDoneSwaps(
            @PathVariable("name") String name,
            @RequestHeader(value = "Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Market market = marketService.findMarket(name);
        if (market == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(marketService.findAllDoneSwaps(market), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public ResponseEntity getMarketInfo(
            @PathVariable("name") String name,
            @RequestHeader(value = "Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Market market = marketService.findMarket(name);
        if (market == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Map<String, Object> map = marketService.getMapWithMarketData(market);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/names")
    public ResponseEntity getAllMarketNames(
            @RequestHeader(value = "Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        return isNotValidToken.orElseGet(() -> new ResponseEntity<>(marketService.findAllMarketNames(), HttpStatus.OK));

    }

    @RequestMapping(method = RequestMethod.POST, value = "/open/{marketName}")
    public ResponseEntity openMarket(@PathVariable String marketName,
                                              @RequestHeader(value = "Authorization") String token) {
        return marketService.changeMarketStateFromTo(marketName, token, MarketState.CLOSED, MarketState.OPEN);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/finish/{marketName}")
    public ResponseEntity finishMarket(@PathVariable String marketName,
                                               @RequestHeader(value = "Authorization") String token) {
        return marketService.changeMarketStateFromTo(marketName, token, MarketState.OPEN, MarketState.FINISHED);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity newMarket(
            @RequestBody Map<String,Object> marketMap,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Market market = marketService.createNewMarket(marketMap, token);
        return new ResponseEntity<>(market, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}/algorithm_accepted_swaps")
    public ResponseEntity algorithmAcceptedSwaps(@PathVariable String name) {
        try {
            Market market = marketService.findMarket(name);
            if (market == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(algorithmService.run(market), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to execute algorithm", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


