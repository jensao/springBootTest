package org.hopto.eriksen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jens on 2016-10-04.
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {
    public BadRequestException(String exceptionText) {
        super(exceptionText);
    }
}
