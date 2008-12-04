package mobile.lib;

import java.util.Vector;

public class Util {
 
	public static boolean enableLog = true;

	public static Vector msgs = new Vector(15);
	
	public static void Log(String msg){

		if( enableLog ){
			//System.out.println();
			System.out.println("Log: "+msg);
			if( msgs.size()>13) 
				msgs.removeElementAt(0);
			msgs.addElement(msg);
		}
	}
	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}

	public static String[] split(String original, String separator) {
		Vector nodes = new Vector();
		// Parse nodes into vector
		int index = original.indexOf(separator);
		while(index>=0) {
			nodes.addElement( original.substring(0, index) );
			original = original.substring(index+separator.length());
			index = original.indexOf(separator);
		}
		// Get the last node
		nodes.addElement( original );

		// Create splitted string array
		String[] result = new String[ nodes.size() ];
		if( nodes.size()>0 ) {
			for(int loop=0; loop<nodes.size(); loop++)
			{
				result[loop] = (String)nodes.elementAt(loop);
				//System.out.println(result[loop]);
			}

		}

		return result;
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
	
	public static boolean isPath(String url){
    	//verifica se nome passado termina com um separador
        if ((url.charAt(url.length() - 1) == '/') || (url.equals(".."))) 
        	return true;
        else
        	return false;
    }

}
