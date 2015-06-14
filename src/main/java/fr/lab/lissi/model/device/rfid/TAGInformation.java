package fr.lab.lissi.model.device.rfid;

import java.security.InvalidParameterException;
import java.util.Formatter;

//import org.upec.lissi.ubistruct.rfid.wavetrend.data.LRX400Data;

/**
 * Representation of a <i>tag information</i>, <i>i.e.</i> all the data
 * contained in the datagram received from the reader.
 * 
 * @author BOUZNAD Sofiane
 * 
 */
public class TAGInformation extends RX50Data {

	/**
	 * Size of a tag datagram.
	 */
	public static final int DATA_SIZE = 30;

	private byte timesRead;
//	private byte firmwareVersion;
//	private long age;
//	private int siteCode;
	private int movementCounter;
	private int tagID;
	private byte type;
	private int rssi;
	private int alarm;


	@Override
	protected int storeData(byte[] buffer) {
		data = new byte[DATA_SIZE];
		int length = Math.min(DATA_SIZE, buffer.length);
		for (int i = 0; i < length; i++)
			data[i] = buffer[i];

		return length;
	}

	/**
	 * Creates a {@link TAGInformation} from a byte array.
	 * 
	 * @param buffer
	 *            a byte array supposed to start with a tag datagram.
	 * @throws InvalidParameterException
	 *             if: <li><code>buffer</code> is <code>null</code> <li>
	 *             <code>buffer</code> is smaller than
	 *             {@link TAGInformation#DATA_SIZE} bytes <li>
	 *             <code>buffer</code> do not contains a tag datagram at its
	 *             beginning
	 */
	public TAGInformation(byte[] buffer) throws InvalidParameterException {
		/*
		 * buffer must contain data...
		 */
		if (buffer == null)
			throw new InvalidParameterException("buffer cannot be null.");

		/*
		 * buffer must a least contain a datagram
		 */
		if (buffer.length < DATA_SIZE)
			throw new InvalidParameterException("buffer must be " + DATA_SIZE
					+ " bytes long.");

		/*
		 * localy copy the buffer
		 */
		storeData(buffer);

		/*
		 * validate the buffer
		 */
//		if (!testCaracteristicBytes())
//			throw new InvalidParameterException("invalid data");

		/*
		 * extract data
		 */
		extractTimesRead();
	//	extractFirmwareVersion();
	//	extractAge();
	//	extractSiteCode();
		extractTagID();
		extractType();
		extractRSSI();
    	extractMovementCounter();
		extractAlarm();
	}

	

	/**
	 * unknown effect
	 * 
	 * @return unknown value
	 */
	public byte getTimesRead() {
		return timesRead;
	}
	public int getAlarm() {
		return alarm;
	}

	/**
	 * Returns the firmware version of the tag
	 * 
	 * @return the value encoded on a {@link Byte}
	 */
	/*public byte getFirmwareVersion() {
		return firmwareVersion;
	}*/

	/**
	 * Returns the age of the tag: the number of times it emitted.
	 * 
	 * @return the age as an integer
	 */
	/*public int getAge() {
		return (int) age;
	}*/

	/**
	 * Returns the site code of the tag (the reseller's code I guess)
	 * 
	 * @return the site code as an integer;
	 */
	/*public int getSiteCode() {
		return (int) siteCode;
	}*/

	/**
	 * Returns the ID of the tag.
	 * 
	 * @return the ID of the tag as an integer.
	 */
	public int getTagID() {
		return (int) tagID;
	}

	/**
	 * Returns the type of the tag.
	 * 
	 * @return the encoded value of the type on a {@link Byte}
	 */
	public byte getType() {
		return type;
	}

	/**
	 * Returns the RSSI value of the last reading for this tag.
	 * 
	 * @return the RSSI value as an integer - expected range: 0-255
	 */
	public int getRSSI() {
		/*
		 * direct byte to int conversion considers bytes as signed int
		 */
		return rssi;
	}

	private void extractTimesRead() {
		timesRead = data[3];
	}

	/*
	private void extractFirmwareVersion() {
		firmwareVersion = data[4];
	}*/

/*	private void extractAge() {
		age = 0;
//		for (int i = 0; i < 4; i++) {
//			age += data[10 + i];
//			if (i != 3)
//				age <<= 8;
//		}

		byte[] tab = { data[10], data[11], data[12], data[13] };
		age = byteArrayToInt(tab);
	} */
	private void extractMovementCounter() {
		byte[] array = { data[8] };
		movementCounter = (int) RX50Data.byteArrayToInt(array);		
	}
	
	
	private void extractAlarm() {
		byte[] array = { data[25] };
		alarm = (int) RX50Data.byteArrayToInt(array);		
	}

	/*
	private void extractSiteCode() {

		byte[] tab = { data[14], data[15], data[18] };

		siteCode = (int) byteArrayToInt(tab);
	}
*/
	
    private void extractTagID() {

		byte[] tab = { data[16], data[17], data[18], data[19] };

		tagID = (int) byteArrayToInt(tab);
	}

	private void extractType() {
		type = data[23];
	}
	
	private void extractRSSI() {
		byte[] array = { data[22] };
		rssi = (int) RX50Data.byteArrayToInt(array);
	}

	private boolean testCaracteristicBytes() {
		return testCaracteristicBytes(data);
	}

	private static boolean testCaracteristicBytes(byte[] buffer) {
		for (int i = 0; i < 4; i++) {
			if (buffer[8 * i] != 0x06 | buffer[8 * i + 1] != i + 1)
				return false;
		}
		return true;
	}

	/**
	 * tests the buffer format.
	 * 
	 * @param buffer
	 * @return <li><code>true</code> if the buffer contains at list one tag's
	 *         datagram located at the start of the buffer <li>
	 *         <code>false</code> otherwise
	 */
	public static boolean isValidBuffer(byte[] buffer) {
		if (buffer == null)
			return false;

		if (buffer.length < DATA_SIZE)
			return false;

		return testCaracteristicBytes(buffer);
	}

	@Override
	public String toString() {
		return getTagID() + ": " + getRSSI() + "\t" + packBytes();
	}

	private String packBytes() {
		// String byteString = "0x%02X";
		String byteString = "%02X";
		String header = byteString + " " + byteString + ":  ";
		String bytes6 = byteString + " " + byteString + " " + byteString + " "
				+ byteString + " " + byteString + " " + byteString;
		return new Formatter().format(
				header + bytes6 + "  -  " + header + bytes6 + "  -  " + header
						+ bytes6 + "  -  " + header + bytes6, data[0], data[1],
				data[2], data[3], data[4], data[5], data[6], data[7], data[8],
				data[9], data[10], data[11], data[12], data[13], data[14],
				data[15], data[16], data[17], data[18], data[19], data[20],
				data[21], data[22], data[23], data[24], data[25], data[26],
				data[27], data[28], data[29], data[30], data[31]).toString();
	}

	public int getMovementCounter() {
		return movementCounter;
	}

	
	
}
