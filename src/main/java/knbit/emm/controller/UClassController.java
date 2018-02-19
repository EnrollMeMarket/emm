package knbit.emm.controller;

import knbit.emm.service.UClassService;
import knbit.emm.utilities.TokenUtils;
import knbit.emm.model.UClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/class")
public class UClassController {

    private final UClassService uClassService;

    @Autowired
    public UClassController(UClassService uClassService) {
        this.uClassService = uClassService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{classId}")
    public ResponseEntity getClassDetails(
            @PathVariable("classId") int classId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        UClass uclass = uClassService.findClass(classId);
        return ResponseUtils.getValueOrNotFound(uclass);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllCourses(
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<UClass> classes = uClassService.findAllClass();
        return ResponseUtils.getValueOrNotFound(classes);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{classId}/swapsGive")
    public ResponseEntity getSwapsGive(
            @PathVariable("classId") int classId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        UClass uclass = uClassService.findClass(classId);
        return ResponseUtils.getValueOrNotFound(uclass);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{classId}/swapsTake")
    public ResponseEntity getSwapsTake(
            @PathVariable("classId") int classId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        UClass uclass = uClassService.findClass(classId);
        return ResponseUtils.getValueOrNotFound(uclass);
    }

}


