package fr.lab.lissi.model;

public class DataElement {

	private String type;
	private String unit;
	private String name;
	private String value;

	public DataElement() {
	}

	public DataElement(String type, String unit, String name, String value) {
		this.type = type;
		this.unit = unit;
		this.name = name;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}