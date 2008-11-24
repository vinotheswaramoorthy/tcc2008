package mobile.lib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;


public class MobConfig {

	private static RecordStore rs;
	final private static String REC_STORE = "beehiveprofile";
	
	private static String name;
	private static String nickname = "";
	private static String picture;
	private static String description;
	private static String sexo;

	public MobConfig(){
		
	}
	
	public static String getName(){
		return name;
	}
	
	public static String getNickname(){
		return nickname;
	}
	
	public static String getPicture(){
		return picture;
	}
	
	public static String getDescription(){
		return description;
	}
	
	public static String getGender(){
		return sexo;
	}
	
	public static boolean reloadProfile()
	{
		boolean success = false;
	    try
	    {
	    	RMSOpen();
	    	if(rs.getNumRecords() > 0)
	    	{
		    	byte[] recData = new byte[50];
		    	ByteArrayInputStream strmBytes = new ByteArrayInputStream(recData);
		    	DataInputStream strmDataType = new DataInputStream(strmBytes);

		    	//System.out.println(rs.getNumRecords());
		    	rs.getRecord(1, recData, 0);
				
				name 		= strmDataType.readUTF();
				nickname 	= strmDataType.readUTF();
				picture 	= strmDataType.readUTF();
				description = strmDataType.readUTF();
				
				boolean isMan = strmDataType.readBoolean();
				boolean isGirl= strmDataType.readBoolean();
				
				if( isMan ) sexo = "M";
				else if( isGirl) sexo = "F";
				
		    	strmBytes.reset();
		    	strmBytes.close();
		    	strmDataType.close();
		    	success = true;
	    	}
     	}
     	catch (Exception e)
     	{
     		e.printStackTrace();
     	}
     	finally
     	{
     		RMSClose();
     	}
     	return success;
	}

	
	private static void RMSOpen() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
	{
		rs = RecordStore.openRecordStore(REC_STORE , true);
	}

	private static void RMSClose()
	{
		try {
			rs.closeRecordStore();
		} catch (RecordStoreNotOpenException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
}
