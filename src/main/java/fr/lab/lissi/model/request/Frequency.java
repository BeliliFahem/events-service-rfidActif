package fr.lab.lissi.model.request;

public class Frequency {
	/**
	 * {@link SubscriptionRequest.Condition}
	 */
	public Condition condition;

	/**
	 * 
	 */
	public float maxThreshold;

	/**
	 * 
	 */
	public float minThreshold;
	
	/**
	 * In milliseconds
	 */
	public int frequencyValue;

	public Frequency() {
	}

	public Frequency(Condition condition, float minThreshold, float maxThreshold , int frequencyValue) {
		this.condition = condition;
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThreshold;
		this.frequencyValue = frequencyValue;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public float getMaxThreshold() {
		return maxThreshold;
	}

	public void setMaxThreshold(float maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	public float getMinThreshold() {
		return minThreshold;
	}

	public void setMinThreshold(float minThreshold) {
		this.minThreshold = minThreshold;
	}

	public enum Condition {
		/**
		 * 
		 */
		EachNewValue,
		/**
		 * 
		 */
		EachNewDifferentValue,
		/**
		 * 
		 */
		IfThresholdIsExceeded;
	}
}
