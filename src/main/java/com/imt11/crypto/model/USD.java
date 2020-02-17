package com.imt11.crypto.model;

import java.math.BigDecimal;

/**
 * @author Dennis Miller
 */
public class USD {

    private BigDecimal price;
    private BigDecimal volume_24h;
    private double percent_change_24h;
    private double percent_change_7d;
    private BigDecimal market_cap;
    private String last_updated;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVolume_24h() {
        return volume_24h;
    }

    public void setVolume_24h(BigDecimal volume_24h) {
        this.volume_24h = volume_24h;
    }

    public double getPercent_change_24h() {
        return percent_change_24h;
    }

    public void setPercent_change_24h(double percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }

    public double getPercent_change_7d() {
        return percent_change_7d;
    }

    public void setPercent_change_7d(double percent_change_7d) {
        this.percent_change_7d = percent_change_7d;
    }

    public BigDecimal getMarket_cap() {
        return market_cap;
    }

    public void setMarket_cap(BigDecimal market_cap) {
        this.market_cap = market_cap;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    @Override
    public String toString() {
        return "USD{" +
                "price=" + price +
                ", volume_24h=" + volume_24h +
                ", percent_change_24h=" + percent_change_24h +
                ", percent_change_7d=" + percent_change_7d +
                ", market_cap=" + market_cap +
                ", last_updated='" + last_updated + '\'' +
                '}';
    }
}
