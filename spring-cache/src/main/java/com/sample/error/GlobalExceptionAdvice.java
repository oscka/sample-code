package com.sample.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sample.error.exception.CategoryNotFoundException;

/**
 *
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> validationNotValidException(MethodArgumentNotValidException e) {
		return new ResponseEntity<String>(e.getBindingResult().toString(),HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<String> categoryNotFoundException(CategoryNotFoundException e) {
		return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
	}

}
