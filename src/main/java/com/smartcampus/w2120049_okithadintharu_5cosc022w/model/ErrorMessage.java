package com.smartcampus.w2120049_okithadintharu_5cosc022w.model;

public class ErrorMessage {

    private int errorCode;

    private String errorMessage;

    private String documentation;

    public ErrorMessage() {
    }

    public ErrorMessage(int errorCode, String errorMessage, String documentation) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.documentation = documentation;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    @Override
    public String toString() {
        return "ErrorMessage{"
                + "errorCode=" + errorCode
                + ", errorMessage='" + errorMessage + '\''
                + ", documentation='" + documentation + '\''
                + '}';
    }

}
