package com.tcc2008.application;

import javax.bluetooth.BluetoothStateException;

import com.tcc2008.services.*;

import net.java.dev.marge.entity.config.ClientConfiguration;
import net.java.dev.marge.entity.config.ServerConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;
import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.ServiceDiscoverer;

public class TestMain {

	
	public static void main(String[] args) {

//	RemoteDeviceDiscovery discovery = new RemoteDeviceDiscovery(new Object());
	
		javax.bluetooth.UUID u = new javax.bluetooth.UUID(4856657);
		System.out.println((char) Byte.decode("#5f").byteValue());
		
		
	}

}
