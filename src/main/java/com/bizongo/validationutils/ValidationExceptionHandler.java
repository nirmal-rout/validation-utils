package com.bizongo.validationutils;

import com.bizongo.exceptions.ValidationFailureExceptions;

import com.bizongo.validationutils.responses.ErrorResponses;
import com.bizongo.validationutils.responses.ValidationFailureResponse;
import com.bizongo.validationutils.utils.AirbrakesNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ValidationExceptionHandler {
    private final AirbrakesNotifier airbrakesNotifier;

    public ValidationExceptionHandler(AirbrakesNotifier airbrakesNotifier) {
        this.airbrakesNotifier = airbrakesNotifier;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationFailureResponse validationError(BindException ex, HttpServletRequest request) {
        logErrorDetails(ex,request);

        ValidationFailureResponse response =  generateValidationResponse(ex.getBindingResult());
        response.setMessage("Please revalidate the API payload and send again");
        response.setError_code("3004");
        return response;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationFailureResponse validationError(IllegalArgumentException ex,HttpServletRequest request) {
        logErrorDetails(ex,request);

        String message = "Please revalidate the API payload and send again for ";
        if(null != ex.getMessage())
            message = message + ex.getMessage();
        ValidationFailureResponse response = new ValidationFailureResponse();
        response.setMessage(message);
        response.setError_code("4004");
        response.setObjectName(ex.getClass().getSimpleName());
        return response;
    }
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationFailureResponse validationError(IllegalStateException ex,HttpServletRequest request) {
        logErrorDetails(ex,request);

        String message = "Please revalidate the API payload and send again for ";
        if(null != ex.getMessage())
            message = message + ex.getMessage();
        ValidationFailureResponse response = new ValidationFailureResponse();
        response.setMessage(message);
        response.setError_code("4004");
        response.setObjectName(ex.getClass().getSimpleName());
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationFailureResponse validationError(MethodArgumentNotValidException ex,HttpServletRequest request) {
        logErrorDetails(ex,request);
        BindingResult result = ex.getBindingResult();

        ValidationFailureResponse response = generateValidationResponse(ex.getBindingResult());
        response.setMessage("Please revalidate the API payload and send again");
        response.setError_code("4004");
        return response;
    }
    @ExceptionHandler(ValidationFailureExceptions.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationFailureResponse validationFailureResponse(ValidationFailureExceptions ex,HttpServletRequest request){
        logErrorDetails(ex,request);
        ValidationFailureResponse response = null;
        if(null != ex.getBindingResult()){
            response = generateValidationResponse(ex.getBindingResult());
            response.setAPI_NAME("API_NAME");
        }else{
            response = new ValidationFailureResponse(ex.getErrors(),ex.getAPIName(),ex.getError_message(),"3004");
        }
        response.setMessage("Please revalidate the API payload and send again");
        response.setError_code("4004");
        return  response;
    }

    private ValidationFailureResponse generateValidationResponse(BindingResult br){
        ValidationFailureResponse response = new ValidationFailureResponse();
        if(null != br){
            List<ErrorResponses> errors = null;
            Map<String,String > errormap = new HashMap<>();
            for (Object object : br.getAllErrors()) {
                if(object instanceof FieldError) {
                    FieldError fieldError = (FieldError) object;
                    errormap.put(fieldError.getDefaultMessage(),fieldError.getCode());
                }

                if(object instanceof ObjectError) {
                    ObjectError objectError = (ObjectError) object;
                    errormap.put(objectError.getDefaultMessage(),objectError.getCode());
                }
            }
           errors = errormap.entrySet().stream()
                    .map( entry -> new ErrorResponses(entry.getValue(), entry.getKey())).collect(Collectors.toList());
            response.setErrorList(errors);
            response.setMessage("Please revalidate the API payload and send again");
            response.setError_code("4004");
            return response;
        }
        return  response;
    }

    private void logErrorDetails(Exception ex, HttpServletRequest request) {
        log.error("error: \n", ex);
        airbrakesNotifier.notify(ex, request,null);
    }
}
