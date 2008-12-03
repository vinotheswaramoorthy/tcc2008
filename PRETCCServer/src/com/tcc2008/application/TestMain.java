package com.tcc2008.application;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.bluetooth.BluetoothStateException;

import com.tcc2008.extend.Dictionary;
import com.tcc2008.extend.Util;
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
		
////		
//		
		System.out.println(u.toHexString());
//		System.out.println(f);
		System.out.println(new String(Utility.genPackageGetUID("emerson", "123")));
		System.out.println(new String(Utility.genPackageToSend("90909090909090909090909090909090", "90909090909090909090909090909091", "90909090909090909090909090909000", true, "Olá tudo bem!".getBytes())));
		System.out.println(new String(Utility.genPackageToSend("90909090909090909090909090909090", "90909090909090909090909090909091", "90909090909090909090909090909000", false, "Olá tudo bem!".getBytes())));
		System.out.println(new String(Utility.genPackageUpdateLocation("90909090909090909090909090909090", "90909090909090909090909090909000")));
	}
	
	
}
