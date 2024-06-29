package com.utkarsh.stack.book.handler;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
@Setter
public class ExceptionResponse {

    private Integer buisnessErrorCode;
    private String buisnessErrorDescription;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
