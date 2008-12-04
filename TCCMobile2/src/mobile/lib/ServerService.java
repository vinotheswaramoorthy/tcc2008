package mobile.lib;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import mobile.lib.PackQueue.PackDeliver;

import com.extend.*;
import net.java.dev.marge.communication.CommunicationListener;
import net.java.dev.marge.entity.Device;
import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.InquiryListener;
import net.java.dev.marge.inquiry.ServiceDiscoverer;
import net.java.dev.marge.inquiry.ServiceSearchListener;

public class ServerService  implements Runnable{

	private Device device;
	private boolean done = false;
	
	public void init(){		
		
		
		//Poderia fazer um INQUIRE, procurando pelo servidor
		
		Thread thread = new Thread(this);
		thread.start();				
	}
	
	public void stop()
	{
		done = true;
	}
	
	private String myUUID;
	
	public void run() {
		try
		{	
			//Recuperar o UUID
			byte[] uuid = Utility.genPackageGetUID("emerson", "123"); 
			
			device.send(uuid);
			
			DeviceDiscoverer.getInstance().startInquiryGIAC(new ServerSearch());
			
			
			while( !done )
			{
				
			}			
		} 
		catch (Exception e) {
			e.printStackTrace();
			Util.Log(e.getClass().getName()+" "+e.getMessage());
		}
		Util.Log("PackQueue thread exit");
	}
	
	class ServerSearch implements InquiryListener{

		public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
			//net.java.dev.marge.util.UUIDGenerator.generate(name)
			
			javax.bluetooth.UUID[] uids = new javax.bluetooth.UUID[1];
			uids[0] = new javax.bluetooth.UUID("69696969696969696969696969696969",false);
			try {
				ServiceDiscoverer.getInstance().startSearch(uids, arg0,new ServerSearchService());
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
		}

		public void inquiryCompleted(RemoteDevice[] arg0) {
			
		}

		public void inquiryError() {
			
		}
		
	}
	
	class ServerSearchService implements ServiceSearchListener{

		public void deviceNotReachable() {
			// TODO Auto-generated method stub
			
		}

		public void serviceSearchCompleted(RemoteDevice arg0,
				ServiceRecord[] arg1) {
			// TODO Auto-generated method stub
			
		}

		public void serviceSearchError() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class ServerPoint implements CommunicationListener{

		public void errorOnReceiving(IOException e) {
			e.printStackTrace();			
		}

		public void errorOnSending(IOException e) {
			e.printStackTrace();
		}

		public void receiveMessage(byte[] receivedString) {
	    	Protocol[] protos = Utility.genProtocols(receivedString);
	    	for (int i = 0; i < protos.length; i++) {
//	            this.insert(1, new StringItem("Recebido: ", protos[i].toString() ));
//	            if(uid == null && protos[i].getCommand() == (byte) Dictionary.CMD_RESPUUID){
//	            	uid = protos[i].getIDFrom();
//	            	this.insert(0, new StringItem("Meu UID: ",uid.toHexString()));
//	            }
	      }
		}
	
		
	}

}
