package com.tcc2008.services;

import java.sql.Time;
import java.util.Date;
import java.util.Vector;

import net.java.dev.marge.entity.ClientDevice;
import net.java.dev.marge.entity.Device;
import net.java.dev.marge.entity.config.ClientConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;
import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.ServiceDiscoverer;

import com.tcc2008.extend.Protocol;
import com.tcc2008.extend.Utility;

public class PackageTXService implements Runnable{
	
	private boolean started = true;
	private BTRFCOMMService serviceRecptor;
	private Vector<Protocol> queueTX;

	public PackageTXService(Vector<Protocol> queueTX, BTRFCOMMService serviceRecptor){
		this.queueTX = queueTX;
		this.serviceRecptor = serviceRecptor;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		while(started )
		{

			if (serviceRecptor.getDevice() != null) 
				serviceRecptor.getDevice().send(("Enviando mensagem as "+ new Time(System.currentTimeMillis()).toLocaleString()).getBytes());

			
			
			try { Thread.sleep(10000);}catch (Exception e) { Utility.Log(e.toString());}
		}
	}
	

	public void stop(){
		started = false;
	}

}
