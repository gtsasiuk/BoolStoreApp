package com.epam.rd.autocode.spring.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleCustomNotFound() {
        return build404();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFound() {
        return build404();
    }

    private ModelAndView build404() {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("errorTitle", "error.404.title");
        mav.addObject("errorMessage", "error.404.message");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex) {
        ModelAndView mav = new ModelAndView("errors/500");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("errorTitle", "error.500.title");
        mav.addObject("errorMessage", "error.500.message");
        return mav;
    }
}

