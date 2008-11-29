package com.tcc2008.services;

import java.util.Vector;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.RemoteDevice;

import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.InquiryListener;

import com.tcc2008.extend.Utility;

/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery implements Runnable{ 

    public static final Vector<RemoteDevice> devicesDiscovered = new Vector<RemoteDevice>();
    Object inquiryCompletedEvent ;
	
  
    public RemoteDeviceDiscovery (Object inquiryCompletedEvent){
    	this.inquiryCompletedEvent = inquiryCompletedEvent;       
    	    	
    	Thread t = new Thread(this);
    	t.start();
    }
    
    public void run(){
    	
    	while(true)
    	{
    		devicesDiscovered.clear();

	       synchronized(inquiryCompletedEvent) {
	          try {
	        	  
	        	  InquiryListener listener = new MargeInquiryListener(inquiryCompletedEvent, devicesDiscovered);  
	        	  DeviceDiscoverer.getInstance().startInquiry(DiscoveryAgent.GIAC,
						listener);
	        	  
	        	  
	        	  System.out.println("wait for device inquiry to complete...");
	        	  inquiryCompletedEvent.wait();
	        	  System.out.println(devicesDiscovered.size() + " device(s) found");
			
	          
	          } catch (Exception e) {
	        	  Utility.Log(e.getMessage());
	          }
	          
	       	}
	       
//	       	ServiceSearchListener services = new MargeServiceSearchListener();
//     	  	try {
//				ServiceDiscoverer.getInstance().startSearch((RemoteDevice)devices.firstElement(), 
//				      services);
//			} catch (BluetoothStateException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
	      
	       
	       try { Thread.sleep(30000);}catch (Exception e) { Utility.Log(e.getMessage());}
    	}
    }

}
