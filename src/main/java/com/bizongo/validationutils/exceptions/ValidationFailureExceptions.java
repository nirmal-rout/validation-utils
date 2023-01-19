package com.bizongo.exceptions;

import lombok.*;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ValidationFailureExceptions extends Exception{
    private  BindingResult bindingResult;

    private  List<String> errors =  new ArrayList<>();

    private  String APIName = "API_NAME";

    private  String errorCode = "3004";

    private String error_message = "validation exception";



    public ValidationFailureExceptions(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
    public ValidationFailureExceptions(List<String> errors, String APIName,String errorCode,String error_message){
        this.errors = errors;
        this.APIName = APIName;
        this.errorCode = errorCode;
        this.error_message = error_message;
    }

}
