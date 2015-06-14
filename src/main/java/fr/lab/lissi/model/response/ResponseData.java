package fr.lab.lissi.model.response;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.Gson;

import fr.lab.lissi.general.Constants;
import fr.lab.lissi.model.DataElement;
import fr.lab.lissi.model.device.rfid.UniqueDataIdentifier;

/**
 * 
 * @author Fahem
 *
 */
@XmlRootElement
public class ResponseData {

	/*
	 * deviceID : nwkAddr for example.
	 */
	private String deviceID;

	/*
	 * deviceID : Kettle for example.
	 */
	private String deviceAliasName;

	private String deviceType;

	private String deviceLocation;

	private StampTime stampTime;

	private List<DataElement> dataList;

	/**
	 * this field let us to link a data to a sensor
	 */
	private transient UniqueDataIdentifier uniqueDataIdentifier;

	public ResponseData(UniqueDataIdentifier uniqueDataIdentifier) {
		this.uniqueDataIdentifier = uniqueDataIdentifier;
	}

	public ResponseData(String deviceID, String deviceAliasName, String deviceLocation,
			StampTime stampTime, List<DataElement> dataList) {
		this.deviceID = deviceID;
		this.deviceAliasName = deviceAliasName;
		this.stampTime = stampTime;
		this.dataList = dataList;
	}

	public ResponseData(String deviceID, String deviceAliasName, String deviceLocation) {
		this.deviceID = deviceID;
		this.deviceAliasName = deviceAliasName;
		this.deviceLocation = deviceLocation;
	}

	public ResponseData(StampTime stampTime, List<DataElement> dataList) {
		this.stampTime = stampTime;
		this.dataList = dataList;
		setParamsFromConfigFile();
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceAliasName() {
		return deviceAliasName;
	}

	public void setDeviceAliasName(String deviceAliasName) {
		this.deviceAliasName = deviceAliasName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceLocation() {
		return deviceLocation;
	}

	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}

	public StampTime getStampTime() {
		return stampTime;
	}

	public void setStampTime(StampTime stampTime) {
		this.stampTime = stampTime;
	}

	public List<DataElement> getDataList() {
		return dataList;
	}

	public void setDataList(List<DataElement> dataList) {
		this.dataList = dataList;
	}

	public UniqueDataIdentifier getUniqueDataIdentifier() {
		return uniqueDataIdentifier;
	}

	public void setUniqueDataIdentifier(UniqueDataIdentifier uniqueDataIdentifier) {
		this.uniqueDataIdentifier = uniqueDataIdentifier;
	}

	private void setParamsFromConfigFile() {
		Properties myProperties = new Properties();
		try {
			myProperties.load(new FileInputStream(Constants.APP_ROOT_PATH
					+ Constants.CONFIG_FILE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.deviceID = myProperties.getProperty("deviceID").trim();
		this.deviceLocation = myProperties.getProperty("deviceLocation").trim();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new Gson().toJson(this);
	}

}