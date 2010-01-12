package de.schnocklake.demo.android.sapclient2.data;

public class Customer {
	private String name;
	private String number;
	private String city;
	private String street;
	
	public Customer(String name, String number, String city, String street) {
		super();
		this.name = name;
		this.number = number;
		this.city = city;
		this.street = street;
	}

	
	public String getName() {
		return name;
	}
	public String getNumber() {
		return number;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getName();
	}

	public String getCity() {
		return city;
	}

	public String getStreet() {
		return street;
	}
	
	
}
