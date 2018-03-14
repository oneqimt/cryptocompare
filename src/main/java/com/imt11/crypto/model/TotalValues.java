package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class TotalValues {

	private String totalCost;
	private String totalValue;
	private String totalPercentageIncreaseDecrease;
	private String increaseDecrease;

	public String getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

	public String getTotalPercentageIncreaseDecrease() {
		return totalPercentageIncreaseDecrease;
	}

	public void setTotalPercentageIncreaseDecrease(String totalPercentageIncreaseDecrease) {
		this.totalPercentageIncreaseDecrease = totalPercentageIncreaseDecrease;
	}

	public String getIncreaseDecrease() {
		return increaseDecrease;
	}

	public void setIncreaseDecrease(String increaseDecrease) {
		this.increaseDecrease = increaseDecrease;
	}

	@Override
	public String toString() {
		return "TotalValues{" +
				"totalCost='" + totalCost + '\'' +
				", totalValue='" + totalValue + '\'' +
				", totalPercentageIncreaseDecrease='" + totalPercentageIncreaseDecrease + '\'' +
				", increaseDecrease='" + increaseDecrease + '\'' +
				'}';
	}
}
