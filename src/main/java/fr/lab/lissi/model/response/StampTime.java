package fr.lab.lissi.model.response;

import java.util.Date;

public class StampTime {

	public Date lowerBound;
	public Date upperBound;

	public StampTime() {
	}

	public StampTime(Date lowerBound, Date upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
}