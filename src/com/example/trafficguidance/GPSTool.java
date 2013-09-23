package com.example.trafficguidance;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

//import com.example.tenglee.R;
import com.friendlyarm.AndroidSDK.HardwareControler;

public class GPSTool {
	int serial_fd = 0;
	int serial_fd_4 = 0;

	int count = 0;
	byte[] buf = new byte[500];
	byte[] revBuf = new byte[21];
	byte[] sendGpsBuf = new byte[21];
	GPSDATA gpsdata = new GPSDATA();
	public List<GPSDATA> list = new ArrayList<GPSDATA>();
	

	class GPSDATA {
		public float currentTime;
		public float latitude;
		public float longitude;
		public float speed;

		public GPSDATA() {
		}

		public GPSDATA(float currentTime, float latitude, float longitude,
				float speed) {
			this.currentTime = currentTime;
			this.latitude = latitude;
			this.longitude = longitude;
			this.speed = speed;
		}

		public GPSDATA(int currentTime, int latitude, int longitude, int speed) {
			this.currentTime = Float.intBitsToFloat(currentTime);
			this.latitude = Float.intBitsToFloat(latitude);
			this.longitude = Float.intBitsToFloat(longitude);
			this.speed = Float.intBitsToFloat(speed);
		}
	}
	
