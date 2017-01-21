package org.hopto.eriksen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String exceptionText) {
        super(exceptionText);
    }

    public NotFoundException(String exceptionText, Throwable cause) {
        super(exceptionText, cause);
    }
}
