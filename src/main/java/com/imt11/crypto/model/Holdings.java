package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class Holdings {

    private int holding_id;
    private int coin_id;
    private double quantity;
    private double cost;
    private int person_id;

    public Holdings() {
    }

    public int getHolding_id() {
        return holding_id;
    }

    public void setHolding_id(int holding_id) {
        this.holding_id = holding_id;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    @Override
    public String toString() {
        return "Holdings{" +
                "holding_id=" + holding_id +
                ", coin_id=" + coin_id +
                ", quantity=" + quantity +
                ", cost=" + cost +
                ", person_id=" + person_id +
                '}';
    }
}
