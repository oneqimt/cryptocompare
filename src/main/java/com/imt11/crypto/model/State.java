package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class State {

	private int id;
	private String name;
	private String country;
	private String abbreviation;

	public State(){

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@Override
	public String toString() {
		return "State{" +
				"id=" + id +
				", name='" + name + '\'' +
				", country='" + country + '\'' +
				", abbreviation='" + abbreviation + '\'' +
				'}';
	}
}
