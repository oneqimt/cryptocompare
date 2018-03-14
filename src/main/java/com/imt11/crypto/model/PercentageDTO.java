package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class PercentageDTO {

	private String valueString;
	private double valueDouble;

	public String getValueString() {
		return valueString;
	}

	public double getValueDouble() {
		return valueDouble;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	public void setValueDouble(double valueDouble) {
		this.valueDouble = valueDouble;
	}

	@Override
	public String toString() {
		return "PercentageDTO{" +
				       "valueString='" + valueString + '\'' +
				       ", valueDouble=" + valueDouble +
				       '}';
	}

}
