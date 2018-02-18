package knbit.emm.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity handleNumberFormatException(HttpServletRequest request, Exception ex){
        log.error("Exception: "+ ex.toString());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
