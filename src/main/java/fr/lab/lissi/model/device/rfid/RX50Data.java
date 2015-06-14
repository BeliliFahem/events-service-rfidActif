package fr.lab.lissi.model.device.rfid;

/**
 * Representation of data read from the L-RX400 usb reader.
 * @author BOUZNAD Sofiane
 *
 */
public abstract class RX50Data {
	
	/**
	 * A copy of the data read from the usb pipe and used to build the object.
	 */
	protected byte[] data = null;

	/**
	 * Method used to properly copy the buffer containing the binary data.
	 * @param buffer the byte array read from the usb pipe.
	 * @return the number of actually copied bytes.
	 */
	protected abstract int storeData(byte[] buffer);
	
	/**
	 * <b>Warning:</b> <u>low</u> indexed bytes of <code>b</code> <b>must</b>
	 * represent the <u>high</u> weight bytes of the number to transcode and
	 * vice versa.
	 * @param b a 4 bytes array to transcode into an integer value
	 * @return an integer represented by the unsigned value contained by
	 * <code>b</code>
	 */

	public static long byteArrayToInt(byte[] b) 
	{
		long value = 0;
        for (int i = 0; i < b.length; i++) {
            int shift = (b.length - 1 - i) * 8;
            value += (b[i]& 0x000000FF) << shift;
        }
        return value;

    }



}
