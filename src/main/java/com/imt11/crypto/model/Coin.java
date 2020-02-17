package com.imt11.crypto.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Dennis Miller
 */
public class Coin {

    private int coin_id;
    private String coin_name;
    private String coin_symbol;
    private int cmc_id;
    private String slug;
    private BigDecimal market_cap;

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

    public int getCmc_id() {
        return cmc_id;
    }

    public void setCmc_id(int cmc_id) {
        this.cmc_id = cmc_id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BigDecimal getMarket_cap() {
        return market_cap;
    }

    public void setMarket_cap(BigDecimal market_cap) {
        this.market_cap = market_cap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return coin_id == coin.coin_id &&
                cmc_id == coin.cmc_id &&
                Objects.equals(coin_name, coin.coin_name) &&
                Objects.equals(coin_symbol, coin.coin_symbol) &&
                Objects.equals(slug, coin.slug) &&
                Objects.equals(market_cap, coin.market_cap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coin_id, coin_name, coin_symbol, cmc_id, slug, market_cap);
    }

    @Override
    public String toString() {
        return "Coin{" +
                "coin_id=" + coin_id +
                ", coin_name='" + coin_name + '\'' +
                ", coin_symbol='" + coin_symbol + '\'' +
                ", cmc_id=" + cmc_id +
                ", slug='" + slug + '\'' +
                ", market_cap=" + market_cap +
                '}';
    }
}
