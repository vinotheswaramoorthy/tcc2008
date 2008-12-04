package mobile.lib;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import mobile.midlet.MainMID;
import net.java.dev.marge.communication.CommunicationListener;
import net.java.dev.marge.entity.ClientDevice;
import net.java.dev.marge.entity.config.ClientConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;
import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.InquiryListener;
import net.java.dev.marge.inquiry.ServiceDiscoverer;
import net.java.dev.marge.inquiry.ServiceSearchListener;

import com.extend.Dictionary;
import com.extend.Protocol;
import com.extend.Utility;

public class ServerService  implements Runnable{

	private ClientDevice device;	
	private CommunicationListener serverComm;
	private CommunicationFactory factory;
	private boolean done = false;
	private Vector pktsToSend;
	private com.extend.UUID myUID = null;
	private com.extend.UUID appUID = null;

	private MainMID midlet;
	
	public void init(MainMID mid){
		
		this.midlet = mid;
		
		Util.Log("Inicializando servidor.");
		serverComm = new ServerCOMM(this);
		
		factory = new RFCOMMCommunicationFactory();
		
		Thread thread = new Thread(this);
		thread.start();				
	}
	
	public void stop()
	{
		done = true;
	}
	


	public CommunicationListener getServerCOMM(){
		return serverComm;
	}		
	public CommunicationFactory getFactory(){
		return factory;
	}	
	
	public void startServer(){		

		Thread t1 = new Thread(this);
		t1.start();
	}
	public synchronized void putDevice(ClientDevice cDevice){
		this.device = cDevice;
		this.device.startListening();
		try {
			Util.Log("Server " + device.getDeviceName() +" listening.");
		} catch (BluetoothStateException e) {
			Util.Log("Servidor X listening...");
		}
		this.notify();
	}
	
	private Protocol[] protocolos;
	public synchronized void receiveProto(Protocol[] protos){
		protocolos = protos;
		this.notify();
	}

	
	
	public synchronized void sendMessage(String msg){
		if( device==null )return;
		if( myUID == null )return;
		if( appUID==null) return;
		
		if( pktsToSend == null ) pktsToSend = new Vector();
		
		pktsToSend.addElement( 
				Utility.genPackageToSend(
						myUID.toHexString(), //FROM
						appUID.toHexString(),//Application							
						false,
						msg.getBytes()
						) 
				);
	}
	
	
	public void run() {
		

		try {			
			ServerListener sl = new ServerListener(this);
			DeviceDiscoverer.getInstance().startInquiryGIAC( sl );
		
			while( !sl.isFinished() ){
				synchronized(this){
					this.wait(5000);
				}
			}
			
			ServerServiceSearch ss = new ServerServiceSearch(this);
			if( sl.getDevices().size()==0) 
				Util.Log("NENHUM SERVIDOR ENCONTRADO!");			
			for(int i=0;i<sl.getDevices().size();i++){
				
				ServiceDiscoverer.getInstance().startSearch( 
						new UUID[]{new UUID("69696969696969696969696969696969",false)}, 
						(RemoteDevice) sl.getDevices().elementAt(i), ss);				
			}			
					
			synchronized(this){
				this.wait();
			}			
			
			if( device!=null){				
				device.send(
						Utility.genPackageGetUID("ivan", "123")
						);				
			}				
			
			//Aguarda resposta de UID
			synchronized(this){
				this.wait();
			}			
			
			if(protocolos!=null){

				for (int i = 0; i < protocolos.length; i++) {					
					if( protocolos[i].getCommand()==(byte) Dictionary.CMD_RESPUUID && myUID == null){
					  myUID = protocolos[i].getIDFrom();
					  Util.Log(" Login no servidor:"+myUID.toHexString());
					}
					
				}							
			}
			
			if( myUID == null ) done = true;
			appUID = new com.extend.UUID("27272727272727272727272727272727");
			while(!done){

				//Atualiza localização do celular!
				Util.Log(" Servidor em atualização...");
				device.send(Utility.genPackageUpdateLocation(myUID.toHexString(), appUID.toHexString() ));

				//Envia possiveis pacotes... 
				if( pktsToSend!=null && pktsToSend.size()>0){
					while(!pktsToSend.isEmpty()){
						device.send( (byte[])pktsToSend.firstElement() );
						pktsToSend.removeElementAt(0);
					}
				}
				
				//Aguarda recebimento de protocolos
				synchronized(this){
					this.wait(30000);
				}
				
				if( protocolos!=null && protocolos.length>0){
					String msg = "";
			    	for (int i = 0; i < protocolos.length; i++) {

			    		
			    		if(protocolos[i].getCommand() == (byte) Dictionary.CMD_SEND){
			    			
			    			ProtoPackage pp = null;
			    			try{
			    				pp = ProtoPackage.getProtoPackage(protocolos[i].getData());			    				
			    			}
			    			catch(Exception ex){}
			    			
			    			if( pp!=null){			    				
			    				this.midlet.handleAction(pp.application, null , pp);			    				
			    			}
			    			
			    		}
			    		
			    		msg = msg + " | " + i + protocolos[i].toString();
	    	
					}			    	
			    	protocolos = null;
				}
				
				Thread.yield();
			}
			
		} catch (BluetoothStateException e) {
			Util.Log("BSEx: "+ e.getMessage());
		} catch (InterruptedException e) {
			Util.Log("IEx: "+e.getMessage());
		} catch (Exception e){
			Util.Log("Ex: "+ e.getMessage());
		}
		finally{
			Util.Log("SERVIDO ENCERRADO.");
		}
		
	}
	
