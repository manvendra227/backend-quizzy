package com.example.demo.Exceptions;

public class QuizNotFoundException extends Exception{
    public QuizNotFoundException(Throwable cause) {
        super(cause);
    }

    protected QuizNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public QuizNotFoundException() {
        super();
    }

    public QuizNotFoundException(String message) {
        super(message);
    }

    public QuizNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
