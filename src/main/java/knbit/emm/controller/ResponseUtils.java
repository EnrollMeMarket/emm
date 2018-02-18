package knbit.emm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

final class ResponseUtils {
    private ResponseUtils() {
    }

    static <X> ResponseEntity getCreatedOrError(X maybeResponse) {
        return handleRequest(maybeResponse, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.CREATED);
    }

    static <X> ResponseEntity getValueOrBadRequest(X maybeResponse) {
        return handleRequest(maybeResponse, HttpStatus.BAD_REQUEST, HttpStatus.OK);
    }

    static <X> ResponseEntity getValueOrNotFound(X maybeResponse) {
        return handleRequest(maybeResponse, HttpStatus.NOT_FOUND, HttpStatus.OK);
    }

    static <X> ResponseEntity getValueOrError(X maybeResponse) {
        return handleRequest(maybeResponse, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.OK);
    }

    private static <X> ResponseEntity handleRequest(X maybeResponse, HttpStatus errorStatus, HttpStatus okStatus) {
        if (maybeResponse == null) {
            return new ResponseEntity(errorStatus);
        }
        return new ResponseEntity<>(maybeResponse, okStatus);
    }
}