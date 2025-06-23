package com.makers.project3.exception;

import com.makers.project3.Service.UserService;
import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;


    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleConflict() {
        // Nothing to do
    }


    //          Handles all generic exceptions
    @ExceptionHandler(Exception.class)
    public String genericExceptionHandler(Model model, Exception exception, HttpServletRequest request) throws Exception {
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);

        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.

        if (AnnotationUtils.findAnnotation
                (exception.getClass(), ResponseStatus.class) != null)
            throw exception;
        // Otherwise setup and send the user to a default error-view.
        makeModel(model);
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("errorMessage", exception.getMessage());
        System.out.println("Exception Caught: " + exception);
        return "error";
    }

    @ExceptionHandler(NullPointerException.class)
    public String nullPointerExceptionHandler(Model model, NullPointerException ex) {
        makeModel(model);
        model.addAttribute("errorMessage", "Uh oh, that's empty!  " + ex.getMessage());
        System.out.println("Null Pointer Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(Model model, NoResourceFoundException ex) {
        makeModel(model);
        model.addAttribute("errorMessage", "What you seek cannot be found!  " + ex.getMessage());
        System.out.println("No Resource Found Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFoundException(Model model, NoSuchElementException ex) {
        makeModel(model);
        model.addAttribute("errorMessage", "What you seek cannot be found!  " + ex.getMessage());
        System.out.println("No Such Element Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(Model model, IllegalArgumentException ex) {
        makeModel(model);
        model.addAttribute("errorMessage", "That's Illegal!  " + ex.getMessage());
        System.out.println("Illegal Argument Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(Model model, MethodArgumentNotValidException ex) {
        makeModel(model);
        model.addAttribute("errorMessage", "Validation failed: " + ex.getMessage());
        System.out.println("Bad Request Exception: " + ex);
        return "error";
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public String handleDatabaseError(Model model, MethodArgumentNotValidException ex) {
        makeModel(model);
        model.addAttribute("errorMessage", "Database Error:: " + ex.getMessage());
        System.out.println("Database Error : " + ex);
        return "error";
    }

//    @ExceptionHandler(NoSuchDeckExistsException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public String handleNoSuchDeckExistsException(Model model, NoSuchDeckExistsException exception){
//        makeModel(model);
//        model.addAttribute("errorMessage", exception.getMessage());
//        System.out.println("Database Error : " + exception);
//        return "error";
//    }

    @ExceptionHandler(NoSuchEntityExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoSuchEntityExistsException(Model model, NoSuchEntityExistsException exception){
        makeModel(model);
        model.addAttribute("errorMessage", "Database Error:: " + exception.getMessage());
        System.out.println("Database Error : " + exception);
        return "error";
    }

//    private void makeModelAndView(ModelAndView modelAndView, HttpServletRequest request){
//        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
//        modelAndView.addObject("currentUser", currentUser);
//        modelAndView.addObject("requestUri", request.getRequestURI());
//        modelAndView.addObject("httpStatusError", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    private void makeModel(Model model) {
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("httpStatusError", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
