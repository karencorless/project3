package com.makers.project3.exception;

import com.makers.project3.Service.UserService;
import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;


//          Handles all generic exceptions
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Model model, Exception ex, HttpServletRequest request) {
        makeModel(model, request);
        model.addAttribute("errorMessage", ex.getMessage());
        System.out.println("Exception Caught: " + ex);
        return "error";
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(Model model, NullPointerException ex, HttpServletRequest request) {
        makeModel(model, request);
        model.addAttribute("errorMessage", "Uh og, that's empty!  " + ex.getMessage());
        System.out.println("Null Pointer Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(Model model, NoResourceFoundException ex, HttpServletRequest request) {
        makeModel(model, request);
        model.addAttribute("errorMessage", "What you seek cannot be found!  " + ex.getMessage());
        System.out.println("No Resource Found Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFoundException(Model model, NoSuchElementException ex, HttpServletRequest request) {
        makeModel(model, request);
        model.addAttribute("errorMessage", "What you seek cannot be found!  " + ex.getMessage());
        System.out.println("No Such Element Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(Model model, IllegalArgumentException ex, HttpServletRequest request) {
        makeModel(model, request);
        model.addAttribute("errorMessage", "That's Illegal!  " + ex.getMessage());
        System.out.println("Illegal Argument Exception: " + ex);
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(Model model, MethodArgumentNotValidException ex, HttpServletRequest request) {
        makeModel(model, request);
        model.addAttribute("errorMessage", "Validation failed: " + ex.getMessage());
        System.out.println("Bad Request Exception: " + ex);
        return "error";
    }


    private void makeModel(Model model, HttpServletRequest request){
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("httpStatusError", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
