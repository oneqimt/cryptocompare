package com.imt11.crypto.model;

import java.util.List;

/**
 * @author Dennis Miller
 */
public class CoinMarketCapLatest {

    private Status status;
    private List<CoinMarketCapCoin> data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<CoinMarketCapCoin> getData() {
        return data;
    }

    public void setData(List<CoinMarketCapCoin> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CoinMarketCapLatest{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
