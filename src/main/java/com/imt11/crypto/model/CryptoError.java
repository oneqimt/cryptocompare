package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class CryptoError {

    private String errorName;
    private int errorId;
    private String errorDescription;

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return "CryptoError{" +
                "errorName='" + errorName + '\'' +
                ", errorId=" + errorId +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
