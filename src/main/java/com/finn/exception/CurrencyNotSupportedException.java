package com.finn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Currency Not Supported")
public class CurrencyNotSupportedException extends RuntimeException{
}
