package fr.lab.lissi.model.device.rfid;

/**
 * Unique identifier data.
 * There is additional information
 * 
 * @author Fahem
 *
 */
public enum UniqueDataIdentifier {

	/*
	 * On peux lier ça à un fichier de configuration pour que ça soit entièrement configurable
	 * utiliser les getter pour récupérer à partire d'une base de donnée
	 */

	RFID_TAG_ID("RFID", "Tag Id", "none", "LONG"),
	RFID_RSSI("RFID", "RSSI", "none", "FLOAT");
	

	private String deviceType = "";
	private String dataName = "";
	private String DataUnit = "";
	private String dataType = "";

	UniqueDataIdentifier(String deviceType, String dataName, String DataUnit, String dataType) {
		this.deviceType = deviceType;
		this.dataType = dataType;
		this.dataName = dataName;
		this.DataUnit = DataUnit;
		this.dataType = dataType;
	}

	public String getDataName() {
		return dataName;
	}

	public String getDataUnit() {
		return DataUnit;
	}

	public String getDataType() {
		return dataType;
	}

	public String getSensorType() {
		return deviceType;
	}

	public String getDeviceType() {
		return deviceType;
	}

}