	class ServerListener implements InquiryListener{

		private ServerService parent;
		private boolean finished = false;
		public boolean isFinished() {
			return finished;
		}
		public ServerListener(ServerService parent){
			this.parent = parent;
			deviceList = new Vector();
		}

		private Vector deviceList;
		public Vector getDevices(){
			return deviceList;
		}
		public void deviceDiscovered(RemoteDevice dev, DeviceClass arg1) {
			String devName = "";
			try {
				devName = dev.getFriendlyName(false);				
			} catch (IOException e) {
				//e.printStackTrace();
			}
		
			if( devName.toLowerCase().indexOf("ivanpc")>-1 || devName.toLowerCase().indexOf("nbemerson")>-1){
				deviceList.addElement(dev);
			}
		}
		
		public void inquiryCompleted(RemoteDevice[] devices) {
			finished = true;
		}

		public void inquiryError() {
			
		}	
	}
	
	public class ServerServiceSearch implements ServiceSearchListener{

		private ServerService parent;
		public ServerServiceSearch(ServerService parent){
			this.parent = parent;
			deviceList = new Vector();
		}
		
		public void deviceNotReachable() {			
			
		}
		
		private boolean finished = false;
		private Vector deviceList;

		public void serviceSearchCompleted(RemoteDevice arg0,
				ServiceRecord[] services) {	
			for (int i = 0; i < services.length; i++){
		        try {		        					        			       
		            ClientConfiguration config = new ClientConfiguration(services[i],
		                    parent.getServerCOMM());	            
		            ClientDevice clientDevice = parent.getFactory().connectToServer(config);
		            parent.putDevice(clientDevice);		            
		
		        } catch (IOException e) {
		            Util.Log("ServiceSearch Error: "+e.getMessage());
		        }
			}

			
		}

		public void serviceSearchError() {
			
		}
		
	}
	
	public class ServerCOMM implements CommunicationListener{

		private ServerService parent;
		public ServerCOMM(ServerService parent){
			this.parent = parent;
		}
		
		public void errorOnReceiving(IOException arg0) {
			
		}

		public void errorOnSending(IOException arg0) {
			
		}

		public void receiveMessage(byte[] msg) {
			parent.receiveProto(Utility.genProtocols(msg));
		}		
	}

}
