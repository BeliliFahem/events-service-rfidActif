package fr.lab.lissi.model.request;

public class Contact {
	/**
	 * 
	 */
	public Protocol protocol;

	/**
	 * 
	 */
	public String address;

	public String responseVaribaleName;

	public Contact(Protocol protocol, String adress, String responseVaribaleName) {
		super();
		this.protocol = protocol;
		this.address = adress;
		this.responseVaribaleName = responseVaribaleName;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getResponseVaribaleName() {
		return responseVaribaleName;
	}

	public void setResponseVaribaleName(String responseVaribaleName) {
		this.responseVaribaleName = responseVaribaleName;
	}

	public enum Protocol {
		HTTP, XMPP;
	}

}
