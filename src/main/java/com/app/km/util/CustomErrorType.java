package com.app.km.util;

/**
 * Created by Kamil-PC on 18.05.2017.
 */
public class CustomErrorType {

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CustomErrorType(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
