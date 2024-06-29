package com.utkarsh.stack.book.exception;

public class OperationNotPermittedException extends RuntimeException{

    public OperationNotPermittedException(){}
    public OperationNotPermittedException(String s) {
        super(s);
    }
}
