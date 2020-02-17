package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class Quote {

    private USD USD;

    public com.imt11.crypto.model.USD getUSD() {
        return USD;
    }

    public void setUSD(com.imt11.crypto.model.USD USD) {
        this.USD = USD;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "USD=" + USD +
                '}';
    }
}
