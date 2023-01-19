package com.bizongo.validationutils.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationFailureResponse {
    private String message;
    private String error_code;
    private String API_NAME;
    private List<?> errorList;

    private String objectName;

    private FieldError[] methodErrors;
    public ValidationFailureResponse(List<?> errorList) {
        this.errorList = errorList;
    }
    public ValidationFailureResponse(FieldError[] errors){
        this.methodErrors = errors;
    }

    public ValidationFailureResponse(List<?> errors,String API_NAME,String message,String error_code){
        this.errorList=errors;
        this.API_NAME = API_NAME;
        this.error_code = error_code;
        this.message = message;
    }

}
