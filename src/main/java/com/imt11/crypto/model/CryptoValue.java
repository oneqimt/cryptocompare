package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class CryptoValue {

	private String id;
	private String USD;
	private Coin coin;
	private String holdingValue;
	private String percentage;
	private String cost;
	private String increaseDecrease;
	private double quantity;

	public String getIncreaseDecrease() {
		return increaseDecrease;
	}

	public void setIncreaseDecrease(String increaseDecrease) {
		this.increaseDecrease = increaseDecrease;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getHoldingValue() {
		return holdingValue;
	}

	public void setHoldingValue(String holdingValue) {
		this.holdingValue = holdingValue;
	}

	public String getUSD() {
		return USD;
	}

	public void setUSD(String USD) {
		this.USD = USD;
	}

	public Coin getCoin() {
		return coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CryptoValue{" +
				"id=" + id +
				", USD='" + USD + '\'' +
				", coin=" + coin +
				", holdingValue='" + holdingValue + '\'' +
				", percentage='" + percentage + '\'' +
				", cost='" + cost + '\'' +
				", increaseDecrease='" + increaseDecrease + '\'' +
				", quantity=" + quantity +
				'}';
	}
}
