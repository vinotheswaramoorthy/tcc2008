package com.tcc2008.services;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import com.tcc2008.extend.Utility;

import net.java.dev.marge.communication.ConnectionListener;
import net.java.dev.marge.inquiry.InquiryListener;
import net.java.dev.marge.inquiry.ServiceDiscoverer;
import net.java.dev.marge.inquiry.ServiceSearchListener;


public class MargeInquiryListener implements InquiryListener {

	private Vector<RemoteDevice> devices;
	private Object inquiryCompletedEvent;
	
	 public MargeInquiryListener(Object inquiryCompletedEvent, Vector<RemoteDevice> devices) {
		this.inquiryCompletedEvent = inquiryCompletedEvent;
		this.devices = devices;
		
	}

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
         System.out.println("MARGE Device " + btDevice.getBluetoothAddress() + " found");
         this.devices.add(btDevice);
         
         try {        	 
             System.out.println("     name " + btDevice.getFriendlyName(false));
         } catch (IOException cantGetDeviceName) {
         }
     }


	public void inquiryCompleted(RemoteDevice[] arg0) {
		System.out.println("Device Inquiry completed!");
        synchronized(inquiryCompletedEvent){
            inquiryCompletedEvent.notifyAll();
        }
        System.out.println("TERMINEI DE ENCONTRAR COM O MARGE!!  yes!!!!!!!!!!!"); 
        
        try {
			ServiceDiscoverer.getInstance().startSearch(devices.firstElement(), 
				      new MargeServiceSearchListener());
			
			
			
		} catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        
	}


	public void inquiryError() {
		// TODO Auto-generated method stub
		
	}
}
