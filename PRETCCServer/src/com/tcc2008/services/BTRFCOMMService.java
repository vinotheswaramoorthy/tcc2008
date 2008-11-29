package com.tcc2008.services;

import java.io.IOException;
import java.util.Vector; 

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;

import net.java.dev.marge.communication.CommunicationListener;
import net.java.dev.marge.communication.ConnectionListener;
import net.java.dev.marge.entity.Device;
import net.java.dev.marge.entity.ServerDevice;
import net.java.dev.marge.entity.config.ServerConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;


public class BTRFCOMMService implements ConnectionListener, CommunicationListener {
	private Device device;
	public Vector<byte[]> buffer;
	
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
			device.startListening();
			this.device = device;
			
			try {
				System.out.println("Estabilizou conexão com: "+ remote.getFriendlyName(false));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	

	public Device getDevice() {
		return device;
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
