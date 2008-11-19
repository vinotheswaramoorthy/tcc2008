import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;

import net.java.dev.marge.communication.*;
import net.java.dev.marge.entity.ServerDevice;
import net.java.dev.marge.entity.config.ServerConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;


public class BTRFCOMMService implements ConnectionListener, CommunicationListener {
	
	Vector<byte[]> buffer;
	
	public BTRFCOMMService(){
		buffer = new Vector<byte[]>();
	}
	
	public void startServer(){
		CommunicationFactory factory = new RFCOMMCommunicationFactory();
		ServerConfiguration config = new ServerConfiguration(this);
		config.setMaxNumberOfConnections(5);
		factory.waitClients(config, this);	
	}
	
	public void stopServer(){
		
		
	}
	
	
	
	public void connectionEstablished(ServerDevice device, RemoteDevice remote) {
//		try { 
//			System.out.println(remote.getBluetoothAddress());
//			Thread.sleep(1000);
			device.startListening();
//			device.setEnableBroadcast(true);
//			device.send("Welcome".getBytes());
//		} catch (InterruptedException ex) {
//			System.err.println(ex);
//			ex.printStackTrace();
//		}

	}
	

	public void errorOnConnection(IOException error) {
		System.out.println(error);		
	}

	public void errorOnReceiving(IOException error) {
		System.out.println(error);		
	}

	public void errorOnSending(IOException error) {
		System.out.println(error);		
	}


	public void receiveMessage(byte[] message) {		
		buffer.add(message);
	}

}