	public GPSTool(){
		serial_fd = HardwareControler.openSerialPort("/dev/s3c2410_serial2", 9600, 8, 1);
		serial_fd_4 = HardwareControler.openSerialPort("/dev/s3c2410_serial3", 115200, 8, 1);
	}
	/**
	 * rev eaqual send
	 */
	public boolean isEqual(byte[] a, byte[] b){
		return (a[2] == b[2])&&(a[3] == b[3]);
	}
	/**
	 * 判断是否有危险
	 * @return boolean
	 */
	public boolean isDangerous(){
//		sendGPS();//发送gps数据给路测节点
		simulationGPSData();
		readZigBee();//收路测发来其他车辆的GPS数据（包括车速）
		Log.i("receive gps", "list size is "+list.size());
		if(list.size()>0){
			Log.e("receive gps", "a car in dangerous");
		}else{
			Log.e("receive gps", "be safe");
		}
		return list.size()>0;
	}
	/**
	 * 假数据
	 */
	public void simulationGPSData(){
		sendGpsBuf[0] = (byte) 0xfe;// System.out.println(sendGpsBuf[0]);
		sendGpsBuf[1] = 21;// System.out.println(sendGpsBuf[1]);
		sendGpsBuf[2] = 17;
		sendGpsBuf[3] = 17;
		int n = HardwareControler.write(serial_fd_4, sendGpsBuf); // 车载发送到zigbee
		if(n>0)
			Log.e("receive gps", "send success");
	}
	/**
	 * read ZIG Bee
	 */
	public void readZigBee(){
		int selectResult = 0;
		int curtime = 0;
		int latitude = 0;
		int longitude = 0;
		int speed = 0;
		selectResult = HardwareControler.select(serial_fd_4, 0, 0);
		Log.e("select result is ", " "+selectResult);
		if (1 == selectResult) {
			for(int i = 0; i < 10; i++){
				if (HardwareControler.read(serial_fd_4, revBuf,revBuf.length) == 0)break;
				Log.e("receive gps", hexString(revBuf)+"|");
				if(revBuf[0] == -2){//FE
					if(isEqual(this.revBuf,this.sendGpsBuf)){
						Log.i("receive gps", "receive yourself info");
					}else{
						curtime = (revBuf[4] << 24 & 0xff000000 | revBuf[5] << 16 & 0xff0000 | revBuf[6] << 8 & 0xff00 | revBuf[7] & 0xff);
						latitude = (revBuf[8] << 24 & 0xff000000 | revBuf[9] << 16 & 0xff0000 | revBuf[10] << 8 & 0xff00 | revBuf[11] & 0xff);
						longitude = (revBuf[12] << 24 & 0xff000000 | revBuf[13] << 16 & 0xff0000 | revBuf[14] << 8 & 0xff00 | revBuf[15] & 0xff);
						speed = (revBuf[16] << 24 & 0xff000000 | revBuf[17] << 16 & 0xff0000 | revBuf[18] << 8 & 0xff00 | revBuf[19] & 0xff);
						GPSDATA g = new GPSDATA(curtime, latitude, longitude, speed);
						list.add(g);
						Log.e("receive gps", "a car in dangerous");
					}
				}else if(revBuf[0] == 0xfb){//FB
					Log.e("rev msg", hexString(revBuf));
				}
			}
		}		
	}
	public void sendGPS(){
		String GPSString = new String();
		int n = 0;
		int selectResult = 0;
		selectResult = HardwareControler.select(serial_fd, 0, 0);
		Log.e("gps result", selectResult+"");
		if (1 == selectResult) {
			while (true) {
				if ((n = HardwareControler.read(serial_fd, buf, buf.length)) == 0) {
					break;
				}
				String temString = new String(buf, 0, n);
				GPSString = GPSString + temString;
				if(temString.contains("$GPRMC"))
					break;
			}
			sendGPSData(GPSString);
		}		
	}
	public void sendGPSData(String GPSString) {
		String[] splitString;
		int n = 0;
		splitString = GPSString.split("\\,");

		for (int k = 0; k < splitString.length; k++) {
			if (splitString[k].contains("$GPRMC")) {
				if (splitString[k + 2].contains("V")) {
					gpsdata.speed = 0.0f;
				} else {
					gpsdata.speed = Float.parseFloat(splitString[k + 7]);
				}
				gpsdata.currentTime = Float.parseFloat(splitString[k + 1]);
				gpsdata.latitude = Float.parseFloat(splitString[k + 3]);
				gpsdata.longitude = Float.parseFloat(splitString[k + 5]);
				System.out.println(gpsdata.currentTime);
				gpsdata.latitude = 33.33330f;
				int currentTime = Float.floatToIntBits(gpsdata.currentTime);
				int latitude = Float.floatToIntBits(gpsdata.latitude);
				int longitude = Float.floatToIntBits(gpsdata.longitude);
				int speed = Float.floatToIntBits(gpsdata.speed);

				sendGpsBuf[0] = (byte) 0xfe;// System.out.println(sendGpsBuf[0]);
				sendGpsBuf[1] = 21;// System.out.println(sendGpsBuf[1]);
				sendGpsBuf[2] = 17;
				sendGpsBuf[3] = 17;
				sendGpsBuf[4] = (byte) (currentTime >> 24 & 0xff);
				sendGpsBuf[5] = (byte) (currentTime >> 16 & 0xff);
				sendGpsBuf[6] = (byte) (currentTime >> 8 & 0xff);
				sendGpsBuf[7] = (byte) (currentTime & 0xff);

				sendGpsBuf[8] = (byte) (latitude >> 24 & 0xff);
				sendGpsBuf[9] = (byte) (latitude >> 16 & 0xff);
				sendGpsBuf[10] = (byte) (latitude >> 8 & 0xff);
				sendGpsBuf[11] = (byte) (latitude & 0xff);

				sendGpsBuf[12] = (byte) (longitude >> 24 & 0xff);
				sendGpsBuf[13] = (byte) (longitude >> 16 & 0xff);
				sendGpsBuf[14] = (byte) (longitude >> 8 & 0xff);
				sendGpsBuf[15] = (byte) (longitude & 0xff);

				sendGpsBuf[16] = (byte) (speed >> 24 & 0xff);
				sendGpsBuf[17] = (byte) (speed >> 16 & 0xff);
				sendGpsBuf[18] = (byte) (speed >> 8 & 0xff);
				sendGpsBuf[19] = (byte) (speed & 0xff);
				sendGpsBuf[20] = 0;
				for (int i = 0; i < 16; i++)
					sendGpsBuf[20] += sendGpsBuf[i + 4];
				Log.e("lhl send gps", hexString(sendGpsBuf));
				n = HardwareControler.write(serial_fd_4, sendGpsBuf); // 车载发送到zigbee
				break;
			}
		}
	}
/************************************************old************************************************************/
	public void start() {
		serial_fd = HardwareControler.openSerialPort("/dev/s3c2410_serial2", 9600, 8, 1);
		serial_fd_4 = HardwareControler.openSerialPort("/dev/s3c2410_serial3", 115200, 8, 1);
		Log.e("serial_fd_4", " "+serial_fd_4);
		Log.e("serial_fd", " "+serial_fd);
		Thread readZigbeeThread = new Thread(rZigbee);
		readZigbeeThread.start();// 接收路测发来其他车辆的GPS数据
		
		Thread readGPSThread = new Thread(rGPS);
		readGPSThread.start();// 得到本车GPS并发送给路测zigbee
	}

