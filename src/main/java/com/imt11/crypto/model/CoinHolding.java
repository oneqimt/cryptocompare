package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class CoinHolding {

    private Coin coin;
    private Holdings holdings;

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public Holdings getHoldings() {
        return holdings;
    }

    public void setHoldings(Holdings holdings) {
        this.holdings = holdings;
    }

    @Override
    public String toString() {
        return "CoinHolding{" +
                "coin=" + coin +
                ", holdings=" + holdings +
                '}';
    }
}
