package fr.lab.lissi.model.request;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import fr.lab.lissi.model.DataElement;
import fr.lab.lissi.model.request.Frequency.Condition;


/**
 * 
 * @author Belili Fahem
 *
 */
public class SubscriptionRequest extends Request {

	/**
	 * 
	 */
	private Frequency frequency;

	/**
	 * This field is useful to new the last sent data list
	 */
	private transient List<DataElement> dataList;


	public SubscriptionRequest() {
		super();
		dataList = new ArrayList<DataElement>();

	}


	public Frequency getFrequency() {
		return frequency;
	}


	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}


	public boolean isValid() {
		/*
		 * TODO verify if there is coherence between contact.protocol and contact.adress.
		 * XMPP<---> valid address mail
		 * HTTP<---> valid URL
		 * Verify integers ...
		 */
		if (super.isValid() 
				&& (this.frequency.condition instanceof Condition)
				&& this.frequency.minThreshold <= this.frequency.maxThreshold
				&& this.frequency.frequencyValue >= 1000
				) {
			return true;
		}
		return false;
	}


	public List<DataElement> getDataList() {
		return dataList;
	}


	public void setDataList(List<DataElement> dataList) {
		this.dataList = dataList;
	}


	@Override
	public String toString() {
		// TODO verify if there is empty or null field after generating JSON format
		return new Gson().toJson(this);
	}


	@Override
	public boolean equals(Object obj) {
		SubscriptionRequest r = (SubscriptionRequest) obj;
		if (!this.contact.address.equals(r.contact.address) ||
				!this.sourceAddress.equals(r.sourceAddress) ||
				!(this.uniqueDataIdentifier == r.uniqueDataIdentifier) ||
				!this.getDeviceId().equals(r.getDeviceId()))
			return false;
		return true;
	}


	@Override
	public int hashCode() {
		int hash = 89;
		hash = hash + getUniqueRequestId().length() + getUniqueRequestId().indexOf("@")
				+ getUniqueRequestId().indexOf(".");
		hash = hash + this.getUniqueRequestId().hashCode();
		return hash;
	}
}