	Runnable rGPS = new Runnable() {

		@Override
		public void run() {
			while (true) {
				Log.e("I am read gps", "gps");
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendGPSToRoad();
			}
		}
	};
	// ------------------------------------------------------------------------------
	Runnable rZigbee = new Runnable() {

		@Override
		public void run() {
			while (true) {
				Log.e("I am receiving","receiving");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ReadZigbeeSerial();// 接收路测发来其他车辆的GPS数据
			}
		}
	};

	/**
	 * 收路测发来其他车辆的GPS数据（包括车速）
	 * 通过接收来的数据进行判断是否有危险
	 * 在地图上显示周围车辆的位置
	 */
	public void ReadZigbeeSerial() {
		int n = 0;
		int selectResult = 0;
		int k = 0;
		int curtime = 0;
		int latitude = 0;
		int longitude = 0;
		int speed = 0;
		String zigbeeRev = new String();

		selectResult = HardwareControler.select(serial_fd_4, 2, 1000);
//		selectResult = HardwareControler.select(serial_fd_4, 0, 0);
		Log.e("select result is ", " "+selectResult);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (1 == selectResult) {
			while (true) {
				
				if ((n = HardwareControler.read(serial_fd_4, revBuf,
						revBuf.length)) == 0) {
					break;
				}

				System.out.println(n);

				curtime = (revBuf[4] << 24 & 0xff000000 | revBuf[5] << 16
						& 0xff0000 | revBuf[6] << 8 & 0xff00 | revBuf[7] & 0xff);
				latitude = (revBuf[8] << 24 & 0xff000000 | revBuf[9] << 16
						& 0xff0000 | revBuf[10] << 8 & 0xff00 | revBuf[11] & 0xff);
				longitude = (revBuf[12] << 24 & 0xff000000 | revBuf[13] << 16
						& 0xff0000 | revBuf[14] << 8 & 0xff00 | revBuf[15] & 0xff);
				speed = (revBuf[16] << 24 & 0xff000000 | revBuf[17] << 16
						& 0xff0000 | revBuf[18] << 8 & 0xff00 | revBuf[19] & 0xff);

				System.out.println("--------------Read GPS 数据-----------------");
				System.out.println(revBuf.toString());
//				System.out.print(revBuf[0]);
//				System.out.print(revBuf[1]);
//				System.out.print(revBuf[2]);
//				System.out.println(revBuf[3]);
				System.out.print(Float.intBitsToFloat(curtime));
				System.out.print(Float.intBitsToFloat(latitude));
				System.out.print(Float.intBitsToFloat(longitude));
				System.out.println(Float.intBitsToFloat(speed));

				String temString = new String(revBuf, 0, n);
				zigbeeRev = zigbeeRev + temString;
				Log.e("receive gps", hexString(revBuf)+"|");
				System.out.println(zigbeeRev);
				GPSDATA g = new GPSDATA(curtime, latitude, longitude, speed);
			}
		}
		// System.out.println(k);
	}

