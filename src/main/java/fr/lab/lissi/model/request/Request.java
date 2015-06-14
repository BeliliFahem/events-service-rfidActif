package fr.lab.lissi.model.request;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import fr.lab.lissi.model.DataElement;
import fr.lab.lissi.model.device.rfid.UniqueDataIdentifier;
import fr.lab.lissi.model.request.Contact.Protocol;


/**
 * 
 * @author Fahem
 *
 */
public class Request {

	/**
	 * 
	 */
	@SerializedName("method")
	protected Method method;

	/**
	 * 
	 */
	protected Contact contact;

	/**
	 * 	
	 */
	protected UniqueDataIdentifier uniqueDataIdentifier;

	/**
	 * 
	 */
	protected String deviceId;

	/**
	 * sourceAdress is used to new the source address mail
	 */
	protected transient String sourceAddress;


	public Request() {
	}


	public Method getMethod() {
		return method;
	}


	public void setMethod(Method method) {
		this.method = method;
	}


	public Contact getContact() {
		return contact;
	}


	public void setContact(Contact contact) {
		this.contact = contact;
	}


	public String getSourceAddress() {
		return sourceAddress;
	}


	public void setSourceAddress(String sourceAdress) {
		this.sourceAddress = sourceAdress;
	}


	public UniqueDataIdentifier getUniqueDataIdentifier() {
		return uniqueDataIdentifier;
	}


	public void setUniqueDataIdentifier(UniqueDataIdentifier uniqueDataIdentifier) {
		this.uniqueDataIdentifier = uniqueDataIdentifier;
	}


	public final String getUniqueRequestId() {
		return uniqueDataIdentifier + " " + deviceId + " " + sourceAddress + " " + contact.address;
	}
	
	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	@Override
	public String toString() {
		// TODO verify if there is empty or null field after generating JSON format
		return new Gson().toJson(this);
	}


	public boolean isValid() {
		/*
		 * TODO verify if there is coherence between contact.protocol and contact.adress.
		 * XMPP<---> valid address mail
		 * HTTP<---> valid URL
		 * Verify integers ...
		 */
		if (!this.contact.address.isEmpty()
				&& this.contact.address != null
				&& (this.contact.protocol instanceof Protocol)
				&& (this.contact.responseVaribaleName != null)
				&& this.method instanceof Method
				&& this.uniqueDataIdentifier instanceof UniqueDataIdentifier
				&& this.deviceId != null && !this.deviceId.isEmpty()) {
			return true;
		}
		return false;
	}

}
