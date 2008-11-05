package mobile.lib;

public class Util {
 
	public static boolean enableLog = false;

	public static void Log(String msg){

		if( enableLog ){
			System.out.println();
			System.out.println("Loggin: "+msg);
		}
	}
	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}

	public static int byteToInt(byte[] buf){
		int i = 0;
		int pos = 0;
		i += unsignedByteToInt(buf[pos++]) << 24;
		i += unsignedByteToInt(buf[pos++]) << 16;
		i += unsignedByteToInt(buf[pos++]) << 8;
		i += unsignedByteToInt(buf[pos++]) << 0;
		return i;
	}

	public static byte[] intToFourBytes(int i, boolean bigEndian) {   
		if (bigEndian) {   
			byte[] data = new byte[4];   
			data[3] = (byte) (i & 0xFF);   
			data[2] = (byte) ((i >> 8) & 0xFF);   
			data[1] = (byte) ((i >> 16) & 0xFF);   
			data[0] = (byte) ((i >> 24) & 0xFF);   
			return data;   

		} else {   
			byte[] data = new byte[4];   
			data[0] = (byte) (i & 0xFF);   
			data[1] = (byte) ((i >> 8) & 0xFF);   
			data[2] = (byte) ((i >> 16) & 0xFF);   
			data[3] = (byte) ((i >> 24) & 0xFF);   
			return data;   
		}   
	}

}
