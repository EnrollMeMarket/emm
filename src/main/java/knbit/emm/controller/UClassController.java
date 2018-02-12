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
@RequestMapping(path = "/class")
public class UClassController {

    private final UClassService uClassService;

    @Autowired
    public UClassController(UClassService uClassService) {
        this.uClassService = uClassService;
    }

    @CrossOrigin
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
        if (uclass == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(uclass, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllCourses(
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<UClass> classes = uClassService.findAllClass();
        if (classes == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @CrossOrigin
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
        if (uclass == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(uclass.getSwapsGive(), HttpStatus.OK);
    }

    @CrossOrigin
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
        if (uclass == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(uclass.getSwapsTake(), HttpStatus.OK);
    }

}


