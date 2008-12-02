package com.tcc2008.application;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.bluetooth.BluetoothStateException;

import com.tcc2008.extend.Utility;
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
	
		//UUID u = UUID.fromString("a258aa44-e943-3a1c-8427-e93bf9d695e7");
		com.tcc2008.extend.UUID u = new com.tcc2008.extend.UUID("00000000000000000000000000000000");
		com.tcc2008.extend.UUID f = new com.tcc2008.extend.UUID(Utility.hexToBytes("a258aa44e9433a1c8427e93bf9d695e7"));
		
		
		System.out.println(u);
		System.out.println(f);
	}
	
	
}