	// ------------------------------------------------------------------------------
	/**
	 * 读本机GPS数据
	 */
	public void sendGPSToRoad() {
		String GPSString = new String();

		int n = 0;
		int count = 0;
		int selectResult = 0;

		selectResult = HardwareControler.select(serial_fd, 0, 0);
		Log.e("gps result", selectResult+"");
//		try {
//			Thread.sleep(1300);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		if (1 == selectResult) {
			while (true) {
				if ((n = HardwareControler.read(serial_fd, buf, buf.length)) == 0) {
					break;
				}
				String temString = new String(buf, 0, n);
				Log.e("gps data", temString+"|");
				// System.out.println(n);
				GPSString = GPSString + temString;
				count += n;
			}

			System.out.println("---------本车GPS数据----------" + GPSString);

			if (count == 280 || count == 328) {
				getTheGPSdate(GPSString);
			}

		}
	}

	// --------------------------------------------------------------
	/**
	 * 获取本车GPS数据 发送到zigbee
	 * 
	 * @param GPSString
	 */
	public GPSDATA getTheGPSdate(String GPSString) {
		String[] splitString;
		int n = 0;
		splitString = GPSString.split("\\,");

		for (int k = 0; k < splitString.length; k++) {
			if (splitString[k].contains("$GPRMC")) {
				if (splitString[k + 2].contains("V")) {
					gpsdata.speed = 0.0f;
				} else {
					gpsdata.speed = Float.parseFloat(splitString[k + 7]);
				}
				gpsdata.currentTime = Float.parseFloat(splitString[k + 1]);
				gpsdata.latitude = Float.parseFloat(splitString[k + 3]);
				gpsdata.longitude = Float.parseFloat(splitString[k + 5]);
				System.out.println(gpsdata.currentTime);
				gpsdata.latitude = 33.33330f;
				int currentTime = Float.floatToIntBits(gpsdata.currentTime);
				int latitude = Float.floatToIntBits(gpsdata.latitude);
				int longitude = Float.floatToIntBits(gpsdata.longitude);
				int speed = Float.floatToIntBits(gpsdata.speed);

				sendGpsBuf[0] = (byte) 0xfe;// System.out.println(sendGpsBuf[0]);
				sendGpsBuf[1] = 17;// System.out.println(sendGpsBuf[1]);
				sendGpsBuf[2] = 0;
				sendGpsBuf[3] = 1;
				sendGpsBuf[4] = (byte) (currentTime >> 24 & 0xff);
				sendGpsBuf[5] = (byte) (currentTime >> 16 & 0xff);
				sendGpsBuf[6] = (byte) (currentTime >> 8 & 0xff);
				sendGpsBuf[7] = (byte) (currentTime & 0xff);
//				System.out.println(sendGpsBuf[4]);
//				System.out.println(sendGpsBuf[5]);
//				System.out.println(sendGpsBuf[6]);
//				System.out.println(sendGpsBuf[7]);

				sendGpsBuf[8] = (byte) (latitude >> 24 & 0xff);
				sendGpsBuf[9] = (byte) (latitude >> 16 & 0xff);
				sendGpsBuf[10] = (byte) (latitude >> 8 & 0xff);
				sendGpsBuf[11] = (byte) (latitude & 0xff);

				sendGpsBuf[12] = (byte) (longitude >> 24 & 0xff);
				sendGpsBuf[13] = (byte) (longitude >> 16 & 0xff);
				sendGpsBuf[14] = (byte) (longitude >> 8 & 0xff);
				sendGpsBuf[15] = (byte) (longitude & 0xff);

				sendGpsBuf[16] = (byte) (speed >> 24 & 0xff);
				sendGpsBuf[17] = (byte) (speed >> 16 & 0xff);
				sendGpsBuf[18] = (byte) (speed >> 8 & 0xff);
				sendGpsBuf[19] = (byte) (speed & 0xff);
				sendGpsBuf[20] = 0;
				for (int i = 0; i < 16; i++)
					sendGpsBuf[20] += sendGpsBuf[i + 4];
				Log.e("lhl send gps", hexString(sendGpsBuf));
				n = HardwareControler.write(serial_fd_4, sendGpsBuf); // 车载发送到zigbee
				break;
			}
		}
		return gpsdata;
	}
    public static String hexString(byte[] b) {
		StringBuffer s = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            s.append(hex.toUpperCase()).append(" ");
        }
        return s.toString();
    } 
}
