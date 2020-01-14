package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class TotalValues {

	private int personId;
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

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	@Override
	public String toString() {
		return "TotalValues{" +
				"personId=" + personId +
				", totalCost='" + totalCost + '\'' +
				", totalValue='" + totalValue + '\'' +
				", totalPercentageIncreaseDecrease='" + totalPercentageIncreaseDecrease + '\'' +
				", increaseDecrease='" + increaseDecrease + '\'' +
				'}';
	}
}
