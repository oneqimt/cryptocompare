package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class Coin {

    private int coin_id;
    private String coin_name;
    private String coin_symbol;

    public Coin() {
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getCoin_symbol() {
        return coin_symbol;
    }

    public void setCoin_symbol(String coin_symbol) {
        this.coin_symbol = coin_symbol;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "coin_id=" + coin_id +
                ", coin_name='" + coin_name + '\'' +
                ", coin_symbol='" + coin_symbol + '\'' +
                '}';
    }
}