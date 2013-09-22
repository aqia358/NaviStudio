package com.friendlyarm.AndroidSDK;
import android.util.Log;

public class HardwareControler
{
	/* Serial Port */
	static public native int openSerialPort( String devName, long baud, int dataBits, int stopBits );
	
	/* LED */
	static public native int setLedState( int ledID, int ledState );
    
    /* PWM */
	static public native int PWMPlay(int frequency);
	static public native int PWMStop();
    
    /* ADC */
	static public native int readADC();
	
	/* I2C */
	static public native int openI2CDevice();
	static public native int writeByteDataToI2C(int fd, int pos, byte byteData);
	static public native int readByteDataFromI2C(int fd, int pos);
	
	/* ͨ�ýӿ� */
	static public native int write(int fd, byte[] data);
	static public native int read(int fd, byte[] buf, int len);
	static public native int select(int fd, int sec, int usec);
	static public native void close(int fd);
    
    static {
        try {
        	System.loadLibrary("friendlyarm-hardware");
        } catch (UnsatisfiedLinkError e) {
            Log.d("HardwareControler", "libfriendlyarm-hardware library not found!");
        }
    }
}