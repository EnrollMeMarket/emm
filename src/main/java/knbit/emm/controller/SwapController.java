package knbit.emm.controller;

import knbit.emm.model.dto.SwapSummary;
import knbit.emm.service.SwapService;
import knbit.emm.utilities.TokenUtils;
import knbit.emm.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/swap")
public class SwapController {

    private static Logger log = Logger.getLogger(SwapController.class.getName());
    private final SwapService swapService;

    @Autowired
    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @RequestMapping(method = RequestMethod.GET, value="/{swapId}")
    public ResponseEntity getSwapDetails(
            @PathVariable("swapId") int swapId,
            @RequestHeader(value="Authorization") String token
    ){
        Swap swap = swapService.findSwap(swapId);
        if(swap == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, swap.getStudentsId());
        return isNotValidToken.orElseGet(() -> new ResponseEntity<>(swap, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllSwaps(
            @RequestHeader(value="Authorization") String token
    ){
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<Swap> swaps = swapService.findAllSwaps();
        return ResponseUtils.getValueOrNotFound(swaps);
    }

    @RequestMapping(method = RequestMethod.GET, value="/done")
    public ResponseEntity getAllDoneSwaps(
            @RequestHeader(value="Authorization") String token
    ){
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<Swap> swaps = swapService.getAllDone();
        return ResponseUtils.getValueOrNotFound(swaps);
    }

    @RequestMapping(method = RequestMethod.DELETE, value="/{swapId}")
    public ResponseEntity deleteSwap(
            @PathVariable("swapId") int swapId,
            @RequestHeader(value="Authorization") String token
    ) {
        Swap swap = swapService.findSwap(swapId);
        if(swap == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, swap.getStudentsId());
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        swapService.deleteSwap(swap);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/many")
    public ResponseEntity deleteManySwaps(
            @RequestBody List<Swap> swapList,
            @RequestHeader(value="Authorization") String token
    ) {
        for (Swap swap : swapList) {
            Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, swap.getStudentsId());
            if (isNotValidToken.isPresent()) {
                return isNotValidToken.get();
            }
        }
        SwapSummary swapSummary = swapService.deleteManySwapsAndCreateResponse(swapList);
        return new ResponseEntity<>(swapSummary, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{swapId}/is_ready")
    public ResponseEntity getIfSwapIsReady(
            @PathVariable("swapId") int swapId,
            @RequestHeader(value="Authorization") String token
    ){
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Swap swap = swapService.findSwap(swapId);
        return ResponseUtils.getValueOrNotFound(swap);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/many")
    public ResponseEntity postSwaps(
            @RequestBody List<Swap> swapList,
            @RequestHeader(value="Authorization") String token
    ){
        for(Swap swap: swapList){
            Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, swap.getStudentsId());
            if(isNotValidToken.isPresent()){
                return isNotValidToken.get();
            }
        }
        SwapSummary swapSummary = swapService.handleManySwapsAndCreateResponse(swapList);
        return new ResponseEntity<>(swapSummary, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postSwap(
            @RequestBody Swap postedSwap,
            @RequestHeader(value="Authorization") String token
    ){
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, postedSwap.getStudentsId());
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }
        Swap swap = swapService.handleSingleSwap(postedSwap).orElse(null);
        return ResponseUtils.getValueOrNotFound(swap);
    }
}
