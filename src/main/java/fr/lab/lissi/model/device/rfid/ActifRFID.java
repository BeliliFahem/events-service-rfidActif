package fr.lab.lissi.model.device.rfid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

import javax.swing.JOptionPane;

import fr.lab.lissi.general.Constants;
import fr.lab.lissi.model.DataElement;
import fr.lab.lissi.model.response.ResponseData;
import fr.lab.lissi.model.response.StampTime;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ActifRFID extends Observable implements SerialPortEventListener, Runnable {
	SerialPort serialPort;

	/** The port we're normally going to use. */
	private static String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB1", // Linux
			"COM5", // Windows
	};
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 115200;
	Map<Integer, Date> lastTagsInformation = new HashMap<Integer, Date>();

	public void initialize() {

		// get port names from config file
		Properties props = new Properties();
		try {
			props.load(new FileReader(Constants.CONFIG_FILE_PATH));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PORT_NAMES = props.getProperty("portNames").trim().split(",");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName.trim())) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.enableReceiveTimeout(1000); // important pour eliminer l'erreur sur zero
													// buffer read
			serialPort.enableReceiveThreshold(0);
			System.out.println(serialPort.getDataBits());
			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream(), "iso-8859-15"));
			output = serialPort.getOutputStream();

			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			e.printStackTrace();
			// System.err.println(e.toString());
		}
	}

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		TAGInformation tag = null;

		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				if (input.ready()) {
					byte[] b = input.readLine().getBytes("iso-8859-15");
					if (b.length < 9)
						return;
					byte[] tagdata = null;
					for (int i = 0; i < b.length; i++) {
						if ((b[i] == 85) && (b.length - i > 30))
							if (b[i + 1] == 32) {
								tagdata = new byte[30];
								for (int j = 0; j < 29; j++) {
									tagdata[j] = b[i + 6 + j];
								}
								break;
							}

					}

					if (tagdata == null)
						return;

					if (tagdata.length == 30) {
						tag = new TAGInformation(tagdata);
						Date d = new Date();

						// System.out.println("====***********************====");
						// System.out.println("Tag ID  : " + tag.getTagID());
						// System.out.println("Tag RSSI  : " + tag.getRSSI());
						// System.out.println("====***********************====");

						// TODO

						if (tag.getAlarm() == 80) // => RSSI change
							return;
						if (lastTagsInformation.containsKey(tag.getTagID())) {

							if (d.getTime() - lastTagsInformation.get(tag.getTagID()).getTime() < 6000)
								return;
						}
						lastTagsInformation.put(tag.getTagID(), d);

						// String ID = tag.getTagID()+ " ";
						System.out.println("Tag ID  : " + tag.getTagID());
						System.out.println("Tag RSSI  : " + tag.getRSSI());
						System.out.println("Tag Mouvement Counter  : " + tag.getMovementCounter());
						System.out.println("Tag Alarm  : " + tag.getAlarm());
						System.out.println("\t_______________________");

						//#############################################
						
						Properties props = new Properties();
						try {
							props.load(new FileReader("config.properties"));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							//			e.printStackTrace();
							JOptionPane.showMessageDialog(null, e.getMessage(), "FileNotFoundException", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, e.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}

						// TODO notify observer
						ResponseData responseData = new ResponseData(UniqueDataIdentifier.RFID_TAG_ID);

						responseData.setDeviceType(UniqueDataIdentifier.RFID_TAG_ID.toString());

						// TODO verify StampTime
						responseData.setStampTime(new StampTime(new Date(tag.getTimesRead()), new Date()));

						ArrayList<DataElement> dataElements = new ArrayList<DataElement>();
						dataElements.add(
								new DataElement(UniqueDataIdentifier.RFID_TAG_ID.getDataType(),
								UniqueDataIdentifier.RFID_TAG_ID.getDataUnit(), 
								UniqueDataIdentifier.RFID_TAG_ID.getDataName(), 
								tag.getTagID() + "")
								);
						
						/*
						 * ajouter 
						 */
						
						dataElements.add(
								new DataElement(UniqueDataIdentifier.RFID_TAG_ID.getDataType(),
								UniqueDataIdentifier.RFID_TAG_ID.getDataUnit(), 
								"CurrentAgedPersonSpaceID", 
								props.getProperty("deviceLocation"))
								);
						
						dataElements.add(
								new DataElement(UniqueDataIdentifier.RFID_TAG_ID.getDataType(),
								UniqueDataIdentifier.RFID_TAG_ID.getDataUnit(), 
								"CurrentAgedPersonPositionX", 
								props.getProperty("CurrentAgedPersonPositionX"))
								);
						
						dataElements.add(
								new DataElement(UniqueDataIdentifier.RFID_TAG_ID.getDataType(),
								UniqueDataIdentifier.RFID_TAG_ID.getDataUnit(), 
								"CurrentAgedPersonPositionY", 
								props.getProperty("CurrentAgedPersonPositionY"))
								);
						
						responseData.setDataList(dataElements);

						setChanged();
						notifyObservers(responseData);

						// enregistrement dans un fichier csv
						// SaveOnCSVFile save = new SaveOnCSVFile();
						// save.SaveRFIDActifOnCSVFile(String .valueOf(tag.getTagID()),"RX50",String
						// .valueOf(tag.getRSSI()),String .valueOf(tag.getMovementCounter()));
						//

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				// System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

	public static ActifRFID getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private final static ActifRFID INSTANCE = new ActifRFID();
	}

	@Override
	public void run() {
		System.out.println("Run " + this.getClass().getSimpleName());
		ActifRFID RFID = getInstance();
		RFID.initialize();
	}

	// public static void main(String[] args) throws Exception {
	// SerialForActifRFID RFID = new SerialForActifRFID();
	// RFID.initialize();
	//
	// }
}